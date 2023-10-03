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

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.arr.simple.R;
import com.arr.simple.broadcast.BalancesBroadcast;
import com.arr.ussd.ResponseUssd;
import com.arr.ussd.utils.UssdUtils;

@RequiresApi(29)
public class BalancesWidget extends AppWidgetProvider {

    private ResponseUssd response;

    private PendingIntent pending;
    private SharedPreferences spBalance;

    private Context mContext;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (int appWidgetId : appWidgetIds) {
                mContext = context;
                UssdUtils ussd = new UssdUtils(context);
                response = new ResponseUssd(ussd);
                spBalance = PreferenceManager.getDefaultSharedPreferences(context);

                RemoteViews remote =
                        new RemoteViews(context.getPackageName(), R.layout.balances_widget);
                setupOnClick(context, remote);
                update(remote);
                appWidgetManager.updateAppWidget(appWidgetId, remote);
            }
        } else {
            RemoteViews views =
                    new RemoteViews(
                            context.getPackageName(), R.layout.layout_widget_not_compatible);
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }
    }

    private void setupOnClick(Context context, RemoteViews remote) {
        if (permissions() != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            remote.setOnClickPendingIntent(R.id.appwidget_sync, PendingIntent.getActivities(context, 0, new Intent[]{intent}, PendingIntent.FLAG_IMMUTABLE));
        } else {
            Intent intent = new Intent(context, BalancesBroadcast.class);
            pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            remote.setOnClickPendingIntent(R.id.appwidget_sync, pending);
        }
    }

    private void update(RemoteViews views) {
        views.setOnClickPendingIntent(R.id.appwidget_sync, pending);
        // TODO: todos los datos
        views.setTextViewText(R.id.appwidget_text_paquete, response.getDataAll());
        // TODO: datos lte
        views.setTextViewText(R.id.appwidget_text_lte, response.getLTE().replace(" LTE", ""));
        // TODO: vence datos
        views.setTextViewText(R.id.appwidget_text_vence, response.getVenceData());
        // TODO: minutos
        views.setTextViewText(R.id.appwidget_text_minutos, response.getMinutos());
        // TODO: mensajes
        views.setTextViewText(R.id.appwidget_text_sms, response.getMensajes());
        // TODO: time update
        views.setTextViewText(
                R.id.time_update,
                spBalance
                        .getString("actualizado", "sin actualizar")
                        .replace("Última actualización: ", ""));
    }

    private int permissions() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);
    }
}
