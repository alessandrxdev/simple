package com.arr.simple.ui.balances;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.arr.services.ResponseUssd;
import com.arr.services.UssdResponse;
import com.arr.services.utils.ussd.SendUssdUtils;
import com.arr.simple.databinding.FragmentBalanceBinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiresApi(28)
public class BalanceFragment extends Fragment {

    private FragmentBalanceBinding binding;

    private SendUssdUtils utils;
    private ResponseUssd response;

    private UssdResponse response2;

    private final String[] ussdCodes = {
        "*222#", "*222*328#", "*222*266#", "*222*767#", "*222*869#",
    };
    private final String[] ussdKeys = {"saldo", "datos", "bonos", "sms", "min"};

    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    private String sim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle arg2) {
        binding = FragmentBalanceBinding.inflate(inflater, parent, false);

        sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        edit = sp.edit();
        sim = sp.getString("sim", "0");

        // services sendUssd
        utils = new SendUssdUtils(getContext());
        response = new ResponseUssd(utils);
        response2 = new UssdResponse(utils);

        // mostrar contenido
        viewContectBalances();

        binding.updateTime.setText(sp.getString("update", "sin actualizar"));

        // sincronizar balances
        binding.swipeRefresh.setOnRefreshListener(
                () -> {
                    Handler handler = new Handler(Looper.getMainLooper());
                    executeUssdRequest(handler, 0);
                    showToast("Actualizando balances, espere...");
                });

        return binding.getRoot();
    }

    private void viewContectBalances() {
        // balances datos
        response2.balancesDatos(
                binding.textTarifa,
                binding.textDatos,
                binding.textDatosLte,
                binding.textMensajeria,
                binding.textDiaria,
                binding.textDatosCu);

        // expire datos
        response2.balanceVencimiento(
                binding.textVenceDatos, binding.textVenceMensajeria, binding.textVenceDiaria);
        int progress = response2.expireDaysProgress();
        progress(progress);

        // balances saldo
        response2.balancesSaldo(
                binding.saldo,
                binding.textVenceSaldo,
                binding.min,
                binding.sms,
                binding.venceMinSms);

        // bonos en promoción
        response2.balanceBonos(
                binding.cardBonosPromo,
                binding.bonoIlimitado,
                binding.bonoSaldoo,
                binding.bonoDatos,
                binding.bonoDatosLte,
                binding.bonoVoz,
                binding.bonoSms);
    }

    // execute code ussd
    private void executeUssdRequest(Handler handler, int index) {
        if (index >= ussdCodes.length) {
            if (isVisible()) {
                viewContectBalances();
                binding.swipeRefresh.setRefreshing(false);
                showToast("Balances actualizados");
                updateTime();
            }
            return;
        }
        String ussdCode = ussdCodes[index];
        String ussdKey = ussdKeys[index];
        utils.execute(Integer.parseInt(sim), ussdCode, ussdKey);
        handler.postDelayed(
                () -> {
                    Log.w("USSD", ussdKey);
                    String response = utils.response(ussdKey);
                    if (!response.isEmpty()) {
                        executeUssdRequest(handler, index + 1);
                    } else {
                        executeUssdRequest(handler, index);
                    }
                },
                4500);
    }

    // TODO: view progress day in progressbar
    private void progress(int days) {
        int progress = (int) ((days / (float) 30) * 100);
        binding.progressDatos.setMax(100);
        binding.progressDatos.setProgress(progress);
    }

    private void updateTime() {
        Calendar calendar = Calendar.getInstance();
        Date dat = calendar.getTime();
        SimpleDateFormat datFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String hActual = datFormat.format(dat);
        edit.putString("update", hActual);
        edit.apply();
        String hor = sp.getString("update", "sin actualizar");
        binding.updateTime.setText(hor);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
