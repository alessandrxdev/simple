package com.arr.simple.broadcast;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import com.arr.simple.R;
import com.arr.ussd.ResponseUssd;
import com.arr.ussd.utils.UssdUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@RequiresApi(29)
public class BalancesBroadcast extends BroadcastReceiver {

    private UssdUtils ussd;
    private ResponseUssd response;

    private final String[] ussdCodes = {
        "*222#", "*222*328#", "*222*266#", "*222*767#", "*222*869#"
    };
    private final String[] ussdKeys = {"saldo", "datos", "bonos", "sms", "min"};

    private String SIM;
    private SharedPreferences spBalance;
    private SharedPreferences.Editor editor;

    private static final String CHANNEL_ID = "Balances";
    private static final String CHANNEL = "Update balances";

    @Override
    public void onReceive(Context context, Intent intent) {

        // import ussdUtils
        ussd = new UssdUtils(context);
        response = new ResponseUssd(ussd);
        Handler handler = new Handler(Looper.getMainLooper());
        executeUssdRequest(context, intent, handler, 0);

        // TODO: SharedPreferences
        spBalance = PreferenceManager.getDefaultSharedPreferences(context);
        editor = spBalance.edit();
        SIM = spBalance.getString("sim", "0");
    }

    private void updateWidget(Context mContext, Intent mIntent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] appWidgetIds = mIntent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        if (appWidgetIds != null) {
            for (int appWidgetId : appWidgetIds) {
                // Actualizar la vista del saldo del paquete
                RemoteViews views =
                        new RemoteViews(mContext.getPackageName(), R.layout.balances_widget);

                // actualiza vistas
                views.setTextViewText(R.id.appwidget_text_paquete, response.getDataAll());
                views.setTextViewText(
                        R.id.appwidget_text_lte, response.getLTE().replace(" LTE", ""));
                views.setTextViewText(R.id.appwidget_text_vence, response.getVenceData());
                views.setTextViewText(R.id.appwidget_text_minutos, response.getMinutos());
                views.setTextViewText(R.id.appwidget_text_sms, response.getMensajes());
                views.setTextViewText(
                        R.id.time_update,
                        spBalance
                                .getString("actualizado", "sin actualizar")
                                .replace("Última actualización: ", "").toString());
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }

    private void executeUssdRequest(Context context, Intent intent, Handler handler, int index) {
        if (index >= ussdCodes.length) {
            // update widget
            updateWidget(context, intent);
            updateTime();

            // mostrar u ocultar notificación de balances actualizados
            if (!spBalance.getBoolean("not_update_balances", false)) {
                notificationUpdate(
                        context,
                        context.getString(R.string.title_balances),
                        "¡Se ha actualizado sus balances!");
            }

            // actualizar notificacipn de balances
            if (!spBalance.getBoolean("balance_notif", false)) {
                Intent broadcast = new Intent(context, NotificationBalances.class);
                context.sendBroadcast(broadcast);
            }
            return;
        }
        String ussdCode = ussdCodes[index];
        String ussdKey = ussdKeys[index];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ussd.execute(Integer.parseInt("0"), ussdCode, ussdKey);
        }
        handler.postDelayed(
                () -> {
                    String response = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        response = ussd.response(ussdKey);
                    }
                    if (!response.isEmpty()) {
                        executeUssdRequest(context, intent, handler, index + 1);
                    } else {
                        executeUssdRequest(context, intent, handler, index);
                    }
                },
                5000);
    }

    // notificación con balances actualizados
    private void notificationUpdate(Context context, String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID, CHANNEL, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo_simple)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(0, builder.build());
    }

    // actualizar la hora de actualización de balances
    private void updateTime() {
        Calendar calendar = Calendar.getInstance();
        Date dat = calendar.getTime();
        SimpleDateFormat datFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String hActual = datFormat.format(dat);
        editor.putString("actualizado", "Última actualización: " + hActual.toString());
        editor.apply();
    }
}
