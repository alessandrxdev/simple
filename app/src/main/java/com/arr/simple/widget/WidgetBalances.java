package com.arr.simple.widget;
import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import androidx.preference.PreferenceManager;
import com.arr.simple.R;
import com.arr.simple.broadcast.BalancesBroadcast;
import com.arr.ussd.ResponseUssd;
import com.arr.ussd.utils.UssdUtils;

public class WidgetBalances extends AppWidgetProvider {
    
    private RemoteViews views;
    private PendingIntent pending;
    private ResponseUssd response;
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds) {
            
            // utils
            UssdUtils utils = new UssdUtils(context);
            response = new ResponseUssd(utils);
        
            /* comprobar si la version de Android es superior a android 8 para mostrar el Widget 
            * de lo contrario se muestra una vista informando que el dispositivo no es compatible
            * con la función.
            */
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
               views = new RemoteViews(context.getPackageName(), R.layout.balances_widget);
                
                /* comprobar si el usuario dio permisos de LLAMADA a la aplicación */
                int permissions = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
                if(permissions != PackageManager.PERMISSION_GRANTED){
                    
                    /* Llevar al usuario a los ajustes de la aplicacion para que conceda el permiso de LLAMADAS */
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                    pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                    views.setOnClickPendingIntent(R.id.appwidget_sync, pending);
                    
                }else{
                    
                    /* si el permiso esta dado permitir al usuario sincronizar los balances */
                    Intent intent = new Intent(context, BalancesBroadcast.class);
                    pending = PendingIntent.getBroadcast(context, 0, intent , PendingIntent.FLAG_IMMUTABLE);
                    views.setOnClickPendingIntent(R.id.appwidget_sync, pending);
                }
                
                /* mostrar contenido en los TextView */
                Handler handler = new Handler(Looper.getMainLooper());
                Runnable runnable = new Runnable(){
                    @Override
                    public void run() {
                        views.setTextViewText(R.id.appwidget_text_minutos, response.getMinutos());
                        views.setTextViewText(R.id.appwidget_text_sms, response.getMensajes());
                        views.setTextViewText(R.id.appwidget_text_paquete, response.getDataAll());
                        views.setTextViewText(R.id.appwidget_text_lte, response.getLTE().replace(" LTE",""));
                        views.setTextViewText(R.id.appwidget_text_vence, response.getVenceData());
                        
                        // actualizado 
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                        String time = sp.getString("actualizado", "sin actualizar").replace("Actualizado:\n", "");
                        views.setTextViewText(R.id.time_update, time);
                        
                        //update to 5 seconds
                        handler.postDelayed(this, 5000);
                }
            };
            handler.post(runnable);
                
                
                /* actualizar vista del widget */
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }else{
                views = new RemoteViews(context.getPackageName(), R.layout.layout_widget_not_compatible);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        	
        }
        
    }
    
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
            Toast.makeText(context, "¡Antes de agregar el widget conceda el permiso de llamada!", Toast.LENGTH_LONG).show();
    }
}
