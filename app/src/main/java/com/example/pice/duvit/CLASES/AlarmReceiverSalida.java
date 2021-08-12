package com.example.pice.duvit.CLASES;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.pice.duvit.ACCESO.LoginActivity;
import com.example.pice.duvit.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiverSalida extends BroadcastReceiver {

    private PendingIntent pendingIntent2;
    private final static String CHANNEL_ID = "NOTIFICACION2";
    public final static int NOTIFICACION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Log", "Entra a receiver Salida");

        setPendingIntent(context);
        createNotificacionChannel(context);
        createNotificacion(context);
    }

    private void createNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Salida";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotificacionChannel(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_icon_notification);
        builder.setContentTitle("Notificación Duvit");
        builder.setContentText("Duvit te recuerda registrar tu salida");
        builder.setSubText("Registrar salida");
        builder.setColor(context.getResources().getColor(R.color.colorPrimary));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.MAGENTA, 1000, 1000);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

        builder.setContentIntent(pendingIntent2);
        //builder.addAction(R.drawable.ic_sesmejor_notificacion, "Entrar", pendingIntent2);
        builder.setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context.getApplicationContext());
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
    }

    private void setPendingIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        pendingIntent2 = stackBuilder.getPendingIntent(2, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
