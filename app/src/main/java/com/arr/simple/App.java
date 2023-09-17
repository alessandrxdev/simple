package com.arr.simple;

import android.app.Application;

import android.os.Build;
import com.arr.bugsend.utils.HandlerUtil;
import com.arr.simple.utils.ThemeManager;
import com.google.android.material.color.DynamicColors;
import cu.suitetecsa.sdk.nauta.framework.JsoupConnectPortalScraper;
import cu.suitetecsa.sdk.nauta.framework.JsoupUserPortalScrapper;
import cu.suitetecsa.sdk.nauta.framework.NautaApi;
import cu.suitetecsa.sdk.nauta.framework.network.DefaultNautaSession;
import cu.suitetecsa.sdk.nauta.framework.network.JsoupConnectPortalCommunicator;
import cu.suitetecsa.sdk.nauta.framework.network.JsoupUserPortalCommunicator;

public class App extends Application {

    private NautaApi api;
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        }

        // TODO: Detectar cierres inesperados en toda la app
        Thread.setDefaultUncaughtExceptionHandler(new HandlerUtil(this));
        ThemeManager.apply(this);

        // api suitetecsa
        instance = this;
        api =
                new NautaApi(
                        new JsoupConnectPortalCommunicator(new DefaultNautaSession()),
                        new JsoupConnectPortalScraper(),
                        new JsoupUserPortalCommunicator(new DefaultNautaSession()),
                        new JsoupUserPortalScrapper());
    }

    public static App getInstance() {
        return instance;
    }

    public NautaApi apiNauta() {
        return api;
    }
}
