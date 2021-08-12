package com.example.pice.duvit.ADAPTER;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pice.duvit.R;

import java.util.ArrayList;


public class AdapterIntegrantes extends RecyclerView.Adapter<AdapterIntegrantes.ViewHolder> {

    private ArrayList<String> listaIntegrantesNombre, listaIntegrantesCorreo, listaIntegrantesCelular;
    private Context context;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public AdapterIntegrantes(Context context, ArrayList<String> listaIntegrantesNombre, ArrayList<String> listaIntegrantesCorreo, ArrayList<String> listaIntegrantesCelular) {
        this.listaIntegrantesNombre = listaIntegrantesNombre;
        this.listaIntegrantesCorreo = listaIntegrantesCorreo;
        this.listaIntegrantesCelular = listaIntegrantesCelular;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre;
        TextView tvCorreo;
        TextView tvCelular;
        ImageView ivCelular;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCorreo = itemView.findViewById(R.id.tvCorreo);
            tvCelular = itemView.findViewById(R.id.tvCelular);
            ivCelular = itemView.findViewById(R.id.ivCelular);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCall();
                }
            });
            ivCelular.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCall();
                }
            });
        }

        private void onClickCall() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // Se tiene permiso
                    //Toast.makeText(context, "Se tiene permiso", Toast.LENGTH_SHORT).show();
                }else{
                    ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_PERMISSIONS);
                    return;
                }
            }else{
                // No se necesita requerir permiso OS menos a 6.0.
                //Toast.makeText(context, "No se necesita requerir permiso OS menos a 6.0.", Toast.LENGTH_SHORT).show();
            }

            String celular = tvCelular.getText().toString();

            Intent llamada = new Intent(Intent.ACTION_CALL);
            llamada.setData(Uri.parse("tel:"+celular));
            context.startActivity(llamada);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_integrantes, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvNombre.setText(listaIntegrantesNombre.get(i));

        if (listaIntegrantesCorreo.get(i).equals("")){
            viewHolder.tvCorreo.setText("Sin correo electrónico");
        }else{
            viewHolder.tvCorreo.setText(listaIntegrantesCorreo.get(i));
        }

        if (listaIntegrantesCelular.get(i).equals("")){
            viewHolder.tvCelular.setText("Sin teléfono");
            viewHolder.ivCelular.setEnabled(false);
        }else{
            viewHolder.tvCelular.setText(listaIntegrantesCelular.get(i));
        }


    }
    @Override
    public int getItemCount() {
        return listaIntegrantesNombre.size();
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
