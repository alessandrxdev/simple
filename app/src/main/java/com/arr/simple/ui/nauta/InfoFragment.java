package com.arr.simple.ui.nauta;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.arr.simple.databinding.FragmentInfoPortalBinding;
import com.arr.simple.utils.Nauta.NautaLogin;
import java.util.HashMap;
import java.util.Map;

public class InfoFragment extends Fragment {

    private FragmentInfoPortalBinding binding;
    private NautaLogin login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentInfoPortalBinding.inflate(inflater, container, false);

        login = new NautaLogin(requireContext());

        // load info
        binding.textTime.setText(load("USUARIO", "time", ""));
        binding.textAccount.setText(load("USUARIO", "account", ""));
        binding.textSaldo.setText(load("USUARIO", "credit", ""));
        binding.textBloqueo.setText(load("USUARIO", "blockingDate", ""));
        binding.textDelete.setText(load("USUARIO", "dateElimination", ""));
        binding.textType.setText(load("USUARIO", "accountType", ""));

        // recargar cuenta
        binding.buttonRecargar.setOnClickListener(
                view -> {
                    String code = binding.editCode.getText().toString().trim();
                    login.topUpAccount(code);
                });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String load(String generalKey, String keyValue, String defaultValue) {
        return getContext()
                .getSharedPreferences(generalKey, Context.MODE_PRIVATE)
                .getString(keyValue, defaultValue);
    }

    private void remove(Context context, String generalKey) {
        context.getSharedPreferences(generalKey, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
