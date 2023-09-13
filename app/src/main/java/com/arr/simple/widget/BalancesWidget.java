package com.arr.simple.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.widget.RemoteViews;
import com.arr.simple.R;
import com.arr.simple.broadcast.UpdateBalances;
import com.arr.ussd.ResponseUssd;
import com.arr.ussd.utils.UssdUtils;

public class BalancesWidget extends AppWidgetProvider {

    private static final String SYNC = "com.arr.simple.ACTION_UPDATE_BALANCES";
    private ResponseUssd response;
    private UssdUtils ussd;
    private Handler handler;
    private Runnable runnable;
    private int timeUpdate = 5000;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (int appWidgetId : appWidgetIds) {
                ussd = new UssdUtils(context);
                response = new ResponseUssd(ussd);

                RemoteViews views =
                        new RemoteViews(context.getPackageName(), R.layout.balances_widget);
                Intent intent = new Intent(context, UpdateBalances.class);
                intent.setAction(SYNC);
                PendingIntent pending =
                        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);
                views.setOnClickPendingIntent(R.id.appwidget_sync, pending);

                update(views);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }

            handler = new Handler(); // Inicializar el objeto Handler
            runnable =
                    new Runnable() {
                        public void run() {
                            for (int appWidgetId : appWidgetIds) {
                                RemoteViews views =
                                        new RemoteViews(
                                                context.getPackageName(), R.layout.balances_widget);
                                update(views);
                                appWidgetManager.updateAppWidget(appWidgetIds, views);
                            }
                            handler.postDelayed(this, timeUpdate);
                        }
                    };
            handler.post(runnable);
        } else {
            RemoteViews views =
                    new RemoteViews(
                            context.getPackageName(), R.layout.layout_widget_not_compatible);
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }
    }

    private void update(RemoteViews views) {
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
    }
}
