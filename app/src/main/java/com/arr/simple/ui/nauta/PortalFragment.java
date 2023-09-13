package com.arr.simple.ui.nauta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.arr.simple.databinding.FragmentPortalNautaBinding;
import com.arr.simple.utils.Nauta.NautaLogin;

public class PortalFragment extends Fragment {

    private FragmentPortalNautaBinding binding;
    private NautaLogin login;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @NonNull ViewGroup parent,
            Bundle savedInstanceState) {
        binding = FragmentPortalNautaBinding.inflate(inflater, parent, false);

        login = new NautaLogin(requireContext());

        // TODO: load captcha
        login.loadCaptcha(binding.imageCaptcha);
        binding.inputCaptcha.setEndIconOnClickListener(
                view -> {
                    login.loadCaptcha(binding.imageCaptcha);
                });

        binding.buttonConnect.setOnClickListener(
                view -> {
                    String usuario = binding.editUser.getText().toString().trim();
                    String password = binding.editPassword.getText().toString().trim();
                    String captcha = binding.editCaptcha.getText().toString().trim();
                    login.connectPortal(usuario, password, captcha);
                });
        return binding.getRoot();
    }
}
