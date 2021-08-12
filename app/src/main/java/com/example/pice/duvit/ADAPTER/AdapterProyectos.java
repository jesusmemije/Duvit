package com.example.pice.duvit.ADAPTER;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pice.duvit.NAVEGACION.Globals;
import com.example.pice.duvit.NAVEGACION.IntegrantesActivity;
import com.example.pice.duvit.R;

import java.util.ArrayList;


public class AdapterProyectos extends RecyclerView.Adapter<AdapterProyectos.ViewHolder> {
    private ArrayList<String> listaProyectosId, listaProyectosNombre;
    private Context context;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public AdapterProyectos(Context context, ArrayList<String> listaProyectosId, ArrayList<String> listaProyectosNombre) {
        this.listaProyectosId = listaProyectosId;
        this.listaProyectosNombre = listaProyectosNombre;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvIdProyecto;
        TextView tvNombreProyecto;
        ImageView ivInfo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIdProyecto = itemView.findViewById(R.id.tvIdProyecto);
            tvNombreProyecto = itemView.findViewById(R.id.tvNombreProyecto);
            ivInfo = itemView.findViewById(R.id.ivInfo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //context = v.getContext();

                    String idProyecto = tvIdProyecto.getText().toString();
                    String proyecto = tvNombreProyecto.getText().toString();
                    Globals.getInstance().idProyectoSelect = idProyecto;
                    Globals.getInstance().nombreProyectoSelect = proyecto;

                    Intent integrantes = new Intent(context, IntegrantesActivity.class);
                    context.startActivity(integrantes);
                }
            });

            ivInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String idProyecto = tvIdProyecto.getText().toString();
                    String proyecto = tvNombreProyecto.getText().toString();
                    Globals.getInstance().idProyectoSelect = idProyecto;
                    Globals.getInstance().nombreProyectoSelect = proyecto;

                    Intent integrantes = new Intent(context, IntegrantesActivity.class);
                    context.startActivity(integrantes);
                }
            });

        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_proyectos, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvIdProyecto.setText(listaProyectosId.get(i));
        viewHolder.tvNombreProyecto.setText(listaProyectosNombre.get(i));
    }
    @Override
    public int getItemCount() {
        return listaProyectosId.size();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // El usuario acepto los permisos.
                    Toast.makeText(context, "Gracias, aceptaste los permisos requeridos para el correcto funcionamiento de esta aplicación.", Toast.LENGTH_SHORT).show();
                }else{
                    // Permiso denegado.
                    Toast.makeText(context, "No se aceptó permisos", Toast.LENGTH_SHORT).show();
                }

        }
    }
}
