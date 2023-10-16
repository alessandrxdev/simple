package com.arr.simple.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class ThemeManager {

    public ThemeManager() {} /*
     *
     */

    public enum Mode {
        system,
        light,
        dark
    }

    public static void apply(Mode mode) {
        switch (mode) {
            case dark:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case light:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            default:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
        }
    }

    public static void apply(Context context) {
        SharedPreferences defaultSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String value = defaultSharedPreferences.getString("theme", Mode.system.name());
        apply(Mode.valueOf(value));
    }
}
