package com.example.pice.duvit.NAVEGACION;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.example.pice.duvit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class DetallesTareasActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    static String PREFS_KEY = "mispreferencias";
    TextView tv_title_content;
    SharedPreferences preferences;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        boolean modo_oscuro_on = preferences.getBoolean("modo_oscuro_on", false);

        if (modo_oscuro_on) {
            setTheme(R.style.FeedActivityThemeDark);
        } else {
            setTheme(R.style.FeedActivityThemeLight);
        }
        setContentView(R.layout.activity_slider_tareas);

        fullscreenview();
        tv_title_content = findViewById(R.id.tv_title_content);
        tv_title_content.setText("Detalles - Tareas");

        /* Variables de control SharedPreferences */
        SharedPreferences preferences = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        /* Control de botones enabled y disabled */
        String cltBotonesPreferences = preferences.getString("ctlBotones", "");
        if (cltBotonesPreferences.equals("verdadero") || cltBotonesPreferences.equals("finalizar")) {
            Globals.getInstance().ctlBotones = true;
        } else {
            Globals.getInstance().ctlBotones = false;
        }

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

    }

    private void fullscreenview() {
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_slider_tareas, menu);
        return true;
    }

    public static class PlaceholderFragment extends Fragment {

        TextView tvActividad, tvTarea, tvDetalles, tvNombreProyecto, tvIdPlaneacion;
        JSONArray jsonArray;
        Button btnStart, btnResume, btnFinish;
        TextView tvNota;

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.item_slider_tareas, container, false);

            tvActividad = (TextView) rootView.findViewById(R.id.tvActividad);
            tvTarea = (TextView) rootView.findViewById(R.id.tvTarea);
            tvDetalles = (TextView) rootView.findViewById(R.id.tvDetalles);
            btnStart = (Button) rootView.findViewById(R.id.btnStart);
            btnResume = (Button) rootView.findViewById(R.id.btnResume);
            btnFinish = (Button) rootView.findViewById(R.id.btnFinish);
            tvNota = (TextView) rootView.findViewById(R.id.tvNota);
            tvNombreProyecto = (TextView) rootView.findViewById(R.id.tvNombreProyecto);
            tvIdPlaneacion = (TextView) rootView.findViewById(R.id.tvIdPlaneacion);

            cargarDatosTareas();

            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int idPlanaacion = Integer.parseInt(tvIdPlaneacion.getText().toString());
                    Globals.getInstance().idPlaneacion = idPlanaacion;
                    String URL = "http://duvitapp.com/WebService/registrarTarea.php?idPlaneacion=" + idPlanaacion + "&tipo=0";
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                            new Response.Listener<JSONObject>() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onResponse(JSONObject response) {
                                    btnStart.setEnabled(false);
                                    btnResume.setEnabled(true);
                                    btnFinish.setEnabled(true);

                                    SharedPreferences settings = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("cltBotonesTareas", "empezar");
                                    editor.apply();

                                    Toast.makeText(getContext(), "Se ha registrado el inicio de la tarea.", Toast.LENGTH_SHORT).show();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Error al registrar fecha de seguimiento", Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                }
            });
            btnResume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int idPlanaacion = Integer.parseInt(tvIdPlaneacion.getText().toString());
                    Globals.getInstance().idPlaneacion = idPlanaacion;
                    String URL = "http://duvitapp.com/WebService/registrarTarea.php?idPlaneacion=" + idPlanaacion + "&tipo=1";
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                            new Response.Listener<JSONObject>() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onResponse(JSONObject response) {
                                    btnStart.setEnabled(true);
                                    btnResume.setEnabled(false);
                                    btnFinish.setEnabled(false);

                                    SharedPreferences settings = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("cltBotonesTareas", "pausar");
                                    editor.apply();

                                    Toast.makeText(getContext(), "Se ha registrado el seguimiento de la tarea.", Toast.LENGTH_SHORT).show();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Error al registrar fecha", Toast.LENGTH_SHORT).show();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                }
            });
            btnFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder dialogo = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.alert));
                    dialogo.setTitle("Importante");
                    dialogo.setMessage("¿Seguro que desea finalizar la tarea?");
                    dialogo.setCancelable(false);
                    dialogo.setPositiveButton("Si, finalizar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo, int id) {

                            int idPlanaacion = Integer.parseInt(tvIdPlaneacion.getText().toString());
                            String URL = "http://duvitapp.com/WebService/registrarTarea.php?idPlaneacion=" + idPlanaacion + "&tipo=2";
                            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                                    new Response.Listener<JSONObject>() {
                                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            btnStart.setEnabled(true);
                                            btnResume.setEnabled(false);
                                            btnFinish.setEnabled(false);

                                            SharedPreferences settings = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("cltBotonesTareas", "finalizar");
                                            editor.apply();

                                            Toast.makeText(getContext(), "Se ha registrado la tarea como finalizada, ¡felicidades!.", Toast.LENGTH_SHORT).show();

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(), "Error al registrar fecha", Toast.LENGTH_SHORT).show();
                                }
                            });
                            requestQueue.add(jsonObjectRequest);
                            //OBTIENE NUEVAMENTE EL NÚMERO DE TAREAS
                            int num_pag = Globals.getInstance().numPages - 1;
                            Globals.getInstance().numPages = num_pag;
                            //Sacarlo al mapa para que cuando se vuelva a meter queda actualizado loas páginas.

                            getActivity().finish();
                            startActivity(Objects.requireNonNull(getActivity()).getIntent());

                        }
                    });
                    dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo, int id) {
                            dialogo.dismiss();
                        }
                    });
                    dialogo.show();
                }
            });
            return rootView;
        }

        int setIdProyecto;

        private void cargarDatosTareas() {
            String URL = "http://duvitapp.com/WebService/datos.php?idStaff=" + Globals.getInstance().idStaff;
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                jsonArray = response.getJSONArray("tareas");
                                if (jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject tareas = jsonArray.getJSONObject(i);
                                        if (getArguments().getInt(ARG_SECTION_NUMBER) == i + 1) {
                                            String idPlaneacion = tareas.getString("idPlaneacion");
                                            String actividad = tareas.getString("actividad");
                                            String tarea = tareas.getString("tarea");
                                            String datalle = tareas.getString("detalle");
                                            int idEstatusTarea = Integer.parseInt(tareas.getString("idEstatusTarea"));
                                            final String proyecto = tareas.getString("proyecto");
                                            setIdProyecto = Integer.parseInt(tareas.getString("idProyecto"));

                                            tvIdPlaneacion.setText(idPlaneacion);

                                            SpannableString mitextoU = new SpannableString(proyecto);
                                            mitextoU.setSpan(new UnderlineSpan(), 0, mitextoU.length(), 0);
                                            tvNombreProyecto.setText(mitextoU);

                                            tvNombreProyecto.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent inten = new Intent(getContext(), TareasProyectoActivity.class);
                                                    inten.putExtra("setIdProyecto", setIdProyecto);
                                                    inten.putExtra("name_proyecto", proyecto);
                                                    startActivity(inten);

                                                }
                                            });
                                            tvActividad.setText(actividad);
                                            tvTarea.setText(tarea);
                                            tvDetalles.setText(datalle);
                                            if (!Globals.getInstance().ctlBotones) {
                                                btnStart.setEnabled(false);
                                                btnResume.setEnabled(false);
                                                btnFinish.setEnabled(false);

                                                tvNota.setText("Nota: Aún no ha registrado su entrada.");
                                            } else {

                                                SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
                                                String cltBotonesTareasPreferences = preferences.getString("cltBotonesTareas", "");

                                                if (cltBotonesTareasPreferences.equals("empezar")) {
                                                    btnStart.setEnabled(false);
                                                    btnResume.setEnabled(true);
                                                    btnFinish.setEnabled(true);

                                                } else if (cltBotonesTareasPreferences.equals("pausar")) {
                                                    btnStart.setEnabled(true);
                                                    btnResume.setEnabled(false);
                                                    btnFinish.setEnabled(false);

                                                } else if (cltBotonesTareasPreferences.equals("finalizar")) {
                                                    btnStart.setEnabled(true);
                                                    btnResume.setEnabled(false);
                                                    btnFinish.setEnabled(false);
                                                } else {
                                                    btnStart.setEnabled(true);
                                                    btnResume.setEnabled(false);
                                                    btnFinish.setEnabled(false);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(getContext(), "No hay datos de Tareas", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Error al hacer conexion: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return Globals.getInstance().numPages;
        }
    }
}
