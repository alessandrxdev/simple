package com.arr.simple.ui.nauta.portal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import com.arr.simple.App;
import com.arr.simple.R;
import com.arr.simple.MainActivity;
import com.arr.simple.databinding.FragmentInfoPortalBinding;
import com.arr.simple.nauta.LoginNauta;
import com.arr.simple.nauta.utils.ExceptionCallback;
import com.arr.simple.utils.ExecuteTask;
import com.arr.simple.utils.preferences.Data;
import com.google.android.material.snackbar.Snackbar;
import cu.suitetecsa.sdk.nauta.framework.NautaApi;

public class InfoFragment extends Fragment {

    private FragmentInfoPortalBinding binding;
    private Data data;
    private LoginNauta nauta;
    private NautaApi api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle arg2) {
        binding = FragmentInfoPortalBinding.inflate(inflater, parent, false);
        api = App.getInstance().apiNauta();

        // LoginNauta
        nauta = new LoginNauta(requireActivity());

        // load data info
        data = new Data(getContext());

        binding.textAccount.setText(data.load("user", "account"));
        binding.textBloqueo.setText(data.load("user", "blockingDate"));
        binding.textDelete.setText(data.load("user", "dateElimination"));
        binding.textSaldo.setText(data.load("user", "credit"));
        binding.textTime.setText(data.load("user", "time"));
        binding.textType.setText(data.load("user", "accountType"));

        // recargar cuenta con cupón
        binding.buttonRecargar.setOnClickListener(
                view -> {
                    String code = binding.editCode.getText().toString().trim();
                    if (code.isEmpty()) {
                        showSnackBar(getString(R.string.text_empty));
                    } else {
                        topUp(code);
                    }
                });

        // transferir saldo
        binding.buttonTransfer.setOnClickListener(
                view -> {
                    String monto = binding.editMonto.getText().toString().trim();
                    String cuenta = binding.editAccount.getText().toString().trim();
                    if (monto.isEmpty() && cuenta.isEmpty()) {
                        showSnackBar("Hay campos vacíos");
                    } else {
                        transferir(monto, cuenta);
                    }
                });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void topUp(String codeRecarga) {
        nauta.topUpAccount(
                codeRecarga,
                new ExceptionCallback() {
                    @Override
                    public void handlerException(Exception e) {
                        showSnackBar("Error: " + e);
                    }
                });
    }

    private void transferir(String monto, String cuenta) {
        new ExecuteTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    float ammount = Float.parseFloat(monto);
                    api.transferFunds(ammount, cuenta);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.w("Recarga Nauta:", "E" + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (result != null) {
                    Log.e("Recargado ", "Conect: " + result);
                    showSnackBar("Recargado");
                }
            }
        }.execute();
    }

    private void showSnackBar(String message) {
        CoordinatorLayout view = ((MainActivity) getContext()).getCoordinator();
        if (isVisible()) {
            Snackbar snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            snack.show();
        }
    }
}
