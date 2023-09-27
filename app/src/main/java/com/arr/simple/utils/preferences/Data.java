package com.arr.simple.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Map;

public class Data {

    private Context mContext;

    public Data(Context context) {
        this.mContext = context;
    } /*
       *
       */

    public void save(String generalKey, Map<String, Object> map) {
        SharedPreferences sp = mContext.getSharedPreferences(generalKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        if (!map.isEmpty()) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                edit.putString(key, value != null ? value.toString() : null).apply();
            }
        }
    }

    /* Cargar los datos guardados en el preference usando Map
     * los datos se cargan como String desde Danta, puede llamar a Data data = new Data(this);
     * y cargar los datos asi: String = data.load("key", "value");
     */
    public String load(String generalKey, String keyValue) {
        return mContext.getSharedPreferences(generalKey, Context.MODE_PRIVATE)
                .getString(keyValue, "");
    }
}
