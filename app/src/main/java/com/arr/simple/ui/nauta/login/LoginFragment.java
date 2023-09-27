package com.arr.simple.ui.nauta.login;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import com.arr.simple.App;
import com.arr.simple.MainActivity;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentLoginBinding;
import com.arr.simple.nauta.LoginNauta;
import com.arr.simple.nauta.utils.LoginCallback;
import com.arr.simple.utils.preferences.Data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import cu.suitetecsa.sdk.nauta.framework.NautaApi;
import cu.suitetecsa.sdk.nauta.framework.model.NautaConnectInformation;
import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginNauta nauta;
    private NautaApi api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle arg2) {
        binding = FragmentLoginBinding.inflate(inflater, parent, false);

        // LoginNauta class
        nauta = new LoginNauta(requireActivity());

        // VPN Activo
        if (isVpnActive()) {
            Toast.makeText(getActivity(), "Desactive su VPN para poder acceder ", Toast.LENGTH_LONG)
                    .show();
        } else {
            api = App.getInstance().apiNauta();
            if (api.isConnected()) {
                NavController controller = navigation();
                controller.navigate(R.id.nav_info_nauta, null, options());
            }

            // connect
            binding.buttonLogin.setOnClickListener(
                    view -> {
                        String usuario = binding.editUser.getText().toString().trim();
                        String password = binding.editPassword.getText().toString().trim();
                        if (usuario.isEmpty() && password.isEmpty()) {
                            showSnackBar(getString(R.string.user_and_password_empty));
                        } else {
                            login(usuario, password);
                        }
                    });
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void login(String usuario, String password) {
        nauta.connect(
                usuario,
                password,
                new LoginCallback() {
                    @Override
                    public void navController(
                            NavController navigation, NautaConnectInformation info) {
                        if (info != null) {
                            Log.e("Conectado", "login " + info);
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("saldo", info.getAccountInfo().getCredit());
                            map.put("estado", info.getAccountInfo().getAccountStatus());
                            map.put("account", usuario);
                            new Data(getActivity()).save("login", map);
                        }
                        // next to info
                        navigation.navigate(R.id.nav_info_nauta, null, options());
                    }

                    @Override
                    public void handlerException(Exception e) {
                        showSnackBar("Error: " + e);
                    }
                });
    }

    private void showSnackBar(String message) {
        CoordinatorLayout view = ((MainActivity) getContext()).getCoordinator();
        BottomNavigationView nav = ((MainActivity) getContext()).getBottomNavigation();
        if (isVisible()) {
            Snackbar snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            snack.setAnchorView(view);
            snack.show();
        }
    }

    private NavOptions options() {
        return new NavOptions.Builder().setLaunchSingleTop(true).build();
    }

    private NavController navigation() {
        return Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
    }

    private boolean isVpnActive() {
        ConnectivityManager manager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = manager.getActiveNetwork();
                NetworkCapabilities compat = manager.getNetworkCapabilities(network);
                return compat != null && compat.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
            } else {
                NetworkInfo info = manager.getActiveNetworkInfo();
                return info != null && info.getType() == ConnectivityManager.TYPE_VPN;
            }
        }
        return false;
    }
}
