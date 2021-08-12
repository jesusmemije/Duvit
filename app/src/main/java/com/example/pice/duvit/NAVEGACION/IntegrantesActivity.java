package com.example.pice.duvit.NAVEGACION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.example.pice.duvit.ADAPTER.AdapterIntegrantes;
import com.example.pice.duvit.ADAPTER.AdapterProyectos;
import com.example.pice.duvit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.pice.duvit.NAVEGACION.HomeNavegacionActivity.PREFS_KEY;

public class IntegrantesActivity extends AppCompatActivity {

    SharedPreferences preferences;
    TextView tv_title_content;

    RecyclerView recycler_integrantes;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    TextView tvNombre;

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
        setContentView(R.layout.activity_integrantes);
        fullscreenview();

        /*Init Título*/
        tv_title_content = findViewById(R.id.tv_title_content);
        tv_title_content.setText("Integrantes");

        tvNombre = findViewById(R.id.tvNombre);
        tvNombre.setText(Globals.getInstance().nombreProyectoSelect);

        /* Init components adapter*/
        recycler_integrantes = findViewById(R.id.recycler_integrantes);
        layoutManager = new LinearLayoutManager(IntegrantesActivity.this);
        recycler_integrantes.setLayoutManager(layoutManager);
        /* End components adapter*/

        //Consumo de WebService
        getIntegrantes();

        ImageView ibAtras = (ImageView) findViewById(R.id.ibAtras);
        Toolbar secondtoolbar = (Toolbar) findViewById(R.id.secondtoolbar);
        setSupportActionBar(secondtoolbar);
        if (modo_oscuro_on){ ibAtras.setImageResource(R.drawable.ic_close_white); }
        else{ ibAtras.setImageResource(R.drawable.ic_close_black); }

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

    private void getIntegrantes() {
        String URL = "http://duvitapp.com/WebService/equipoProyecto.php?id=" + Globals.getInstance().idProyectoSelect;
        //Log.i("Memije1", URL);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray datos = response.getJSONArray("datos");
                            //Globals.getInstance().numProyectos = proyectos.length();
                            Globals.getInstance().listaIntegrantesNombre.clear();
                            Globals.getInstance().listaIntegrantesCorreo.clear();
                            Globals.getInstance().listaIntegrantesCelular.clear();
                            if (datos.length() != 0) {
                                for (int i = 0; i < datos.length(); i++) {
                                    JSONObject integrante = datos.getJSONObject(i);
                                    Globals.getInstance().listaIntegrantesNombre.add(integrante.getString("nombre") + " " + integrante.getString("apellidoPaterno"));
                                    Globals.getInstance().listaIntegrantesCorreo.add(integrante.getString("correoCorporativo"));
                                    Globals.getInstance().listaIntegrantesCelular.add(integrante.getString("telefonoCelular"));
                                }
                                /* Adapter 2 de 2 */
                                adapter = new AdapterIntegrantes( IntegrantesActivity.this,
                                        Globals.getInstance().listaIntegrantesNombre,
                                        Globals.getInstance().listaIntegrantesCorreo,
                                        Globals.getInstance().listaIntegrantesCelular);
                                recycler_integrantes.setAdapter(adapter);
                                /* End Adapter */

                            }else{ Toast.makeText(IntegrantesActivity.this, "No hay Integrantes", Toast.LENGTH_SHORT).show(); }
                        } catch (JSONException e) { Toast.makeText(IntegrantesActivity.this, "Algo salió mal. Error al realizar la consulta de datos." + e.getMessage(), Toast.LENGTH_SHORT).show(); }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { Toast.makeText(IntegrantesActivity.this, "Error al hacer conexión con el servidor. ", Toast.LENGTH_SHORT).show(); }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
