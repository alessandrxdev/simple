package com.arr.simple.utils.Nauta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import com.arr.simple.App;
import com.arr.simple.R;
import com.arr.simple.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import cu.suitetecsa.sdk.nauta.domain.model.NautaUser;
import cu.suitetecsa.sdk.nauta.framework.NautaApi;
import cu.suitetecsa.sdk.nauta.framework.model.NautaConnectInformation;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import kotlin.Pair;

public class NautaLogin {

    private NautaApi api;
    private Context mContext;
    private String errorMessage;

    private String statusAccount;
    private String creditAccount;
    private String expireAccount;
    private String formattedTime = "";

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
        this.api = App.getInstance().apiNauta();
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
                                api.setCredentials(usuario, password);
                                api.connect();
                                ((Activity) mContext)
                                        .runOnUiThread(
                                                () -> {
                                                    navegateTo(R.id.nav_conectado);
                                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                ((Activity) mContext)
                                        .runOnUiThread(
                                                () -> {
                                                    showSnackBarAction(
                                                            "Ah habido un error " + e, "Erros" + e, true);
                                                });
                            }
                        });
    }

    public void connectionInfo(String usuario, String password) {
        Executors.newSingleThreadExecutor()
                .execute(
                        () -> {
                            try {
                                api.setCredentials(usuario, password);
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

            private String message;

            @Override
            protected NautaUser doInBackground(Void... params) {
                try {
                    InetAddress.getAllByName("www.portal.nauta.cu");
                    api.setCredentials(usuario, password);
                    NautaUser user = api.login(captcha);
                    return user;
                } catch (Exception e) {
                    e.printStackTrace();
                    message = e.getMessage();
                    Log.e("ERROR ", "SIMPLE: " + e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(NautaUser result) {
                if (message != null) {
                    showSnackBar("error" + message, true);
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

    public void topUpCuenta(String code) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(
                () -> {
                    try {
                        api.topUp(code);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String error = e.getMessage();
                        ((Activity) mContext)
                                .runOnUiThread(
                                        () -> {
                                            showSnackBar("Error" + error, false);
                                        });
                    }
                });
    }

    public void transferToAccount(String account, String monto) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(
                () -> {
                    try {
                        float ammount = Float.parseFloat(monto);
                        api.transferFunds(ammount, account);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String error = e.getMessage();
                        ((Activity) mContext)
                                .runOnUiThread(
                                        () -> {
                                            showSnackBar("Error" + error, false);
                                        });
                    }
                });
    }

    @SuppressWarnings("deprecation")
    public void topUpAccount(String code) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    api.topUp(code);
                    Log.w("Recargado con éxito", "se ha recargado la cuenta");
                } catch (Exception err) {
                    err.printStackTrace();
                    errorMessage = err.getMessage();
                    Log.e("Error de recarga", errorMessage);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (errorMessage != null) {
                    Log.e("Error", errorMessage);
                    showSnackBar("Error " + errorMessage, false);
                }
            }
        }.execute();
    }

    // TODO: información obtenida de login
    @SuppressWarnings("deprecation")
    public void formatTime(TextView text) {
        new AsyncTask<Void, Void, NautaConnectInformation>() {
            @Override
            protected NautaConnectInformation doInBackground(Void... voids) {
                try {
                    return api.getConnectInformation();
                } catch (Exception err) {
                    err.printStackTrace();
                    errorMessage = err.getMessage();
                    Log.e("Error de recarga", errorMessage);
                }
                return null;
            }

            @Override
            protected void onPostExecute(NautaConnectInformation result) {
                if (result != null) {
                    // Map
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("accessAreas", result.getAccountInfo().getAccessAreas());
                    map.put("expireDate", result.getAccountInfo().getExpirationDate());
                    map.put("ddd", result.getAccountInfo().getAccountStatus());
                    saveInfo("LOGIN", map);
                }
            }
        }.execute();
    }

    // TODO: desconectar la cuenta
    @SuppressWarnings("deprecation")
    public void getTiempo(TextView text) {
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                try {
                    return api.getRemainingTime();
                } catch (Exception err) {
                    err.printStackTrace();
                    errorMessage = err.getMessage();
                    Log.e("Error de recarga", errorMessage);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Long millisecods) {
                if (millisecods != null) {
                    new CountDownTimer(millisecods, 1000) {
                        public void onTick(long millisUntilFinished) {
                            int h = (int) (millisUntilFinished / 3600000);
                            int m = (int) (millisUntilFinished - h * 3600000) / 60000;
                            int s = (int) (millisUntilFinished - h * 3600000 - m * 60000) / 1000;
                            String hh = h < 10 ? "0" + h : String.valueOf(h);
                            String mm = m < 10 ? "0" + m : String.valueOf(m);
                            String ss = s < 10 ? "0" + s : String.valueOf(s);
                            formattedTime = String.format("%s:%s:%s", hh, mm, ss);
                            text.setText(formattedTime);
                        }

                        public void onFinish() {
                            formattedTime = "00:00:00";
                            text.setText(formattedTime);
                        }
                    }.start();
                }
            }
        }.execute();
    }

    // TODO: desconectar la cuenta
    @SuppressWarnings("deprecation")
    public void disconnect() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    api.disconnect();
                } catch (Exception err) {
                    err.printStackTrace();
                    errorMessage = err.getMessage();
                    Log.e("Error de recarga", errorMessage);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (errorMessage != null) {
                    Log.e("Error", errorMessage);
                    showSnackBar("Error " + errorMessage, false);
                }
                showSnackBar("Desconectado", false);
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

    private void showSnackBarAction(String message, String error, boolean isAnchor) {
        CoordinatorLayout coordinator = ((MainActivity) mContext).getCoordnator();
        BottomNavigationView nav = ((MainActivity) mContext).getBottomNavigation();
        Snackbar snack = Snackbar.make(coordinator, message, Snackbar.LENGTH_SHORT);
        if (isAnchor) {
            snack.setAnchorView(nav);
        }
        snack.setAction(
                "Enviar",
                (click) -> {
                    sendCrashReport(error);
                });
        snack.show();
    }

    private void sendCrashReport(String report) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"soporteapplify@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "NAUTA");
        intent.putExtra(Intent.EXTRA_TEXT, report);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:"));
        mContext.startActivity(Intent.createChooser(intent, "Enviar reporte"));
    }
}
