package com.arr.simple.utils.Nauta;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import com.arr.simple.R;
import com.arr.simple.MainActivity;
import com.arr.simple.utils.Nauta.exception.NautaException;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import cu.suitetecsa.sdk.nauta.domain.model.NautaUser;
import cu.suitetecsa.sdk.nauta.framekork.JsoupConnectPortalScraper;
import cu.suitetecsa.sdk.nauta.framekork.JsoupUserPortalScrapper;
import cu.suitetecsa.sdk.nauta.framekork.NautaApi;
import cu.suitetecsa.sdk.nauta.framework.model.NautaConnectInformation;
import cu.suitetecsa.sdk.nauta.framework.network.DefaultNautaSession;
import cu.suitetecsa.sdk.nauta.framework.network.JsoupConnectPortalCommunicator;
import cu.suitetecsa.sdk.nauta.framework.network.JsoupUserPortalCommunicator;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import kotlin.Pair;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;

public class NautaLogin {

    private NautaApi api;
    private Context mContext;
    private String errorMessage;

    private String statusAccount;
    private String creditAccount;
    private String expireAccount;

    public NautaLogin(String status, String credit, String expire) {
        this.statusAccount = status;
        this.creditAccount = credit;
        this.expireAccount = expire;
    }

    public String getStatusAccount() {
        return statusAccount;
    }

    public String getCreditAccount() {
        return creditAccount;
    }

    public NautaLogin(Context context) {
        mContext = context;
        api =
                new NautaApi(
                        new JsoupConnectPortalCommunicator(new DefaultNautaSession()),
                        new JsoupConnectPortalScraper(),
                        new JsoupUserPortalCommunicator(new DefaultNautaSession()),
                        new JsoupUserPortalScrapper());
    }

    /*
     * NautaLogin tiene un constructor que inicializa la instancia de NautaApi con las dependencias existentes.
     * Luego, el método  connect crea un nuevo hilo y llama al método setCredencials dentro de ese hilo.
     */

    public void connect(String usuario, String password) {
        Executors.newSingleThreadExecutor()
                .execute(
                        () -> {
                            try {
                                api.setCredentials(new Pair<>(usuario, password));
                                api.connect();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
    }

    public void connectionInfo(String usuario, String password) {
        Executors.newSingleThreadExecutor()
                .execute(
                        () -> {
                            try {
                                api.setCredentials(new Pair<>(usuario, password));
                                NautaConnectInformation info = api.getConnectInformation();
                                statusAccount = info.getAccountInfo().getAccountStatus();
                                creditAccount = info.getAccountInfo().getCredit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
    }

    @SuppressWarnings("deprecation")
    public void loadCaptcha(ImageView imageCaptcha) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    byte[] byteArray = api.getCaptchaImage();
                    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                } catch (Exception err) {
                    err.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    imageCaptcha.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

    @SuppressWarnings("deprecation")
    public void connectPortal(String usuario, String password, String captcha) {
        new AsyncTask<Void, Void, NautaUser>() {

            private Exception exeption;

            @Override
            protected NautaUser doInBackground(Void... params) {
                try {
                    InetAddress.getAllByName("www.portal.nauta.cu");
                    api.setCredentials(new Pair<>(usuario, password));
                    NautaUser user = api.login(captcha);
                    return user;
                } catch (Exception e) {
                    exeption = e;
                    Log.e("ERROR ", "SIMPLE: " + e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(NautaUser result) {
                if (exeption != null) {
                    exeption.printStackTrace();
                    showSnackBar("Error, compruebe sus datos y conexión!", true);
                }
                if (result != null) {
                    Log.w("CONECRADO ", "Conexión: " + result);

                    // Map
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("accountType", result.getAccountType());
                    map.put("activationDate", result.getActivationDate());
                    map.put("blockingDate", result.getBlockingDate());
                    map.put("credit", result.getCredit());
                    map.put("dateElimination", result.getDateOfElimination());
                    map.put("mail", result.getMailAccount());
                    map.put("serviceType", result.getServiceType());
                    map.put("time", result.getTime());
                    map.put("account", result.getUserName());
                    saveInfo("USUARIO", map);
                    // navigate
                    navegateTo(R.id.nav_info_nauta);
                }
            }
        }.execute();
    }

    @SuppressWarnings("deprecation")
    public void topUpAccount(String code) {
        new AsyncTask<Void, Void, Void>() {
            private Exception ex;

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    api.topUp(code);
                } catch (Exception err) {
                    err.printStackTrace();
                    ex = err;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                showSnackBar("Cuenta recargads", false);
                if (ex != null) {
                    String error = ex.getMessage();
                    showSnackBar("Error " + error, false);
                }
            }
        }.execute();
    }

    private void saveInfo(String generalKey, Map<String, Object> map) {
        SharedPreferences preferences =
                mContext.getSharedPreferences(generalKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        if (!map.isEmpty()) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                edit.putString(key, value != null ? value.toString() : null).apply();
            }
        }
    }

    private void navegateTo(int id) {
        NavController nav =
                Navigation.findNavController(
                        ((Activity) mContext), R.id.nav_host_fragment_content_main);
        nav.navigate(id, null, new NavOptions.Builder().setLaunchSingleTop(true).build());
    }

    private void showSnackBar(String message, boolean isAnchor) {
        CoordinatorLayout coordinator = ((MainActivity) mContext).getCoordnator();
        BottomNavigationView nav = ((MainActivity) mContext).getBottomNavigation();
        Snackbar snack = Snackbar.make(coordinator, message, Snackbar.LENGTH_SHORT);
        if (isAnchor) {
            snack.setAnchorView(nav);
        }
        snack.show();
    }
}
