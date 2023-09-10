package com.arr.simple;

import android.app.Application;

import android.os.Build;
import com.arr.bugsend.utils.HandlerUtil;
import com.arr.simple.utils.ThemeManager;
import com.google.android.material.color.DynamicColors;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        }

        // TODO: Detectar cierres inesperados en toda la app
        Thread.setDefaultUncaughtExceptionHandler(new HandlerUtil(this));
        ThemeManager.apply(this);
    }
}
