package com.example.pice.duvit.NAVEGACION;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pice.duvit.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class PrincipalFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ArrayList<Marker> tmpRealTimeMarker = new ArrayList<>();
    private ArrayList<Marker> realTimeMarker = new ArrayList<>();

    private View view;
    private Button btnCheckIn, btnCheckOut;
    private TextView tvUbicacion, tvTrabajo, txtfrase, txtautor, tvtitle, tvtitle_2;

    private Dialog modalpopup;

    private static String PREFS_KEY = "mispreferencias";
    private SharedPreferences preferences;
    private String cltBotonesPreferences;
    private String tema;

    private CoordinatorLayout coordinatorLayout;
    private View bottom_sheet;

    private Drawable btn_expand, btn_hidden;

    String fecha_letra;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_principal, container, false);


        modalpopup = new Dialog(getContext());
        modalpopup.setContentView(R.layout.dialog_frasemotivacional);
        txtfrase = modalpopup.findViewById(R.id.txtfrase);
        txtautor = modalpopup.findViewById(R.id.txtautor);

        preferences = getContext().getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        tema = preferences.getString("temaSharedPreferenceWeb", "default");

        String URL = "http://duvitapp.com/WebService/fraseMotivacional.php";
        String URL_LIMPIA = URL.replaceAll("\\s","");

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_LIMPIA, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("datos");
                            if (jsonArray.length() != 0){
                                for (int i=0; i < jsonArray.length(); i++){
                                    JSONObject datos = jsonArray.getJSONObject(i);

                                    String frase = datos.getString("frase");
                                    String autor = "-"+datos.getString("autor")+".";

                                    txtfrase.setText(Html.fromHtml("&ldquo;" + frase + "&rdquo;."));
                                    txtautor.setText(autor);
                                    modalpopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    //modalpopup.setCancelable(false);
                                    modalpopup.show();

                                }
                            }else{ Toast.makeText(getContext(), "No se encontró frase motivadora", Toast.LENGTH_SHORT).show(); }
                        } catch (JSONException e) { Toast.makeText(getContext(), "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show(); }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Ocurrió un error al consumir WebService.", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

        if (Globals.getInstance().validaSesionIniciada){
            preferences = Objects.requireNonNull(getContext()).getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
            float latitudPreference = preferences.getFloat("latitudSharedPreference", 0);
            float longitudPreference = preferences.getFloat("longitudSharedPreference", 0);
            float latitudPreferenceWeb = preferences.getFloat("latitudSharedPreferenceWeb", 0);
            float longitudPreferenceWeb = preferences.getFloat("longitudSharedPreferenceWeb", 0);
            int idUsuarioPreferenceWeb = preferences.getInt("idUsuarioSharedPreferenceWeb", 0);
            int idStaffPreferenceWeb = preferences.getInt("idStaffSharedPreferenceWeb", 0);
            int validaUbicacionGPS = preferences.getInt("validaUbicacionGPS", 1);

            Globals.getInstance().validaUbicacionGPS = validaUbicacionGPS;
            Globals.getInstance().latitud = latitudPreference;
            Globals.getInstance().longitud = longitudPreference;
            Globals.getInstance().latitudWebServ = latitudPreferenceWeb;
            Globals.getInstance().longitudWebServ = longitudPreferenceWeb;
            Globals.getInstance().idUsuario = idUsuarioPreferenceWeb;
            Globals.getInstance().idStaff = idStaffPreferenceWeb;
        }

        /* Enlazar mapa de API de Google para que se muestre*/
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* Cargar los compenentes que se utilizarán*/
        btnCheckIn = view.findViewById(R.id.btnCheckIn);
        btnCheckOut = view.findViewById(R.id.btnCheckOut);
        tvUbicacion =  view.findViewById(R.id.tvUbicacion);
        tvTrabajo = view.findViewById(R.id.tvTrabajo);

        cltBotonesPreferences = leerSharedPreferences("ctlBotones");

        if (cltBotonesPreferences.equals("verdadero")){
            Globals.getInstance().ctlBotones = true;
            btnCheckIn.setEnabled(false);
            btnCheckOut.setEnabled(true);
        }else{
            Globals.getInstance().ctlBotones = false;
            btnCheckIn.setEnabled(true);
            btnCheckOut.setEnabled(false);
        }

        /* Método que sirve para obtener las coordenadas cada cierto periodo de tiempo*/
        getCoordenadas();
        /* Método que sirve para obtener el número de tareas de cada usuario logeado*/
        getNumTareas();
        /* Método que sirve para mostrar el mapa con tu ubicación exacta*/
        verUbicacion();
        verUbicacionTrabajo();
        /* Método que sirve para cargar el botón para hacer registro de la entrada*/
        btnCheckEntrada();
        /* Método que sirve para cargar el botón para hacer registro de la salida*/
        btnCheckSalida();

        /* Saca fecha para mandar los valores */
        //Fecha actual desglosada:
        Calendar fecha = Calendar.getInstance();
        int anio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH) + 1;
        int dia = fecha.get(Calendar.DAY_OF_MONTH);

        String mesString;

        switch (mes) {
            case 1:  mesString = "Enero";
                break;
            case 2:  mesString  = "Febrero";
                break;
            case 3:  mesString = "Marzo";
                break;
            case 4:  mesString = "Abril";
                break;
            case 5:  mesString = "Mayo";
                break;
            case 6:  mesString = "Junio";
                break;
            case 7:  mesString = "Julio";
                break;
            case 8:  mesString = "Agosto";
                break;
            case 9:  mesString = "Septiembre";
                break;
            case 10: mesString = "Octubre";
                break;
            case 11: mesString = "Noviembre";
                break;
            case 12: mesString = "Diciembre";
                break;
            default: mesString = "Invalid month";
                break;
        }

        fecha_letra = mesString + "," + dia;
        //fecha_letra = dia + " de " + mesString;

        final String nombrePreferenceWeb = preferences.getString("nombreSharedPreferenceWeb", "");

        coordinatorLayout = view.findViewById(R.id.main_content);
        tvtitle = view.findViewById(R.id.tvtitle);
        tvtitle_2 = view.findViewById(R.id.tvtitle_2);
        tvtitle.setText("Mapa completo");
        tvtitle_2.setText(fecha_letra);


        bottom_sheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        //bottom_sheet.setBackgroundResource(R.drawable.bottom_sheet_round);

        btn_expand = getContext().getResources().getDrawable( R.drawable.ic_bottom_sheet_expand );
        btn_hidden = getContext().getResources().getDrawable( R.drawable.ic_bottom_sheet_hidden );

        tvtitle.setCompoundDrawablesWithIntrinsicBounds( btn_hidden, null, null, null);

        //tvtitle.setTextColor(tvtitle.getContext().getResources().getColor(R.color.colorBlack));
        //tvtitle_2.setTextColor(tvtitle_2.getContext().getResources().getColor(R.color.colorBlackGris));

        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottom_sheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:{
                        //Toast.makeText(getContext(), "STATE_HIDDEN", Toast.LENGTH_SHORT).show();
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                        /*Toast.makeText(getContext(), "STATED_EXPANDED", Toast.LENGTH_SHORT).show();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Date date = new Date();*/

                        tvtitle.setText("Mapa completo");
                        tvtitle_2.setText(fecha_letra);

                        tvtitle.setCompoundDrawablesWithIntrinsicBounds( btn_hidden, null, null, null);

                        //bottom_sheet.setBackgroundResource(R.drawable.bottom_sheet_round_hidden);
                        //tvtitle.setTextColor(tvtitle.getContext().getResources().getColor(R.color.colorWhite));
                        //tvtitle_2.setTextColor(tvtitle_2.getContext().getResources().getColor(R.color.colorWhite));
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        //This
                        //Toast.makeText(buttom_sheet_Activity.this, "STATED_COLLAPSED", Toast.LENGTH_SHORT).show();
                        tvtitle.setText("Más opciones");
                        tvtitle_2.setText(nombrePreferenceWeb);

                        tvtitle.setCompoundDrawablesWithIntrinsicBounds( btn_expand, null, null, null);

                        //bottom_sheet.setBackgroundResource(R.drawable.bottom_sheet_round);
                        //tvtitle.setTextColor(tvtitle.getContext().getResources().getColor(R.color.colorBlack));
                        //tvtitle_2.setTextColor(tvtitle_2.getContext().getResources().getColor(R.color.colorBlackGris));
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:{
                        //Toast.makeText(getContext(), "STATED_DRAGGING", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case BottomSheetBehavior.STATE_SETTLING:{
                        //Toast.makeText(getContext(), "STATED_SETTLING", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        return view;
    }

    public void guardarSharedPreferences(String keyPref, String valor) {
        SharedPreferences settings = (getContext()).getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(keyPref, valor);
        editor.apply();
    }

    public String leerSharedPreferences(String keyPref) {
        SharedPreferences preferences = (getContext()).getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getString(keyPref, "");
    }

    private void btnCheckEntrada() {
        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Globals.getInstance().ctlUbicacionCorrecta){
                    Toast.makeText(getContext(), "¡No puede registrar su entrada porque no esta en la ubicación de trabajo!", Toast.LENGTH_SHORT).show();
                    btnCheckIn.setEnabled(true);
                    btnCheckOut.setEnabled(false);
                }else{
                    registrarCheckIn();
                }
            }
            void registrarCheckIn(){
                String URL = "http://duvitapp.com/WebService/actualizarAsistencia.php?idStaff="+ Globals.getInstance().idStaff+"&tipo=0";
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getContext(), "¡Ha registrado su entrada correctamente!", Toast.LENGTH_SHORT).show();
                                guardarSharedPreferences("ctlBotones", "verdadero");
                                btnCheckIn.setEnabled(false);
                                btnCheckOut.setEnabled(true);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { Toast.makeText(getContext(), "Error al hacer conexion: " + error.getMessage(), Toast.LENGTH_SHORT).show(); }
                });requestQueue.add(jsonObjectRequest);
            }
        });
    }

    private void btnCheckSalida() {
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Globals.getInstance().ctlUbicacionCorrecta){
                    Toast.makeText(getContext(), "¡No puede registrar su salida porque no esta en la ubicación de trabajo!", Toast.LENGTH_SHORT).show();
                    guardarSharedPreferences("ctlBotones", "verdadero");
                    btnCheckIn.setEnabled(false);
                    btnCheckOut.setEnabled(true);
                }else{

                    String cltBotonesTareasPreferences = leerSharedPreferences("cltBotonesTareas");
                    if (cltBotonesTareasPreferences.equals("empezar")){

                        androidx.appcompat.app.AlertDialog.Builder dialogo = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                        dialogo.setTitle("Notificación");
                        dialogo.setMessage("No puede registrar su salida porque tiene una o más tareas iniciadas.");
                        dialogo.setCancelable(false);
                        dialogo.setPositiveButton("DE ACUERDO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo, int id) {
                                dialogo.dismiss();
                            }
                        });
                        dialogo.show();

                    }else{
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                        dialogo1.setTitle("SALIDA");
                        dialogo1.setMessage("¿Estas seguro de registrar su salida ahora?");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                String URL = "http://duvitapp.com/WebService/actualizarAsistencia.php?idStaff="+ Globals.getInstance().idStaff+"&tipo=1";
                                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Toast.makeText(getContext(), "Se ha registrado su salida exitosamente", Toast.LENGTH_SHORT).show();
                                                guardarSharedPreferences("ctlBotones", "falso");
                                                btnCheckIn.setEnabled(true);
                                                btnCheckOut.setEnabled(false);
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) { Toast.makeText(getContext(), "Error al hacer conexion: " + error.getMessage(), Toast.LENGTH_SHORT).show(); }
                                });
                                requestQueue.add(jsonObjectRequest);
                            }
                        });
                        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) { dialogo1.dismiss(); }
                        });dialogo1.show();

                    }
                }
            }
        });
    }

    private void getCoordenadas() {
        locationManager = (LocationManager) (getContext()).getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Globals.getInstance().latitud = location.getLatitude();
                Globals.getInstance().longitud = location.getLongitude();

                SharedPreferences settings = (getContext()).getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat("latitudSharedPreference", (float) location.getLatitude());
                editor.putFloat("longitudSharedPreference", (float) location.getLongitude());
                editor.apply();

                //Toast.makeText(getContext(), "Datos: " + "Latitud: " + location.getLatitude() + ", Longitud: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            public void onProviderEnabled(String provider) { Toast.makeText(getContext(), "GPS Activado", Toast.LENGTH_SHORT).show(); }
            public void onProviderDisabled(String provider) {
                if (Globals.getInstance().cltPreguntaGpsFramentPrincipal){
                    preguntarGPS();
                }
            }
        };
        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0, locationListener);
    }

    private void preguntarGPS() {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
            builder.setTitle("¡Usted ha desactivado su GPS!");
            builder.setMessage("Para seguir utilizando la App debe volver a activarlo en modo de alta precisión: GPS, Wi-Fi y redes móviles.");
            builder.setCancelable(false);
            builder.setPositiveButton("Si, de acuerdo", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS ); startActivity(myIntent);
                    Globals.getInstance().cltPreguntaGpsFramentPrincipal = false;
                }
            }).setNegativeButton("No, salir", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { System.exit(0); }
            });
            builder.create();
            builder.show();
    }

    private CountDownTimer timer;
    private void countDownTimer(){
        timer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) { Log.e("seconds remaining: ", "¡TIEMPO! ===== "+ millisUntilFinished / 1000); }
            public void onFinish() {
                onMapReady(mMap);
                verUbicacion();
            }
        };
        timer.start();
    }

    private void getNumTareas() {
        String URL = "http://duvitapp.com/WebService/datos.php?idStaff=" + Globals.getInstance().idStaff;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                            }else{ Toast.makeText(getContext(), "No tienes tareas pendientes. ¡Good Job!.", Toast.LENGTH_SHORT).show(); }
                        } catch (JSONException e) { Toast.makeText(getContext(), "Algo salió mal. Error al realizar la consulta de datos." + e.getMessage(), Toast.LENGTH_SHORT).show(); }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { Toast.makeText(getContext(), "Error al hacer conexión con el servidor. ", Toast.LENGTH_SHORT).show(); }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @SuppressLint("SetTextI18n")
    private void verUbicacion() {
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(
                    Globals.getInstance().latitud, Globals.getInstance().longitud, 1);
            if (!list.isEmpty()) {
                Address DirCalle = list.get(0);
                tvUbicacion.setText(DirCalle.getAddressLine(0));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void verUbicacionTrabajo() {
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(
                    Globals.getInstance().latitudWebServ, Globals.getInstance().longitudWebServ, 1);
            if (!list.isEmpty()) {
                Address DirCalle = list.get(0);
                if (Globals.getInstance().validaUbicacionGPS == 0){
                    tvTrabajo.setText("No se necesita ubicación.");
                }else{
                    tvTrabajo.setText(DirCalle.getAddressLine(0));
                }

            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set a preference for minimum and maximum zoom.
        mMap.setMinZoomPreference(11.0f);
        mMap.setMaxZoomPreference(19.0f);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        for (Marker marker:realTimeMarker){
            marker.remove();
        }
        LatLng latLng = new LatLng(Globals.getInstance().latitud, Globals.getInstance().longitud);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Estoy aquí");
        markerOptions.snippet("Ubicación en tiempo real.");
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(11));
        markerOptions.visible(true);

        //Estas coordenadas se obtendran de la base de datos: Nota: cambiar en ambas partes CIRCLE.
        LatLng sydney = new LatLng(Globals.getInstance().latitudWebServ, Globals.getInstance().longitudWebServ);

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Trabajo").snippet("Debe estar aquí.")).showInfoWindow();

        MarkerOptions markerOptionsSydney = new MarkerOptions();
        markerOptionsSydney.position(sydney);
        markerOptionsSydney.title("Trabajo");
        markerOptionsSydney.snippet("Debe estar aquí.");

        //markerOptionsSydney.icon(BitmapDescriptorFactory.fromResource(R.drawable.duvit_icono));
        //markerOptionsSydney.icon(BitmapDescriptorFactory.defaultMarker(12));
        markerOptionsSydney.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        markerOptionsSydney.visible(true);
        mMap.addMarker(markerOptionsSydney).showInfoWindow();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        //mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(Globals.getInstance().latitudWebServ, Globals.getInstance().longitudWebServ))
                .radius(30)
                .strokeColor(Color.rgb(178, 225, 242))
                .fillColor(Color.rgb(232, 246, 251)));
        float[] disResultado = new float[2];
        Location.distanceBetween( Globals.getInstance().latitud, Globals.getInstance().longitud,
                circle.getCenter().latitude,
                circle.getCenter().longitude,
                disResultado);

        //0 = no valida
        if (Globals.getInstance().validaUbicacionGPS == 0){
            Globals.getInstance().ctlUbicacionCorrecta = true;
        }else{
            if(disResultado[0] > circle.getRadius()){
                Globals.getInstance().ctlUbicacionCorrecta = false;
            } else {
                Globals.getInstance().ctlUbicacionCorrecta = true;
            }
        }

        if (Globals.getInstance().cltZoomMap){

            if (mMap != null){

                if(disResultado[0] > circle.getRadius()){

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(markerOptions.getPosition());
                    builder.include(markerOptionsSydney.getPosition());

                    LatLngBounds bounds = builder.build();

                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = getResources().getDisplayMetrics().heightPixels;
                    int padding = (int) (width * 0.38);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));

                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5f));
                }

                Globals.getInstance().cltZoomMap = false;
            }

        }

        tmpRealTimeMarker.add(mMap.addMarker(markerOptions));
        realTimeMarker.clear();
        realTimeMarker.addAll(tmpRealTimeMarker);
        countDownTimer();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer.cancel();
        locationManager.removeUpdates(locationListener);
        Globals.getInstance().cltZoomMap = true;
    }
    @Override
    public void onResume() {
        super.onResume();

        if (!Globals.getInstance().cltPreguntaGpsFramentPrincipal){
            locationManager = (LocationManager) (getContext()).getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                preguntarGPS();
            }else{
                getCoordenadas();
            }
        }
    }
}
