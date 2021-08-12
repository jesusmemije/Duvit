package com.example.pice.duvit.NAVEGACION;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pice.duvit.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;

public class EstadisticasActivity extends AppCompatActivity {

    PieChart pieChart;
    static String PREFS_KEY = "mispreferencias";
    TextView tv_title_content;
    SharedPreferences preferences;

    @SuppressLint("SetTextI18n")
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

        setContentView(R.layout.activity_estadisticas);

        fullscreenview();
        tv_title_content = findViewById(R.id.tv_title_content);
        tv_title_content.setText("Estadísticas");

        pieChart = findViewById(R.id.pieChart);
        pieChart.setUsePercentValues(true);
        ArrayList<Entry> valores = new ArrayList<Entry>();
        valores.add(new Entry(8f, 0));
        valores.add(new Entry(15f, 1));
        valores.add(new Entry(12f, 2));
        PieDataSet dataSet = new PieDataSet(valores, "Leyenda.");
        ArrayList<String> titulo = new ArrayList<String>();
        titulo.add("Tiempo");
        titulo.add("Dificultad");
        titulo.add("Eficiencia");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        PieData data = new PieData(titulo, dataSet);
        pieChart.setData(data);
        //pieChart.setDescription("Desempeño general.");

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
}
