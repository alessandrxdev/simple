package com.arr.simple.broadcast;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.arr.simple.R;

import com.arr.ussd.utils.UssdUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateBalances extends BroadcastReceiver {

    private final String[] ussdCodes = {
            "*222#", "*222*328#", "*222*266#", "*222*767#", "*222*869#"
    };
    private final String[] ussdKeys = {"saldo", "datos", "bonos", "sms", "min"};
    private SharedPreferences spBalance;
    private SharedPreferences.Editor editor;
    private UssdUtils ussd;
    private static final String CHANNEL_ID = "Balances";
    private static final String CHANNEL = "Update balances";
    private Context mContext;
    private String SIM;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Log.e("Update", "onRecive called");

        // TODO: SharedPreferences
        spBalance = PreferenceManager.getDefaultSharedPreferences(context);
        editor = spBalance.edit();
        SIM = spBalance.getString("sim", "0");

        // ussd
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ussd = new UssdUtils(context);
        }
        Handler handler = new Handler(Looper.getMainLooper());
        executeUssdRequest(handler, 0);

        /*
        String spTime = spBalance.getString("update_balances", "0");
        int time = Integer.parseInt(spTime);

        if (time == 1) {
            Handler handler = new Handler(Looper.getMainLooper());
            executeUssdRequest(handler, 0);
        } else if (time == 2) {
            Handler handler = new Handler(Looper.getMainLooper());
            executeUssdRequest(handler, 0);
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void executeUssdRequest(Handler handler, int index) {
        if (index >= ussdCodes.length) {
            // Se han realizado todas las consultas
            updateHora();
            boolean isChecked = spBalance.getBoolean("not_update_balances", false);
            if (!isChecked) {
                createNotification(mContext, "Balances", "¡Se han actualizado sus balances!");
            }

            // actualizar notificación
            boolean isNotifi = spBalance.getBoolean("balance_notif", true);
            if (!isNotifi) {
                Intent broadcast = new Intent(((Activity) mContext), NotificationBalances.class);
                mContext.sendBroadcast(broadcast);
            }
            return;
        }
        String ussdCode = ussdCodes[index];
        String ussdKey = ussdKeys[index];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ussd.execute(Integer.parseInt(SIM), ussdCode, ussdKey);
        }
        /*
        handler.po stDelayed(
                () -> {
                    String response = ussd.response(ussdKey);
                    if (!response.isBlank()) {
                        executeUssdRequest(handler, index + 1);
                    } else {
                        executeUssdRequest(handler, index);
                    }
                },
                5000);

         */
    }

    private void updateHora() {
        Calendar calendar = Calendar.getInstance();
        Date dat = calendar.getTime();
        SimpleDateFormat datFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String hActual = datFormat.format(dat);
        editor.putString("actualizado", "Última actualización: " + hActual.toString());
        editor.apply();
    }

    private void createNotification(Context context, String title, String message) {
        // Crear un canal de notificación para Android 8.0 y versiones posteriores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID, CHANNEL, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Crear la notificación utilizando NotificationCompat.Builder
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo_simple)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        // Mostrar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(0, builder.build());
    }
}
