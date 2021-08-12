package com.example.pice.duvit.NAVEGACION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.example.pice.duvit.ADAPTER.AdapterProyectos;
import com.example.pice.duvit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.pice.duvit.NAVEGACION.HomeNavegacionActivity.PREFS_KEY;

public class ProyectosActivity extends AppCompatActivity {

    SharedPreferences preferences;
    TextView tv_title_content;

    RecyclerView recycler_proyectos;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Theme select */
        preferences = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        boolean modo_oscuro_on = preferences.getBoolean("modo_oscuro_on", false);
        if (modo_oscuro_on){ setTheme(R.style.FeedActivityThemeDark); }
        else{ setTheme(R.style.FeedActivityThemeLight); }
        /*End botón atras*/
        setContentView(R.layout.activity_proyectos);

        fullscreenview();

        /*Init Título*/
        tv_title_content = findViewById(R.id.tv_title_content);
        tv_title_content.setText("Proyectos");

        /* Init components adapter*/
        recycler_proyectos = findViewById(R.id.recycler_proyectos);
        layoutManager = new LinearLayoutManager(ProyectosActivity.this);
        recycler_proyectos.setLayoutManager(layoutManager);
        /* End components adapter*/

        /*Nos aseguramos que el archivo Globals tenga el idStaff*/
        Globals.getInstance().idStaff = preferences.getInt("idStaffSharedPreferenceWeb", 1);

        //Consumo de WebService
        getProyectos();

        ImageView ibAtras = (ImageView) findViewById(R.id.ibAtras);
        Toolbar secondtoolbar = (Toolbar) findViewById(R.id.secondtoolbar);
        setSupportActionBar(secondtoolbar);
        if (modo_oscuro_on){ ibAtras.setImageResource(R.drawable.ic_flecha_atras_white); }
        else{ ibAtras.setImageResource(R.drawable.ic_flecha_atras_black); }

        ibAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

    }

    private void fullscreenview() {
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void getProyectos() {
        String URL = "http://duvitapp.com/WebService/proyectos.php?id=" + Globals.getInstance().idStaff;
        //Log.i("Memije1", URL);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray proyectos = response.getJSONArray("proyectos");
                            //Globals.getInstance().numProyectos = proyectos.length();
                            Globals.getInstance().listaProyectosId.clear();
                            Globals.getInstance().listaProyectosNombre.clear();
                            if (proyectos.length() != 0) {
                                for (int i = 0; i < proyectos.length(); i++) {
                                    JSONObject proyecto = proyectos.getJSONObject(i);
                                    Globals.getInstance().listaProyectosId.add(proyecto.getString("idProyecto"));
                                    Globals.getInstance().listaProyectosNombre.add(proyecto.getString("nombreProyecto"));
                                }
                                /* Adapter 2 de 2 */
                                adapter = new AdapterProyectos( ProyectosActivity.this,
                                        Globals.getInstance().listaProyectosId,
                                        Globals.getInstance().listaProyectosNombre);
                                recycler_proyectos.setAdapter(adapter);
                                /* End Adapter */

                            }else{ Toast.makeText(ProyectosActivity.this, "No hay proyectos", Toast.LENGTH_SHORT).show(); }
                        } catch (JSONException e) { Toast.makeText(ProyectosActivity.this, "Algo salió mal. Error al realizar la consulta de datos." + e.getMessage(), Toast.LENGTH_SHORT).show(); }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { Toast.makeText(ProyectosActivity.this, "Error al hacer conexión con el servidor. ", Toast.LENGTH_SHORT).show(); }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
