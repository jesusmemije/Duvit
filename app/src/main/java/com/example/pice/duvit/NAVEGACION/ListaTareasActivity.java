package com.example.pice.duvit.NAVEGACION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pice.duvit.ADAPTER.AdapterDatosTareas;
import com.example.pice.duvit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.pice.duvit.NAVEGACION.HomeNavegacionActivity.PREFS_KEY;

public class ListaTareasActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    Context context = ListaTareasActivity.this;
    ;
    SharedPreferences preferences;
    TextView tv_title_content;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Theme select */
        preferences = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        boolean modo_oscuro_on = preferences.getBoolean("modo_oscuro_on", false);
        if (modo_oscuro_on) {
            setTheme(R.style.FeedActivityThemeDark);
        } else {
            setTheme(R.style.FeedActivityThemeLight);
        }
        /*End botón atras*/
        setContentView(R.layout.activity_lista_tareas);

        fullscreenview();

        getNumTareas();

        tv_title_content = findViewById(R.id.tv_title_content);
        tv_title_content.setText("Lista - Tareas");


        /*Init botón atras*/
        ImageView ibAtras = (ImageView) findViewById(R.id.ibAtras);
        Toolbar secondtoolbar = (Toolbar) findViewById(R.id.secondtoolbar);
        setSupportActionBar(secondtoolbar);
        if (modo_oscuro_on) {
            ibAtras.setImageResource(R.drawable.ic_flecha_atras_white);
        } else {
            ibAtras.setImageResource(R.drawable.ic_flecha_atras_black);
        }
        ibAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*End botón atras*/
    }

    private void getNumTareas() {
        String URL = "http://duvitapp.com/WebService/datos.php?idStaff=" + Globals.getInstance().idStaff;
        RequestQueue requestQueue = Volley.newRequestQueue(ListaTareasActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("tareas");
                            Globals.getInstance().numPages = jsonArray.length();
                            Globals.getInstance().listaTareasTarea.clear();
                            Globals.getInstance().listaTareasDescripcion.clear();
                            Globals.getInstance().listaTareasNumero.clear();
                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject tareas = jsonArray.getJSONObject(i);
                                    Globals.getInstance().listaTareasTarea.add(tareas.getString("tarea"));
                                    Globals.getInstance().listaTareasDescripcion.add("Descripción: " + tareas.getString("detalle"));
                                }
                            } else {
                                Toast.makeText(ListaTareasActivity.this, "No tienes tareas pendientes. ¡Good Job!.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(ListaTareasActivity.this, "Algo salió mal. Error al realizar la consulta de datos." + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        mostrarTareas();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListaTareasActivity.this, "Error al hacer conexión con el servidor. ", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void fullscreenview() {
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void mostrarTareas() {

        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        for (int i = 1; i <= Globals.getInstance().listaTareasTarea.size(); i++) {
            Globals.getInstance().listaTareasNumero.add("0" + i);
        }
        adapter = new AdapterDatosTareas(context,
                Globals.getInstance().listaTareasTarea,
                Globals.getInstance().listaTareasDescripcion,
                Globals.getInstance().listaTareasNumero);
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
