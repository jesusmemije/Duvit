package com.example.pice.duvit.CLASES;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pice.duvit.NAVEGACION.Globals;
import com.example.pice.duvit.NAVEGACION.HomeNavegacionActivity;
import com.example.pice.duvit.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    // [START receive_message]
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            sendNotification(remoteMessage);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage);
        }

    }
    // [END receive_message]

    // [START on_new_token]
    /*@Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        FirebaseMessaging.getInstance().subscribeToTopic("dispositivos");

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        String idusuario = String.valueOf(Globals.getInstance().idUsuario);

        if (!idusuario.equals("")){
            registrarToken(idusuario, token);
        }
    }
    // [END on_new_token]

    private void registrarToken(final String idusuario, String token) {

        String URL = "http://duvitapp.com/WebService/tokenNotificaciones.php?idusuario=" + idusuario + "&token=" + token + " ";
        String URL_LIMPIA = URL.replaceAll("\\s", "");

        RequestQueue requestQueue = Volley.newRequestQueue(this);
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
                                //Toast.makeText(MyFirebaseMessagingService.this, "[CODE : "+ code +"] " + mensaje, Toast.LENGTH_LONG).show();
                                Log.d(TAG, "[CODE : "+ code +"] " + mensaje);
                            } else if (code == 201) {
                                //Toast.makeText(getApplicationContext(), "" + mensaje, Toast.LENGTH_LONG).show();
                                Log.d(TAG, "[CODE : "+ code +"] " + mensaje);
                            } else {
                                //Si por alguna razón el server no arrojá nada.
                                Toast.makeText(MyFirebaseMessagingService.this, "Problema desconocido, intente nuevamente por favor.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(MyFirebaseMessagingService.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Problema al consumir el WS - Registrar token - MyFirebaseMessagingService: " + error.getMessage() , Toast.LENGTH_LONG).show();
                Log.d(TAG, "Problema al consumir el WS - Registrar token - MyFirebaseMessagingService: " + error.getMessage());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }*/

    private void sendNotification(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");

        Intent intent = new Intent(this, HomeNavegacionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_icon_notification)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }

    }
}