package com.arr.simple;

import android.app.Application;

import com.arr.bugsend.utils.HandlerUtil;
import com.arr.simple.utils.ThemeManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: Detectar cierres inesperados en toda la app
        Thread.setDefaultUncaughtExceptionHandler(new HandlerUtil(this));
        ThemeManager.apply(this);
    }
}
