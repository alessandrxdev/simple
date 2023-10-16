package com.arr.services.utils.sim;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.List;

public class SimCardUtils {

    private final Context mContext;
    private List<PhoneAccountHandle> phoneAccountHandleList;

    private static final String[] simSlotName = {
        "extra_asus_dial_use_dualsim",
        "com.android.phone.extra.slot",
        "slot",
        "simslot",
        "sim_slot",
        "subscription",
        "Subscription",
        "phone",
        "com.android.phone.DialingMode",
        "simSlot",
        "slot_id",
        "simId",
        "simnum",
        "phone_type",
        "slotId",
        "slotIdx"
    };

    public SimCardUtils(Context context) {
        this.mContext = context;
    }

    // comprobar permisos de READ_PHONE_STATE y CALL_PHONE
    public boolean hasReadPhoneStatePermission() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    // solicitar permisos de READ_PHONE_STATE y CALL_PHONE
    public void requestReadPhoneStatePermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(
                activity,
                new String[] {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE},
                requestCode);
    }

    public void getCallCapablePhoneAccounts() {
        TelecomManager manager =
                (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            phoneAccountHandleList = manager.getCallCapablePhoneAccounts();
        }
    }

    // comprobar si el dispositivo dispone de dos SIM
    public boolean isDualSIM() {
        return phoneAccountHandleList != null && phoneAccountHandleList.size() > 1;
    }

    // realizar la llamada 
    public void makeCall(String phoneNumber, String selectedSIM) {
        Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        if (selectedSIM.equals("0")) {
            for (String s : simSlotName) {
                intent.putExtra(s, 0);
            }
            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0) {
                intent.putExtra(
                        "android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                        phoneAccountHandleList.get(0));
            }
        } else if (selectedSIM.equals("1")) {
            for (String s : simSlotName) {
                intent.putExtra(s, 1);
            }
            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 1) {
                intent.putExtra(
                        "android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                        phoneAccountHandleList.get(1));
            }
        }

        mContext.startActivity(intent);
    }
}
