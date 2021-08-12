package com.example.pice.duvit.ACCESO;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.example.pice.duvit.CLASES.AlarmReceiverEntrada;
import com.example.pice.duvit.CLASES.AlarmReceiverSalida;
import com.google.android.material.snackbar.Snackbar;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pice.duvit.NAVEGACION.HomeNavegacionActivity;
import com.example.pice.duvit.R;
import com.example.pice.duvit.NAVEGACION.Globals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Calendar;

import dmax.dialog.SpotsDialog;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LoginActivity extends AppCompatActivity {

    Button btnEntrar;
    TextView tvMostrar;
    EditText etUsuario, etContrasena;
    ProgressDialog progressDialogUbicacion;
    SpotsDialog progressDialog;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    LocationManager locationManager;
    static String PREFS_KEY = "mispreferencias";
    String usuario;
    String contrasena;
    String usuarioPreferences;
    String passwordPreferences;

    public static final int REQUEST_CODE = 101, REQUEST_CODE2 = 102;
    AlarmManager alarmManager, alarmManager2;
    PendingIntent pendingIntent, pendingIntent2;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullscreenview();

        btnEntrar = (Button) findViewById(R.id.btnEntrar);
        tvMostrar = (TextView) findViewById(R.id.tvMostrar);
        etUsuario = (EditText) findViewById(R.id.etUsuario);
        etContrasena = (EditText) findViewById(R.id.etContrasena);

        usuarioPreferences = leerSharedPreferences("user");
        passwordPreferences = leerSharedPreferences("pass");

        if (!usuarioPreferences.equals("") && !passwordPreferences.equals("")) {


            Globals.getInstance().validaSesionIniciada = true;
            startActivity(new Intent(LoginActivity.this, HomeNavegacionActivity.class));
            finish();


        } else {

            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {*/

            permisoUbicacion();
            //progressObtenerUbicacion();
            alarmEntradaSalida();

                /*}
            }, 7000);*/
        }

        cargaBtnEntrar();
    }

    private void fullscreenview() {
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void alarmEntradaSalida() {

        Calendar calendar = Calendar.getInstance();
        Calendar calendarExit = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_WEEK);
        if (dia >= Calendar.MONDAY && dia <= Calendar.FRIDAY) {

            alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiverEntrada.class);
            pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 30);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            //---------------------------------------------------------------------------------------------------------------

            alarmManager2 = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent2 = new Intent(this, AlarmReceiverSalida.class);
            pendingIntent2 = PendingIntent.getBroadcast(this, REQUEST_CODE2, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            calendarExit.setTimeInMillis(System.currentTimeMillis());
            calendarExit.set(Calendar.HOUR_OF_DAY, 17);
            calendarExit.set(Calendar.MINUTE, 0);
            alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, calendarExit.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent2);
        }
    }

    public void guardarSharedPreferences(String keyPref, String valor) {
        SharedPreferences settings = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(keyPref, valor);
        editor.apply();
    }

    public String leerSharedPreferences(String keyPref) {
        SharedPreferences preferences = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return preferences.getString(keyPref, "");
    }

    private void cargaBtnEntrar() {
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = etUsuario.getText().toString();
                contrasena = etContrasena.getText().toString();

                if (usuario.equals("") || contrasena.equals("")) {
                    Toast.makeText(LoginActivity.this, "¡No debe dejar campos vacíos!", Toast.LENGTH_SHORT).show();
                } else {

                    progressLogin();
                    autentificacionDatos(usuario, contrasena);


                }
            }
        });
    }

    private void autentificacionDatos(String usuario, String contrasena) {

        Globals.getInstance().validaDatosDeSesion = false;

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(LoginActivity.this, "Sin conexión a internet", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else {
            if (networkInfo.isConnected()) {

                String URL = "http://duvitapp.com/WebService/login.php?correo=" + usuario + "&password=" + contrasena + " ";
                String URL_LIMPIA = URL.replaceAll("\\s", "");

                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_LIMPIA, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("datos");
                                    if (jsonArray.length() != 0) {

                                        JSONObject datos = jsonArray.getJSONObject(0);
                                        int code = Integer.parseInt(datos.getString("code"));
                                        String mensaje = datos.getString("mensaje");

                                        if (code == 501 || code == 601 || code == 401) {
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "" + mensaje, Toast.LENGTH_SHORT).show();
                                        } else if (code == 201) {
                                            Globals.getInstance().idUsuario = Integer.parseInt(datos.getString("idUsuario"));
                                            Globals.getInstance().idStaff = Integer.parseInt(datos.getString("idStaff"));
                                            Globals.getInstance().validaUbicacionGPS = Integer.parseInt(datos.getString("validarGPS"));
                                            Globals.getInstance().latitudWebServ = Float.parseFloat(datos.getString("lat"));
                                            Globals.getInstance().longitudWebServ = Float.parseFloat(datos.getString("long"));
                                            SharedPreferences settings = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putInt("idUsuarioSharedPreferenceWeb", Integer.parseInt(datos.getString("idUsuario")));
                                            editor.putInt("idStaffSharedPreferenceWeb", Integer.parseInt(datos.getString("idStaff")));
                                            editor.putInt("idGeneroSharedPreferenceWeb", Integer.parseInt(datos.getString("idGenero")));
                                            editor.putInt("validaUbicacionGPS", Integer.parseInt(datos.getString("validarGPS")));
                                            editor.putFloat("latitudSharedPreferenceWeb", Float.parseFloat(datos.getString("lat")));
                                            editor.putFloat("longitudSharedPreferenceWeb", Float.parseFloat(datos.getString("long")));
                                            editor.putString("nombreSharedPreferenceWeb", datos.getString("nombre"));
                                            editor.putString("correoSharedPreferenceWeb", datos.getString("correo"));
                                            editor.putString("tokenNotificaciones", datos.getString("token"));
                                            //editor.putString("temaSharedPreferenceWeb", datos.getString("Tema"));
                                            editor.putString("temaSharedPreferenceWeb", "default");
                                            editor.putBoolean("modo_oscuro_on", false);
                                            editor.apply();
                                            guardarSharedPreferences("user", etUsuario.getText().toString());
                                            guardarSharedPreferences("pass", etContrasena.getText().toString());
                                            new Handler().postDelayed(new Runnable() {
                                                public void run() {
                                                    startActivity(new Intent(LoginActivity.this, HomeNavegacionActivity.class));
                                                    progressDialog.dismiss();
                                                    finish();
                                                }
                                            }, 3500);
                                            Globals.getInstance().validaDatosDeSesion = true;
                                        } else {
                                            //Si por alguna razón el server no arrojá nada.
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "Problema desconocido, intente nuevamente por favor.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "No hay datos de este usuario", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Globals.getInstance().validaSesionIniciada = false;
                        Globals.getInstance().cltZoomMap = true;
                        Globals.getInstance().validaDatosDeSesion = false;
                        Toast.makeText(LoginActivity.this, "Problemas al hacer la petición: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                requestQueue.add(jsonObjectRequest);
            } else {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "¡Algo salió mal al tratar de conectarse a la red!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCoordenadasLogin() {
        LocationListener locationListener = new LocationListener() {
            @SuppressLint("SetTextI18n")
            public void onLocationChanged(Location location) {
                Globals.getInstance().latitud = location.getLatitude();
                Globals.getInstance().longitud = location.getLongitude();
                tvMostrar.setText("¡Ubicación obtenida, listo!");
                //progressDialogUbicacion.dismiss();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        ContextCompat.checkSelfPermission(LoginActivity.this, ACCESS_FINE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    private void progressLogin() {

        progressDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
        progressDialog.show();


    }

    private void progressObtenerUbicacion() {
        progressDialogUbicacion = new ProgressDialog(LoginActivity.this);
        progressDialogUbicacion.setTitle("Obteniendo coordenadas");
        progressDialogUbicacion.setMessage("Espere un momento...");
        progressDialogUbicacion.setCancelable(false);
        progressDialogUbicacion.show();

    }

    private void preguntaGPSoCargaDatos() {
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            preguntarGPS();
        } else {
            getCoordenadasLogin();
        }
    }

    private void permisoUbicacion() {
        locationManager = (LocationManager) LoginActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //CODE..
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            } else {
                preguntaGPSoCargaDatos();
            }
        } else {
            preguntaGPSoCargaDatos();
        }
    }

    public void preguntarGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Para continuar, activa la ubicación del dispositivo en modo de alta precisión: GPS, Wi-Fi y redes móviles.");
        builder.setCancelable(false);
        builder.setPositiveButton("Si, de acuerdo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
                Globals.getInstance().cltPreguntarGps = false;
            }
        })
                .setNegativeButton("No, salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        builder.create();
        builder.show();
    }

    private void SnackbarPermisos() {
        Snackbar.make(findViewById(android.R.id.content), "Es necesario conceder permisos de ubicación a la App.", Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorPrimary))
                .setAction("PERMISOS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Globals.getInstance().cltPreguntarGps = false;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", LoginActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        LoginActivity.this.startActivityForResult(intent, 1);
                    }
                }).show();
    }

    private void preguntarActivarPermiso() {
        android.app.AlertDialog.Builder dialogo = new android.app.AlertDialog.Builder(LoginActivity.this);
        dialogo.setTitle("¡Importante!");
        dialogo.setMessage("Es necesario conceder permisos de ubicación a la App, de lo contrario no podrá hacer uso de la App.");
        dialogo.setCancelable(false);
        dialogo.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                Globals.getInstance().cltPreguntaPermiso = false;
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        });
        dialogo.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                dialogo.dismiss();
                SnackbarPermisos();
            }
        });
        dialogo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    preguntaGPSoCargaDatos();
                } else {
                    if (Globals.getInstance().cltPreguntaPermiso) {
                        preguntarActivarPermiso();
                    } else {
                        SnackbarPermisos();
                    }
                }
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!Globals.getInstance().cltPreguntarGps) {
            locationManager = (LocationManager) LoginActivity.this.getSystemService(Context.LOCATION_SERVICE);
            preguntaGPSoCargaDatos();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
