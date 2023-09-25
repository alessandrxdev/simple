package com.arr.simple.ui.nauta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.arr.simple.MainActivity;
import com.arr.simple.databinding.FragmentLoginBinding;
import com.arr.simple.utils.Nauta.NautaLogin;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private NautaLogin login;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @NonNull ViewGroup parent,
            Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, parent, false);

        // TODO: importar NautaLogin para realizar la coneccion
        login = new NautaLogin(requireContext());

        // TODO: button login
        binding.buttonLogin.setOnClickListener(
                view -> {
                    String usuario = binding.editUser.getText().toString().trim();
                    String password = binding.editPassword.getText().toString().trim();
                    if (usuario.isEmpty() && password.isEmpty()) {
                        showSnackBar("Usuario o contraseña vacio", true);
                    } else {
                        login.connect(usuario, password);
                    }
                });
        
        

        binding.buttonInfo.setOnClickListener(
                view -> {
                    String usuario = binding.editUser.getText().toString().trim();
                    String password = binding.editPassword.getText().toString().trim();
                    if (usuario.isEmpty() && password.isEmpty()) {
                        showSnackBar("Usuario o contraseña vacio", true);
                    } else {
                        login.connectionInfo(usuario, password);
                        String status = login.getStatusAccount();
                        String credit = login.getCreditAccount();
                    }
                });

        
        
        
        return binding.getRoot();
    }

    public void showSnackBar(String message, boolean dimissable) {
        CoordinatorLayout coordinator = ((MainActivity) getActivity()).getCoordinator();
        BottomNavigationView nav = ((MainActivity) getActivity()).getBottomNavigation();
        Snackbar snack = Snackbar.make(coordinator, message, Snackbar.LENGTH_SHORT);
        snack.setAnchorView(nav);
        if (dimissable) {
            snack.dismiss();
        } else {
            snack.setDuration(Snackbar.LENGTH_SHORT);
        }
        snack.show();
    }
}
