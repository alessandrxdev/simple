package com.arr.services.utils.ussd;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@RequiresApi(28)
public class SendUssdUtils {

    private final Context mContext;

    public SendUssdUtils(Context context) {
        this.mContext = context;
    }

    public void execute(int sim, String ussd, String key) {
        TelephonyManager telephonyManager;
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    (Activity) mContext, new String[] {Manifest.permission.CALL_PHONE}, 20);
            return;
        } else {
            telephonyManager =
                    (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (sim != 0) {
                telephonyManager = telephonyManager.createForSubscriptionId(sim);
            }
        }
        telephonyManager.sendUssdRequest(
                ussd,
                new TelephonyManager.UssdResponseCallback() {
                    @Override
                    public void onReceiveUssdResponse(
                            TelephonyManager telephonyManager,
                            String request,
                            CharSequence response) {
                        super.onReceiveUssdResponse(telephonyManager, request, response);
                        saveUssdResponse(key, response.toString().trim(), mContext);
                      //  saveUssdResponse("time", updateTime(), mContext);
                    }

                    @Override
                    public void onReceiveUssdResponseFailed(
                            TelephonyManager telephonyManager, String request, int failureCode) {
                        super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                        saveUssdResponse("error", "" + failureCode, mContext);
                    }
                },
                new Handler(Looper.getMainLooper()));
    }

    private void saveUssdResponse(String key, String response, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("USSDB", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, response);
        editor.apply();
    }

    public String response(String key) {
        SharedPreferences preferences =
                mContext.getSharedPreferences("USSDB", Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public void clear(String key, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("USSDB", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    private String updateTime() {
        Calendar calendar = Calendar.getInstance();
        Date dat = calendar.getTime();
        SimpleDateFormat datFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String hActual = datFormat.format(dat);
        if (hActual.isBlank()) {
            return hActual;
        }
        return null;
    }
}
