package com.arr.services.utils.ussd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.arr.services.utils.sim.SimCardUtils;

public class Call {

    private final Context mContext;

    public Call(Context context) {
        this.mContext = context;
    }

    public void code(String code, String sim) {
        SimCardUtils simUtils = new SimCardUtils(mContext);

        if (simUtils.hasReadPhoneStatePermission()) {
            simUtils.getCallCapablePhoneAccounts();
            if (simUtils.isDualSIM()) {
                // Realiza la llamada utilizando la SIM seleccionada
                simUtils.makeCall(code, sim);
            } else {
                // Realiza la llamada sin especificar la SIM (para dispositivos con una sola SIM)
                Intent intent =
                        new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + code));
                mContext.startActivity(intent);
            }
        } else {
            int READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 20;
            if (mContext instanceof Activity) {
                simUtils.requestReadPhoneStatePermission(
                        (Activity) mContext, READ_PHONE_STATE_PERMISSION_REQUEST_CODE);
            }
        }
    }
}
