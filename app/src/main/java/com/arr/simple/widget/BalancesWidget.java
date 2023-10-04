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
import android.provider.Settings;
import android.widget.RemoteViews;

import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.arr.simple.R;
import com.arr.simple.broadcast.BalancesBroadcast;
import com.arr.ussd.ResponseUssd;
import com.arr.ussd.utils.UssdUtils;

@RequiresApi(29)
public class BalancesWidget extends AppWidgetProvider {

    private Context mContext;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (int appWidgetId : appWidgetIds) {
                mContext = context;
                UssdUtils ussd = new UssdUtils(context);
                ResponseUssd response = new ResponseUssd(ussd);
                SharedPreferences spBalance = PreferenceManager.getDefaultSharedPreferences(context);

                /* remoteView */
                RemoteViews remote =
                        new RemoteViews(context.getPackageName(), R.layout.balances_widget);

                /* check permissions PHONE_CALL  */
                int permission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);
                PendingIntent pending;
                
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                    pending = PendingIntent.getActivities(context, 0, new Intent[]{intent}, PendingIntent.FLAG_IMMUTABLE);
                    Toast.makeText(context, "Conceda permiso de llamada", Toast.LENGTH_LONG).show();
                    remote.setOnClickPendingIntent(R.id.appwidget_sync, pending);
                } else {
                    Intent intent = new Intent(context, BalancesBroadcast.class);
                    pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                    remote.setOnClickPendingIntent(R.id.appwidget_sync, pending);
                
            }
                
                    /* views  */
                    // TODO: todos los datos
                    remote.setTextViewText(R.id.appwidget_text_paquete, response.getDataAll());
                    // TODO: datos lte
                    remote.setTextViewText(R.id.appwidget_text_lte, response.getLTE().replace(" LTE", ""));
                    // TODO: vence datos
                    remote.setTextViewText(R.id.appwidget_text_vence, response.getVenceData());
                    // TODO: minutos
                    remote.setTextViewText(R.id.appwidget_text_minutos, response.getMinutos());
                    // TODO: mensajes
                    remote.setTextViewText(R.id.appwidget_text_sms, response.getMensajes());
                    // TODO: time update
                    remote.setTextViewText(R.id.time_update,spBalance.getString("actualizado", "sin actualizar").replace("Actualizado: ", ""));
                

                // Actualiza el widget después de verificar los permisos.
                appWidgetManager.updateAppWidget(appWidgetId, remote);
            }
        } else {
            RemoteViews views =
                    new RemoteViews(
                            context.getPackageName(), R.layout.layout_widget_not_compatible);
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }
    }

    private int permissions() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);
    }
}
