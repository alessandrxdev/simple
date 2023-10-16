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
        // bonos
        if((response.ilimitado() != null && !response.ilimitado().isBlank()) || (response.bonosDatos() != null && !response.bonosDatos().isBlank()) || (response.bonosSaldo() != null && !response.bonosSaldo().isBlank())){
            binding.cardBonos.setVisibility(View.VISIBLE);
        }
        
        if (!response.ilimitado().isEmpty()) {
            binding.ilimitados.setVisibility(View.VISIBLE);
            binding.ilimitados.setText(response.ilimitado());
        }
        if (!response.bonosDatos().isEmpty()) {
            binding.bonosDatos.setVisibility(View.VISIBLE);
            binding.bonosDatos.setText(response.bonosDatos());
        }
        if (!response.bonosSaldo().isEmpty() && response.bonosSaldo() != null) {
            binding.bonoSaldo.setVisibility(View.VISIBLE);
            binding.bonoSaldo.setText(response.bonosSaldo());
        }

        // balances datos
        binding.textTarifa.setText(response.tarifa());
        binding.textDatos.setText(response.allData());
        binding.textDatosLte.setText(response.dataLte());
        binding.textDatosCu.setText(response.nacionales());
        binding.textVenceDatos.setText(response.venceAllData());
        
        // update progress bar
        if (response.venceAllData() != null) {
            Matcher matcher = Pattern.compile("\\d+").matcher(response.venceAllData());
            while(matcher.find()) {
            	String strDays = matcher.group();
                if(strDays != null){
                    int days = Integer.parseInt(strDays);
                   progress(days);
                }
            }
        } else {
            progress(0);
        }
        
        // bolsa diaria 
        binding.textDiaria.setText(response.diaria());
        binding.textVenceDiaria.setText(response.venceDiaria());
        
        // bolsa de mensajeria 
        binding.textMensajeria.setText(response.mensajeria());
        binding.textVenceMensajeria.setText(response.venceMensajeria());

        // saldo movil
        binding.saldo.setText(response.saldoMovil());
        binding.textVenceSaldo.setText("Expira: " + response.venceSaldo());
        binding.min.setText(response.minutos());
        binding.sms.setText(response.mensajes());
        binding.venceMinSms.setText(response.venceMensajes());
    }
    
    // execute code ussd
    private void executeUssdRequest(Handler handler, int index) {
        if (index >= ussdCodes.length) {
            if (isVisible()) {
                binding.swipeRefresh.setRefreshing(false);
                showToast("Balances actualizados");
                viewContectBalances();
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
    
    private void updateTime(){
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
