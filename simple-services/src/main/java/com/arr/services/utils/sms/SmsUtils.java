package com.arr.services.utils.sms;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.arr.services.utils.sim.SimCardUtils;

public class SmsUtils {

    private final Context mContext;

    public SmsUtils(Context context) {
        this.mContext = context;
    }

    public void send(String number, String message) {
        //  SimCardUtils utils = new SimCardUtils(mContext);
        if (hasSmsPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SmsManager sms = mContext.getSystemService(SmsManager.class);
                sms.sendTextMessage(number, null, message, null, null);
            } else {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(number, null, message, null, null);
            }
        } else {
            int SEND_SMS_PERMISSION_REQUEST_CODE = 20;
            if (mContext instanceof Activity) {
                requestSmsPermission((Activity) mContext, SEND_SMS_PERMISSION_REQUEST_CODE);
            }
        }
    }

    public boolean hasSmsPermission() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestSmsPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(
                activity, new String[] {Manifest.permission.SEND_SMS}, requestCode);
    }
}
