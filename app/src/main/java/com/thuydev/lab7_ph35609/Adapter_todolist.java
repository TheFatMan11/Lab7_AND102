package com.thuydev.lab7_ph35609;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class Adapter_todolist extends RecyclerView.Adapter<Adapter_todolist.Viewholder> {

    Context context;
    List<Todolist_DTO> list;
    Todolist_DTO dto;
    FirebaseFirestore firestore;
    public Adapter_todolist(Context context, List<Todolist_DTO> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.item_list, parent, false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_name.setText(list.get(position).getTitle());
        holder.tv_ngay.setText(list.get(position).getDate());
        dto = list.get(position);
        firestore = FirebaseFirestore.getInstance().collection("lab7-a082f").getFirestore();


        if (list.get(position).getStatus() == 1) {
            holder.sta.setChecked(true);
        }
        holder.sta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dto = list.get(position);

                if (isChecked) {

                    dto.setStatus(1);
                    firestore.collection("lab7-a082f").document(dto.getId()).update(dto.convertHashMap());
                } else {

                    dto.setStatus(0);
                    firestore.collection("lab7-a082f").document(dto.getId()).update(dto.convertHashMap());
                }
            }
        });
        holder.xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Cảnh báo");

                builder.setMessage("Bạn chắc chắn muốn xóa");
                builder.setNegativeButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dto = list.get(position);
                        firestore.collection("lab7-a082f").document(dto.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();


            }
        });
        holder.sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dto = list.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View view = inflater.inflate(R.layout.dialogsua, null, false);
                builder.setView(view);
                Dialog dialog = builder.create();
                dialog.show();
                EditText title = view.findViewById(R.id.edt_title_sua);
                EditText content = view.findViewById(R.id.edt_content_sua);
                EditText date = view.findViewById(R.id.edt_date_sua);
                EditText type = view.findViewById(R.id.edt_type_sua);
                Button sua = view.findViewById(R.id.btn_add_sua);
                title.setText(dto.getTitle());
                content.setText(dto.getContent());
                date.setText(dto.getDate());
                type.setText(dto.getType());
                type.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String [] mang = new String[]{
                                "Khó","Trung bình","Dễ"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                sua.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dto.setTitle(title.getText().toString());
                        dto.setContent(content.getText().toString());
                        dto.setDate(date.getText().toString());
                        dto.setType(type.getText().toString());
                        firestore.collection("lab7-a082f").document(dto.getId()).update(dto.convertHashMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_ngay;
        ImageButton sua, xoa;
        CheckBox sta;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name_mon);
            tv_ngay = itemView.findViewById(R.id.tv_ngay);
            sua = itemView.findViewById(R.id.ibtn_update);
            xoa = itemView.findViewById(R.id.ibtn_delete);
            sta = itemView.findViewById(R.id.cbk_);
        }
    }


}
