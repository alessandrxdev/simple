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
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.arr.services.ResponseUssd;
import com.arr.services.utils.ussd.SendUssdUtils;
import com.arr.simple.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BalanceBroadcast extends BroadcastReceiver {
    
    private SendUssdUtils utils;
    private ResponseUssd response;
    
    private final String[] ussdCodes = {
        "*222#", "*222*328#", "*222*266#", "*222*767#", "*222*869#",
    };
    private final String[] ussdKeys = {"saldo", "datos", "bonos", "sms", "min"};

    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    private String sim;
    
    private Context mContext;
    private Intent mIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        this.mIntent = intent;
        
        // preference para obtener el numero de SIM card 
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        sim = sp.getString("sim", "0");
        edit = sp.edit();
        
        
        // SendUssdUtils
        utils = new SendUssdUtils(context);
        response = new ResponseUssd(utils);
        
        // ejecutar ussd 
        Handler handler = new Handler(Looper.getMainLooper());
        executeUssdRequest(handler, 0);
        
        // actualizar widget 
        handler.postDelayed(()->{
           updateWidget();
        }, 4000);
        
    }
    private void updateWidget(){
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(mContext);
            int[] widgetId = mIntent.getIntArrayExtra(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            if(widgetId != null){
                for (int appWidgetId : widgetId) {
                    RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.balances_widget);
                    views.setTextViewText(R.id.appwidget_text_paquete, response.allData());
                    views.setTextViewText(R.id.appwidget_text_lte, response.dataLte());
                    views.setTextViewText(R.id.appwidget_text_vence, response.venceAllData());
                    views.setTextViewText(R.id.appwidget_text_minutos, response.venceMinutos());
                    views.setTextViewText(R.id.appwidget_text_sms, response.venceMensajes());
                      
                    views.setTextViewText(R.id.time_update, sp.getString("update", "sin actualizar"));
                        
                    widgetManager.updateAppWidget(appWidgetId, views);
                }
            }
    }
    
    // execute code ussd
    private void executeUssdRequest(Handler handler, int index) {
        if (index >= ussdCodes.length) {
            // actualizar hora en que se ejecutó el código
            updateTime();
            updateWidget();
            
            // ocultar notificacion de balances actualizados
            boolean isShow = sp.getBoolean("not_update_balances", false);
            if(isShow){
                notificationUpdate("Balances", "¡Se han actualizado sus balances!");
            }
            
            // si la notificacion con la info de balances esta activa se actualiza 
            boolean isActive = sp.getBoolean("balance_notif", false);
            if(isActive){
                Intent i = new Intent(mContext, NotificationBalances.class);
                mContext.sendBroadcast(i);
            }
            return;
        }
        String ussdCode = ussdCodes[index];
        String ussdKey = ussdKeys[index];
        utils.execute(Integer.parseInt(sim), ussdCode, ussdKey);
        handler.postDelayed(
                () -> {
                    Log.w("USSD Execute: ", ussdKey);
                    String response = utils.response(ussdKey);
                    if (!response.isEmpty()) {
                        executeUssdRequest(handler, index + 1);
                    } else {
                        executeUssdRequest(handler, index);
                    }
                },
                4500);
    }
    private void updateTime(){
        Calendar calendar = Calendar.getInstance();
        Date dat = calendar.getTime();
        SimpleDateFormat datFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String hActual = datFormat.format(dat);
        edit.putString("update", hActual);
        edit.apply();
    }
    
        // notificación con balances actualizados
    private void notificationUpdate(String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = mContext.getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel("BALANCES", "BALANCES_CHANNEL", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "BALANCES")
                        .setSmallIcon(R.drawable.ic_logo_simple)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(0, builder.build());
    }
}
