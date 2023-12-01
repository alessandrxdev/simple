package com.arr.simple.ui.nauta.portal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import com.arr.simple.App;
import com.arr.simple.MainActivity;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentPortalNautaBinding;
import com.arr.simple.nauta.LoginNauta;
import com.arr.simple.nauta.utils.CaptchaCallback;
import com.arr.simple.nauta.utils.PortalCallback;
import com.arr.simple.utils.ExecuteTask;
import com.arr.simple.utils.preferences.Data;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import cu.suitetecsa.sdk.nauta.domain.model.NautaUser;
import cu.suitetecsa.sdk.nauta.framework.NautaApi;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class PortalFragment extends Fragment {

    private FragmentPortalNautaBinding binding;
    private LoginNauta nauta;
    private NautaApi api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle arg2) {
        binding = FragmentPortalNautaBinding.inflate(inflater, parent, false);
        api = App.getInstance().apiNauta();

        // LoginNauta class
        nauta = new LoginNauta(requireActivity());

        // rellenado
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        boolean isActive = sp.getBoolean("autocomplete", false);
        if (isActive) {
            binding.editUser.setText(sp.getString("internet", ""));
            binding.editPassword.setText(sp.getString("passInternet", ""));
        } else {
            binding.editUser.getText().clear();
            binding.editPassword.getText().clear();
        }

        // load captcha
        if (isVpnActive()) {
            binding.imageCaptcha.setImageDrawable(
                    getActivity().getDrawable(R.drawable.ic_sync_problem_24px));
            Toast.makeText(getActivity(), "Desactive su VPN para poder acceder ", Toast.LENGTH_LONG)
                    .show();
        } else {
            loadCaptcha(binding.imageCaptcha);
            binding.inputCaptcha.setEndIconOnClickListener(
                    view -> loadCaptcha(binding.imageCaptcha));

            // connect to portal nauta
            binding.buttonConnect.setOnClickListener(
                    view -> {
                        String usuario = binding.editUser.getText().toString().trim();
                        String password = binding.editPassword.getText().toString().trim();
                        String captcha = binding.editCaptcha.getText().toString().trim();
                        if (usuario.isEmpty() && password.isEmpty() && captcha.isEmpty()) {
                            Toast.makeText(
                                            getActivity(),
                                            getString(R.string.user_and_password_empty),
                                            Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            conncet(usuario, password, captcha);
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

    private void conncet(String user, String pass, String captcha) {
        nauta.portal(
                user,
                pass,
                captcha,
                new PortalCallback() {
                    @Override
                    public void handlerException(Exception e) {
                        Log.e("NautaException", "E: " + e);
                        showSnackBar("Error " + e);
                    }

                    @Override
                    public void portalResult(NavController nav, NautaUser result) {
                        Log.e("Conectado", "nauta" + result);
                        if (result != null) {
                            Log.e("Conectado", "nauta" + result);
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
                            new Data(requireContext()).save("user", map);

                            // next to info
                            NavController controller = navigation();
                            controller.navigate(R.id.nav_info_nauta, null, options());
                        }
                    }
                });
    }

    private void loadCaptcha(ImageView image) {
        nauta.captcha(
                new CaptchaCallback() {
                    @Override
                    public void loadCaptcha(Bitmap bitmap) {
                        image.setImageBitmap(bitmap);
                    }

                    @Override
                    public void handlerException(Exception e) {
                        //   Toast.makeText(getActivity(), "" + e, Toast.LENGTH_LONG).show();
                        image.setImageDrawable(
                                requireActivity().getDrawable(R.drawable.ic_about_24px));
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
