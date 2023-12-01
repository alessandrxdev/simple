package com.arr.simple.nauta;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.arr.simple.App;
import com.arr.simple.R;
import com.arr.simple.nauta.utils.CaptchaCallback;
import com.arr.simple.nauta.utils.ExceptionCallback;
import com.arr.simple.nauta.utils.LoginCallback;
import com.arr.simple.nauta.utils.PortalCallback;
import com.arr.simple.utils.ExecuteTask;
import cu.suitetecsa.sdk.nauta.domain.model.NautaUser;
import cu.suitetecsa.sdk.nauta.framework.NautaApi;
import cu.suitetecsa.sdk.nauta.framework.model.NautaConnectInformation;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginNauta {

    private NautaApi api;
    private Activity mActivity;
    private long mTime;

    public LoginNauta(Activity activity) {
        this.mActivity = activity;
        this.api = App.getInstance().apiNauta();
    }

    public long time() {
        return mTime;
    }

    // hacer coneccion con el login nauta
    public void connect(String usuario, String password, LoginCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(
                () -> {
                    try {
                        api.setCredentials(usuario, password);
                        api.connect();
                        NautaConnectInformation info = api.getConnectInformation();
                        mTime = api.getRemainingTime();
                        mActivity.runOnUiThread(() -> callback.navController(navigation(), info));
                    } catch (Exception e) {
                        e.printStackTrace();
                        mActivity.runOnUiThread(() -> callback.handlerException(e));
                    }
                });
    }

    // conectar al portal usuario
    public void portal(
            String usuario, String password, String codeCaptcha, PortalCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(
                () -> {
                    try {
                        InetAddress.getAllByName("www.portal.nauta.cu");
                        api.setCredentials(usuario, password);
                        NautaUser result = api.login(codeCaptcha);
                        mActivity.runOnUiThread(() -> callback.portalResult(navigation(), result));
                    } catch (Exception e) {
                        e.printStackTrace();
                        mActivity.runOnUiThread(() -> callback.handlerException(e));
                    }
                });
    }

    // recargar cuenta nauta
    public void topUpAccount(String code, ExceptionCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(
                () -> {
                    try {
                        api.topUp(code);
                        mActivity.runOnUiThread(() -> Log.w("NAUTA", "recaegado"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.w("NAUTA", "Error: " + e);
                        mActivity.runOnUiThread(() -> callback.handlerException(e));
                    }
                });
    }

    // transfer saldo

    // TODO: cargar captcha como Bitmap
    public void captcha(CaptchaCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(
                () -> {
                    try {
                        byte[] byteArray = api.getCaptchaImage();
                        Bitmap bitmap =
                                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        mActivity.runOnUiThread(() -> callback.loadCaptcha(bitmap));
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("Error: " + e);
                    }
                });
    }

    // desconectar cuenta
    public void desconectar(ExceptionCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(
                () -> {
                    try {
                        api.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mActivity.runOnUiThread(() -> callback.handlerException(e));
                    }
                });
    }

    // navigate to next fragment
    private NavController navigation() {
        return Navigation.findNavController(mActivity, R.id.nav_host_fragment_content_main);
    }

    private void showToast(String message) {
        mActivity.runOnUiThread(() -> Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show());
    }
}
