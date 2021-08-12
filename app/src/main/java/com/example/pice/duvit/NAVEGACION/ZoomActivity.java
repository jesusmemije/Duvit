package com.example.pice.duvit.NAVEGACION;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pice.duvit.CLASES.Constants;
import com.example.pice.duvit.R;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;

import static com.example.pice.duvit.NAVEGACION.HomeNavegacionActivity.PREFS_KEY;

public class ZoomActivity extends AppCompatActivity implements Constants, ZoomSDKInitializeListener, MeetingServiceListener, ZoomSDKAuthenticationListener {

    EditText etMeetingNo;
    Button btnUnirse;
    SharedPreferences preferences;
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
        setContentView(R.layout.activity_zoom);

        fullscreenview();
        tv_title_content = findViewById(R.id.tv_title_content);
        tv_title_content.setText("Reuniones virtuales");

        etMeetingNo = findViewById(R.id.etMeetingNo);
        btnUnirse = findViewById(R.id.btnUnirse);

        etMeetingNo.setText("5198156129");

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if(savedInstanceState == null) {
            zoomSDK.initialize(this, APP_KEY, APP_SECRET, WEB_DOMAIN, this);
        }

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

    public void onClickBtnJoinMeeting(View view) {

        // Step 1: Get meeting number from input field.
        String meetingNo = etMeetingNo.getText().toString().trim();
        // Check if the meeting number is empty.
        if(meetingNo.length() == 0) {
            Toast.makeText(this, "You need to enter a meeting number/ vanity id which you want to join.", Toast.LENGTH_LONG).show();
            return;
        }

        // Step 2: Get Zoom SDK instance.
        //zoomSDK = ZoomSDK.getInstance();
        // Check if the zoom SDK is initialized
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if(!zoomSDK.isInitialized()) {
            Toast.makeText(this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
            return;
        }

        // Step 3: Get meeting service from zoom SDK instance.
        MeetingService meetingService = zoomSDK.getMeetingService();

        // Step 4: Configure meeting options.
        JoinMeetingOptions opts = new JoinMeetingOptions();

        // Some available options
        //		opts.no_driving_mode = true;
        //		opts.no_invite = true;
        //		opts.no_meeting_end_message = true;
        //		opts.no_titlebar = true;
        //		opts.no_bottom_toolbar = true;
        //		opts.no_dial_in_via_phone = true;
        //		opts.no_dial_out_to_phone = true;
        //		opts.no_disconnect_audio = true;
        //		opts.no_share = true;
        //		opts.invite_options = InviteOptions.INVITE_VIA_EMAIL + InviteOptions.INVITE_VIA_SMS;
        //		opts.no_audio = true;
        //		opts.no_video = true;
        //		opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE;
        //		opts.no_meeting_error_message = true;
        //		opts.participant_id = "participant id";

        // Step 5: Setup join meeting parameters
        JoinMeetingParams params = new JoinMeetingParams();

        params.displayName = "Hello World From Zoom SDK";
        params.meetingNo = meetingNo;

        // Step 6: Call meeting service to join meeting
        meetingService.joinMeetingWithParams(this, params, opts);
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
    public void onZoomSDKInitializeResult(int i, int i1) {

    }

    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int i, int i1) {

    }

    @Override
    public void onZoomSDKLoginResult(long l) {

    }

    @Override
    public void onZoomSDKLogoutResult(long l) {

    }

    @Override
    public void onZoomIdentityExpired() {

    }

    @Override
    public void onZoomAuthIdentityExpired() {

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
