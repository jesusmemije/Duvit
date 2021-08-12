package com.example.pice.duvit.NAVEGACION;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class PuntuacionActivity extends AppCompatActivity {

    SharedPreferences preferences;
    static String PREFS_KEY = "mispreferencias";
    String tema;
    ImageView ivAvatar;
    int idGenero;
    TextView tv_title_content;

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

        setContentView(R.layout.activity_puntuacion);

        fullscreenview();

        /*ivAvatar = findViewById(R.id.ivAvatar);*/
        tv_title_content = findViewById(R.id.tv_title_content);
        tv_title_content.setText("Puntuación");

        preferences = PuntuacionActivity.this.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        tema = preferences.getString("temaSharedPreferenceWeb", "default");
        idGenero = preferences.getInt("idGeneroSharedPreferenceWeb", 0);

        /*if (tema.equals("medieval")) {
            if (idGenero == 1) {
                ivAvatar.setImageResource(R.drawable.medievalavatarhombre);
            } else if (idGenero == 2) {
                ivAvatar.setImageResource(R.drawable.medievalavatarmujer);
            } else {
                ivAvatar.setImageResource(R.drawable.duvit_icono);
            }
        } else if (tema.equals("futbol")) {
            if (idGenero == 1) {
                ivAvatar.setImageResource(R.drawable.footballavatarhombre);
            } else if (idGenero == 2) {
                ivAvatar.setImageResource(R.drawable.footballavatarmujer);
            } else {
                ivAvatar.setImageResource(R.drawable.duvit_icono);
            }
        } else if (tema.equals("polar")) {
            if (idGenero == 1) {
                ivAvatar.setImageResource(R.drawable.footballavatarhombre);
            } else if (idGenero == 2) {
                ivAvatar.setImageResource(R.drawable.footballavatarmujer);
            } else {
                ivAvatar.setImageResource(R.drawable.duvit_icono);
            }
        } else {
            if (idGenero == 1) {
                ivAvatar.setImageResource(R.drawable.defaultavatarhombre);
            } else if (idGenero == 2) {
                ivAvatar.setImageResource(R.drawable.defaultavatarmujer);
            } else {
                ivAvatar.setImageResource(R.drawable.duvit_icono);
            }
        } */

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
