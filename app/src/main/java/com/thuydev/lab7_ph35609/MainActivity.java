package com.thuydev.lab7_ph35609;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    FirebaseFirestore database;
    Adapter_todolist adapter_todolist;
    RecyclerView rc_list;
    List<Todolist_DTO> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseFirestore.getInstance();


        EditText title = findViewById(R.id.edt_title);
        EditText content = findViewById(R.id.edt_content);
        EditText date = findViewById(R.id.edt_date);
        EditText type = findViewById(R.id.edt_type);
        rc_list = findViewById(R.id.rc_list);
        Button add = findViewById(R.id.btn_add);
        list = new ArrayList<>();
        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] mang = new String[]{
                        "Khó", "Trung bình", "Dễ"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Vui lòng chọn mức độ khó");
                builder.setItems(mang, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        type.setText(mang[which]);
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            }


        });

        database.collection("lab7-a082f").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot qr : queryDocumentSnapshots
                ) {
                    Todolist_DTO dto = new Todolist_DTO();
                    dto.setId(qr.getString("id"));
                    dto.setTitle(qr.getString("title"));
                    dto.setContent(qr.getString("content"));
                    dto.setDate(qr.getString("date"));
                    dto.setType(qr.getString("title"));
                    long a = qr.getLong("status");
                    int b = (int) a;
                    dto.setStatus(b);

                    list.add(dto);
                }
                adapter_todolist = new Adapter_todolist(MainActivity.this, list);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                rc_list.setLayoutManager(linearLayoutManager);
                rc_list.setAdapter(adapter_todolist);
            }
        });
        adapter_todolist = new Adapter_todolist(MainActivity.this, list);
        nghe();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = UUID.randomUUID().toString();
                String titlr = title.getText().toString();
                String con = content.getText().toString();
                String da = date.getText().toString();
                String ty = type.getText().toString();


                Todolist_DTO todolistDto = new Todolist_DTO(id, titlr, con, da, ty, 0);
                HashMap<String, Object> map = todolistDto.convertHashMap();
                if (!id.isEmpty() && !titlr.isEmpty() && !con.isEmpty() && !da.isEmpty() && !ty.isEmpty()) {
                    database.collection("lab7-a082f").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, "Thành công", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Không dược để trống thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    String TAG = "a";

    public void nghe() {
        database.collection("lab7-a082f").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onEvent: ", error);
                    return;
                }

                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()
                    ) {
                        switch (dc.getType()) {
                            case ADDED:
                                dc.getDocument().toObject(Todolist_DTO.class);
                                list.add(dc.getDocument().toObject(Todolist_DTO.class));
                                adapter_todolist.notifyItemInserted(list.size() - 1);
                                break;
                            case MODIFIED:
                                Todolist_DTO dto = dc.getDocument().toObject(Todolist_DTO.class);
                                if (dc.getOldIndex() == dc.getNewIndex()) {
                                    list.clear();
                                    list.add(dc.getOldIndex(), dto);
                                    adapter_todolist.notifyItemInserted(dc.getOldIndex());
                                } else {
                                    list.remove(dc.getOldIndex());
                                    list.add(dto);
                                    adapter_todolist.notifyItemMoved(dc.getOldIndex(), dc.getNewIndex());
                                }
                                break;
                            case REMOVED:
                                dc.getDocument().toObject(Todolist_DTO.class);
                                list.remove(dc.getDocument().toObject(Todolist_DTO.class));
                                adapter_todolist.notifyItemRemoved(dc.getOldIndex());
                                break;
                        }
                    }
                }
            }
        });
    }
}