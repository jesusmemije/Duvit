package com.example.pice.duvit.NAVEGACION;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pice.duvit.ACCESO.LoginActivity;
import com.example.pice.duvit.DIALOG.InfoDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pice.duvit.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class HomeNavegacionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    public static String PREFS_KEY = "mispreferencias";
    TextView tvMostrarNombre, tvMostrarInfo;

    /*ImageView iv_profile;*/
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int idGenero;
    String tema, nombrePreferenceWeb, correoPreferenceWeb, userPreferenceWeb;
    String token_register, idusuario;

    private static final String TAG = "HomeNavegationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_navegacion);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_icon_hamburguesa);

        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View hView = navigationView.getHeaderView(0);
        tvMostrarNombre = hView.findViewById(R.id.tvMostrarNombre);
        tvMostrarInfo = hView.findViewById(R.id.tvMostrarInfo);
        /*iv_profile = hView.findViewById(R.id.iv_profile);*/

        preferences = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        nombrePreferenceWeb = preferences.getString("nombreSharedPreferenceWeb", "");
        correoPreferenceWeb = preferences.getString("correoSharedPreferenceWeb", "");
        userPreferenceWeb = preferences.getString("user", "");
        tema = preferences.getString("temaSharedPreferenceWeb", "default");
        idGenero = preferences.getInt("idGeneroSharedPreferenceWeb", 0);

        /*if (tema.equals("medieval")) {
            if (idGenero == 1) {
                iv_profile.setImageResource(R.drawable.medievalavatarhombre);
            } else if (idGenero == 2) {
                iv_profile.setImageResource(R.drawable.medievalavatarmujer);
            } else {
                iv_profile.setImageResource(R.drawable.duvit_icono);
            }
        } else if (tema.equals("futbol")) {
            if (idGenero == 1) {
                iv_profile.setImageResource(R.drawable.footballavatarhombre);
            } else if (idGenero == 2) {
                iv_profile.setImageResource(R.drawable.footballavatarmujer);
            } else {
                iv_profile.setImageResource(R.drawable.duvit_icono);
            }
        } else if (tema.equals("polar")) {
            if (idGenero == 1) {
                iv_profile.setImageResource(R.drawable.footballavatarhombre);
            } else if (idGenero == 2) {
                iv_profile.setImageResource(R.drawable.footballavatarmujer);
            } else {
                iv_profile.setImageResource(R.drawable.duvit_icono);
            }
        } else {
            if (idGenero == 1) {
                iv_profile.setImageResource(R.drawable.defaultavatarhombre);
            } else if (idGenero == 2) {
                iv_profile.setImageResource(R.drawable.defaultavatarmujer);
            } else {
                iv_profile.setImageResource(R.drawable.duvit_icono);
            }
        }*/

        tvMostrarNombre.setText(nombrePreferenceWeb);

        if (!correoPreferenceWeb.equals("")) {
            tvMostrarInfo.setText(correoPreferenceWeb);
        } else {
            tvMostrarInfo.setText(userPreferenceWeb);
        }

        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contenedor, new PrincipalFragment()).commit();

        editor = preferences.edit();

        navigationView.getMenu().findItem(R.id.navModoOscuro).setActionView(new Switch(this));
        boolean modo_oscuro_on = preferences.getBoolean("modo_oscuro_on", false);

        if (modo_oscuro_on) {
            ((Switch) navigationView.getMenu().findItem(R.id.navModoOscuro).getActionView()).setChecked(true);
        } else {
            ((Switch) navigationView.getMenu().findItem(R.id.navModoOscuro).getActionView()).setChecked(false);
        }
        Switch aSwitch = ((Switch) navigationView.getMenu().findItem(R.id.navModoOscuro).getActionView());
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    editor.putBoolean("modo_oscuro_on", true);
                    editor.apply();
                    Toast.makeText(HomeNavegacionActivity.this, "Modo oscuro activado", Toast.LENGTH_SHORT).show();

                } else {

                    editor.putBoolean("modo_oscuro_on", false);
                    editor.apply();
                    Toast.makeText(HomeNavegacionActivity.this, "Modo oscuro desactivado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String tokenNotificaciones = preferences.getString("tokenNotificaciones", "");
        if (tokenNotificaciones.equals("")){
            getToken();
        }

    }

    private void getToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = Objects.requireNonNull(task.getResult()).getToken();

                        registrarToken(token);

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        //Toast.makeText(HomeNavegacionActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registrarToken(final String token) {

        idusuario = String.valueOf(Globals.getInstance().idUsuario);
        token_register = token;

        String URL = "http://duvitapp.com/WebService/tokenNotificaciones.php?idusuario=" + idusuario + "&token=" + token_register + " ";
        String URL_LIMPIA = URL.replaceAll("\\s", "");

        RequestQueue requestQueue = Volley.newRequestQueue(HomeNavegacionActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_LIMPIA, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject getinput) {
                        try {
                            JSONArray jsonArray = getinput.getJSONArray("response");

                                JSONObject response = jsonArray.getJSONObject(0);
                                int code = Integer.parseInt(response.getString("code"));
                                String mensaje = response.getString("mensaje");

                                if (code == 501 || code == 601 || code == 401) {
                                    //Toast.makeText(HomeNavegacionActivity.this, "[CODE : "+ code +"] " + mensaje, Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "[CODE : "+ code +"] " + mensaje);
                                } else if (code == 201) {
                                   // Toast.makeText(getApplicationContext(), "" + mensaje, Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "[CODE : "+ code +"] " + mensaje);
                                }

                        } catch (JSONException e) {
                            //Toast.makeText(HomeNavegacionActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Problema al consumir el WS - Registrar token - HomeNavegationActivity: " + error.getMessage() , Toast.LENGTH_LONG).show();
                Log.d(TAG, "Problema al consumir el WS - Registrar token - HomeNavegationActivity: " + error.getMessage());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public int contador = 0;

    @Override
    public void onBackPressed() {

        if (contador == 0) {
            Toast.makeText(HomeNavegacionActivity.this, "Presione nuevamente para cerrar sesión", Toast.LENGTH_SHORT).show();
            contador++;
        } else {

            SharedPreferences settings = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
            settings.edit().clear().apply();

            Globals.getInstance().validaSesionIniciada = false;
            Globals.getInstance().cltZoomMap = true;

            registrarToken("");

            startActivity(new Intent(HomeNavegacionActivity.this, LoginActivity.class));
            finish();

        }

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                contador = 0;
            }
        }.start();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        //------------------------------------------------------------------------------------------
        int clt_num_tareas = Globals.getInstance().numPages;
        if (id == R.id.navTareas) {
            if (clt_num_tareas != 0) {
                Intent detallestareas = new Intent(HomeNavegacionActivity.this, DetallesTareasActivity.class);
                startActivity(detallestareas);
            } else {
                Intent info = new Intent(this, SintareasActivity.class);
                startActivity(info);
            }
        } else if (id == R.id.navPuntuacion) {
            Intent puntuacion = new Intent(this, PuntuacionActivity.class);
            startActivity(puntuacion);
        } else if (id == R.id.navEstadisticas) {
            Intent estadisticas = new Intent(this, EstadisticasActivity.class);
            startActivity(estadisticas);
        } else if (id == R.id.navTareasLista) {
            if (clt_num_tareas != 0) {
                Intent lista = new Intent(this, ListaTareasActivity.class);
                startActivity(lista);
            } else {
                Intent info = new Intent(this, SintareasActivity.class);
                startActivity(info);
            }
        } else if (id == R.id.navProyectos) {
            Intent proyectos = new Intent(this, ProyectosActivity.class);
            startActivity(proyectos);
        } else if (id == R.id.navReuniones) {
            Intent reuniones = new Intent(this, ZoomActivity.class);
            startActivity(reuniones);
        } else if (id == R.id.navJugar) {
            Intent jugar = new Intent(this, JugarActivity.class);
            startActivity(jugar);
        } else if (id == R.id.navAcercade) {
            FragmentManager fragmentManager = Objects.requireNonNull(getSupportFragmentManager());
            InfoDialog dialogo = new InfoDialog();
            dialogo.show(fragmentManager, "tagAlerta");
        } else if (id == R.id.navCerraSesion) {

            SharedPreferences preferences = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
            String cltCheckOut = preferences.getString("ctlBotones", "");

            if (cltCheckOut.equals("verdadero")) {

                AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
                dialogo.setTitle("Notificación");
                dialogo.setMessage("Antes de cerrar sesión debe registrar su salida.");
                dialogo.setCancelable(false);
                dialogo.setPositiveButton("DE ACUERDO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo, int id) {
                        dialogo.dismiss();
                    }
                });
                dialogo.show();

            } else {
                AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
                dialogo.setTitle("Cerrar sesión");
                dialogo.setMessage("¿Seguro que quieres cerrar la sesión actual?");
                dialogo.setCancelable(false);
                dialogo.setPositiveButton("Si, cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo, int id) {

                        SharedPreferences settings = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
                        settings.edit().clear().apply();

                        Globals.getInstance().validaSesionIniciada = false;
                        Globals.getInstance().cltZoomMap = true;

                        registrarToken("");

                        startActivity(new Intent(HomeNavegacionActivity.this, LoginActivity.class));
                        finish();
                    }
                });
                dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo, int id) {
                        dialogo.dismiss();
                    }
                });
                dialogo.show();
            }

        }
        //------------------------------------------------------------------------------------------
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

