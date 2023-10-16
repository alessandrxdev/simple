package com.arr.simple.broadcast;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.arr.services.ResponseUssd;
import com.arr.services.utils.ussd.SendUssdUtils;
import com.arr.simple.MainActivity;
import com.arr.simple.R;

public class NotificationBalances extends BroadcastReceiver {

    private final String CHANNEL_ID = "Balances";
    private final String CHANNEL_NAME = "Balances";
    private final String CHANNEL_DESCRIPTION =
            "Muestra información de sus balances en la barra de notificaciones";
    private SendUssdUtils ussd;
    private ResponseUssd response;
    private PendingIntent pendingBalances;

    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO: UssdUtils
        ussd = new SendUssdUtils(context);
        response = new ResponseUssd(ussd);

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("I: ").append(response.allData());
        strBuilder.append(" | ");
        strBuilder.append("LTE: ").append(response.dataLte());
        strBuilder.append(" | ");
        strBuilder.append("CU: ").append(response.nacionales());
        strBuilder.append(" | ").append("V: ").append(response.venceAllData());
        String content = strBuilder.toString();

        // open app
        Intent action = new Intent(context, MainActivity.class);
        action.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, action, PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableVibration(false);
            channel.setShowBadge(false);
            channel.setSound(null, null);
            NotificationManager notiManager = context.getSystemService(NotificationManager.class);
            notiManager.createNotificationChannel(channel);
        }

        // sync balances

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
        } else {
            Intent intentBalance =
                    new Intent(context.getApplicationContext(), BalanceBroadcast.class);
            int flag = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flag = PendingIntent.FLAG_IMMUTABLE;
            }
            pendingBalances = PendingIntent.getBroadcast(context, 0, intentBalance, flag);
        }

        // Crea y muestra la notificación
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo_simple)
                        .setContentTitle(CHANNEL_NAME)
                        .setContentText(content)
                        .setContentIntent(pendingIntent)
                        .addAction(0, "Actualizar", pendingBalances)
                        .setShowWhen(false)
                        .setSound(null)
                        .setOngoing(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(22, builder.build());
    }
}
