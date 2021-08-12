package com.example.pice.duvit.NAVEGACION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class JugarActivity extends AppCompatActivity {

    static String PREFS_KEY = "mispreferencias";
    TextView tv_title_content;
    SharedPreferences preferences;

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

        setContentView(R.layout.activity_jugar);

        fullscreenview();
        tv_title_content = findViewById(R.id.tv_title_content);
        tv_title_content.setText("Jugar");

        /* Code */



        /* End code */

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
