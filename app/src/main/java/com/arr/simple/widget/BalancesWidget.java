package com.arr.simple.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.arr.simple.R;
import com.arr.simple.broadcast.UpdateBalances;
import com.arr.ussd.ResponseUssd;
import com.arr.ussd.utils.UssdUtils;

public class BalancesWidget extends AppWidgetProvider {

    private ResponseUssd response;
    private UssdUtils ussd;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.balances_widget);
            // TODO: Ejecutar consultas
            ussd = new UssdUtils(context);
            response = new ResponseUssd(ussd);

            // TODO: todos los datos
            views.setTextViewText(R.id.appwidget_text_paquete, response.getDataAll());
            // TODO: datos lte
            views.setTextViewText(R.id.appwidget_text_lte, response.getLTE());
            // TODO: vence datos
            views.setTextViewText(R.id.appwidget_text_vence, response.getVenceData());
            // TODO: minutos
            views.setTextViewText(R.id.appwidget_text_minutos, response.getMinutos());
            // TODO: mensajes
            views.setTextViewText(R.id.appwidget_text_sms, response.getMensajes());

            // TODO: sincronizar balances
            Intent intent = new Intent(context, UpdateBalances.class);
            intent.setAction("com.arr.simple.ACTION_UPDATE_BALANCES");
            int flag = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flag |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, flag);
            views.setOnClickPendingIntent(R.id.appwidget_sync, pending);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
