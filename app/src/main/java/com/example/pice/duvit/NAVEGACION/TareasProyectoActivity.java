package com.example.pice.duvit.NAVEGACION;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pice.duvit.ADAPTER.AdapterDatosTareasProyecto;
import com.example.pice.duvit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TareasProyectoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    Context context;
    JSONArray jsonArray;
    static String PREFS_KEY = "mispreferencias";
    SharedPreferences preferences;
    String tema;
    TextView tv_title_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        boolean modo_oscuro_on = preferences.getBoolean("modo_oscuro_on", false);

        if (modo_oscuro_on){
            setTheme(R.style.FeedActivityThemeDark);
        }else{
            setTheme(R.style.FeedActivityThemeLight);
        }

        setContentView(R.layout.activity_tareas_proyecto);

        fullscreenview();
        tv_title_content = findViewById(R.id.tv_title_content);

        int idStaff = Globals.getInstance().idStaff;
        Bundle datos = this.getIntent().getExtras();
        int idProyecto = datos.getInt("setIdProyecto");
        String name_proyecto = datos.getString("name_proyecto");
        String title_content = "Tareas " + name_proyecto;
        tv_title_content.setText(title_content);

        getTareas(idStaff,idProyecto);

        ImageView ibAtras = (ImageView) findViewById(R.id.ibAtras);
        Toolbar secondtoolbar = (Toolbar) findViewById(R.id.secondtoolbar);
        setSupportActionBar(secondtoolbar);

        if (modo_oscuro_on){
            ibAtras.setImageResource(R.drawable.ic_flecha_atras_white);
        }else{
            ibAtras.setImageResource(R.drawable.ic_flecha_atras_black);
        }

        ibAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    int idProyectoGlobal = 0;

    private void getTareas(int idStaff, int idProyecto) {
        idProyectoGlobal = idProyecto;
        //String URL = "http://duvitapp.com/WebService/tareasProyecto.php?idProyecto="+idProyecto+"";
        String URL = "http://duvitapp.com/WebService/datos.php?idStaff="+ Globals.getInstance().idStaff;
        //String URL = "http://duvitapp.com/WebService/tareasProyecto.php?idStaff="+idStaff+"&idProyecto="+idProyecto+"";
        //Toast.makeText(TareasProyectoActivity.this, "Esto: " + URL, Toast.LENGTH_LONG).show();
        System.out.println(URL);
        RequestQueue requestQueue = Volley.newRequestQueue(TareasProyectoActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("tareas");
                            Globals.getInstance().numPagesAdaptar = jsonArray.length();
                            Globals.getInstance().listaTareasTareaAdapter.clear();
                            Globals.getInstance().listaTareasDescripcionAdapter.clear();
                            Globals.getInstance().listaTareasNumeroAdapter.clear();
                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject tareas = jsonArray.getJSONObject(i);

                                    int idProyectoWeb = Integer.valueOf(tareas.getString("idProyecto"));

                                    if (idProyectoWeb == idProyectoGlobal){
                                        Globals.getInstance().listaTareasTareaAdapter.add(tareas.getString("tarea"));
                                        Globals.getInstance().listaTareasDescripcionAdapter.add("Descripción: " + tareas.getString("detalle"));
                                    }
                                }
                            }else{ Toast.makeText(TareasProyectoActivity.this, "No se encontraron tareas de este proyecto. ¡Good Job!.", Toast.LENGTH_SHORT).show(); }
                        } catch (JSONException e) { Toast.makeText(TareasProyectoActivity.this, "Algo salió mal. Error al realizar la consulta de datos." + e.getMessage(), Toast.LENGTH_SHORT).show(); }

                        mostrarTareas();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TareasProyectoActivity.this, "Error al hacer conexion: " +error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void fullscreenview() {
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void mostrarTareas() {
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(TareasProyectoActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        context = TareasProyectoActivity.this;
        for (int i = 1; i <= Globals.getInstance().listaTareasTareaAdapter.size(); i++){
            Globals.getInstance().listaTareasNumeroAdapter.add("0"+i);
        }
        adapter = new AdapterDatosTareasProyecto(context, Globals.getInstance().listaTareasTareaAdapter, Globals.getInstance().listaTareasDescripcionAdapter, Globals.getInstance().listaTareasNumeroAdapter);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
