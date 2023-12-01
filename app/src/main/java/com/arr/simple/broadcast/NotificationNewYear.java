package com.arr.simple.broadcast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationManagerCompat;
import com.arr.simple.R;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;

public class NotificationNewYear extends BroadcastReceiver {

    private String CHANNEL_ID = "SIMple";
    private String CHANNEL_NAME = "New Year";
    private String CHANNEL_DESCRIPTION = "Notificación por nuevo año";

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        if (currentDay == 1 && currentMonth == Calendar.JANUARY) {

            if (intent.getAction().equals("com.arr.simple.NOTIFICATION")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel =
                            new NotificationChannel(
                                    CHANNEL_ID,
                                    CHANNEL_NAME,
                                    NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription(CHANNEL_DESCRIPTION);
                    channel.enableVibration(true);
                    channel.setShowBadge(true);
                    channel.setSound(null, null);
                    NotificationManager notiManager =
                            context.getSystemService(NotificationManager.class);
                    notiManager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_logo_simple)
                                .setContentTitle("¡Feliz año nuevo!")
                                .setStyle(
                                        new NotificationCompat.BigTextStyle()
                                                .bigText(
                                                        context.getString(R.string.happy_new_year)))
                                .setContentText(context.getString(R.string.happy_new_year))
                                .setShowWhen(false)
                                .setSound(null)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(context);
                notificationManager.notify(23, builder.build());
            }
        }
    }
}
