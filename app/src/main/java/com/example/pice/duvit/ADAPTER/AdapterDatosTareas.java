package com.example.pice.duvit.ADAPTER;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pice.duvit.NAVEGACION.DetallesTareasActivity;
import com.example.pice.duvit.R;

import java.util.ArrayList;
public class AdapterDatosTareas extends RecyclerView.Adapter<AdapterDatosTareas.ViewHolder> {

    private ArrayList<String> listaTareasTarea, listaTareasDescripcion, listaTareasNumero;
    private Context context;

    public AdapterDatosTareas(Context context, ArrayList<String> listaTareasTarea, ArrayList<String> listaTareasDescripcion, ArrayList<String> listaTareasNumero) {
        this.listaTareasTarea = listaTareasTarea;
        this.listaTareasDescripcion = listaTareasDescripcion;
        this.listaTareasNumero = listaTareasNumero;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public int currentItem;
        TextView itemNumero;
        TextView itemTitle;
        TextView itemDetail;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNumero = itemView.findViewById(R.id.itemNumero);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDetail = itemView.findViewById(R.id.item_detail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    context = v.getContext();

                    Intent detail = new Intent(context.getApplicationContext(), DetallesTareasActivity.class);
                    context.startActivity(detail);

                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_listatareas, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.itemTitle.setText(listaTareasTarea.get(i));
        viewHolder.itemDetail.setText(listaTareasDescripcion.get(i));
        viewHolder.itemNumero.setText(listaTareasNumero.get(i));

    }

    @Override
    public int getItemCount() {
        return listaTareasTarea.size();
    }
}
