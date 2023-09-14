package com.arr.simple.ui.nauta;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.arr.simple.databinding.FragmentConectadoBinding;
import com.arr.simple.utils.Nauta.NautaLogin;

public class ConectadoFragment extends Fragment {

    private FragmentConectadoBinding binding;
    private NautaLogin login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentConectadoBinding.inflate(inflater, container, false);

        login = new NautaLogin(getContext());

        // obtener tiempo
        login.getTiempo(binding.chronometer);
        // obtener cuenta
        binding.textAccount.setText(load("USUARIO", "account", ""));
        
        // desconectado
        binding.buttonDesconectar.setOnClickListener(
                view -> {
                    login.disconnect();
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
}
