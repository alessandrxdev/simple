package com.arr.simple.ui.nauta.correo;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.preference.PreferenceManager;
import com.arr.nautaclear.model.MailCount;
import com.arr.nautaclear.utils.EmailsCallback;
import com.arr.simple.R;
import androidx.fragment.app.Fragment;
import com.arr.nautaclear.NautaMail;
import com.arr.simple.databinding.FragmentMailBinding;

public class ClearFragments extends Fragment {

    private FragmentMailBinding binding;
    private NautaMail mail;

    @Override
    public View onCreateView(LayoutInflater inflate, ViewGroup parent, Bundle arg2) {
        binding = FragmentMailBinding.inflate(inflate, parent, false);

        // NautaMail
        mail = new NautaMail(requireActivity());
        binding.buttonDelete.setEnabled(false);
        
        // rellenado
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        boolean isActive = sp.getBoolean("autocomplete", false);
        if(isActive){
            binding.editUser.setText(sp.getString("correo", ""));
            binding.editPassword.setText(sp.getString("passCorreo",""));
        }else{
            binding.editUser.setText("");
            binding.editPassword.setText("");
        }
        
        
        if (isVpnActive()) {
            binding.buttonSync.setEnabled(false);
            showToast("Desactive su VPN para continuar");
        } else {
            // sincronizar correos
            binding.buttonSync.setOnClickListener(
                    view -> {
                        String correo = binding.editUser.getText().toString().trim();
                        String password = binding.editPassword.getText().toString().trim();
                        if (correo.isEmpty() && password.isEmpty()) {
                            showToast(getString(R.string.user_and_password_empty));
                        } else {
                            sincronizar(correo, password);
                            showToast("Sincronizando, espere...");
                        }
                    });

            binding.buttonDelete.setOnClickListener(
                    view -> {
                        String correo = binding.editUser.getText().toString().trim();
                        String password = binding.editPassword.getText().toString().trim();
                        if (correo.isEmpty() && password.isEmpty()) {
                            showToast(getString(R.string.user_and_password_empty));
                        } else {
                            deletrmail(correo, password);
                            showToast("Eliminando, espere...");
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

    private void sincronizar(String correo, String password) {
        mail.obtainsEmail(
                correo,
                password,
                new EmailsCallback() {
                    @Override
                    public void updateUI(MailCount count) {
                        String inbox = count.inboxCount + " correos";
                        String trash = count.trashCount + " correos";
                        if (isVisible()) {
                            binding.textInbox.setText(inbox);
                            binding.textDelete.setText(trash);
                            binding.buttonDelete.setEnabled(true);
                            showToast("¡Sincronizado!");
                        }
                    }

                    @Override
                    public void handleException(Exception e) {
                        showToast("" + e);
                    }
                });
    }

    private void deletrmail(String correo, String password) {
        mail.deleteEmails(
                correo,
                password,
                new EmailsCallback() {
                    @Override
                    public void updateUI(MailCount count) {
                        String inbox = count.inboxCount + " correos";
                        String trash = count.trashCount + " correos";
                        if (isVisible()) {
                            binding.textInbox.setText(inbox);
                            binding.textDelete.setText(trash);
                            showToast("¡Eliminados!");
                        }
                    }

                    @Override
                    public void handleException(Exception e) {
                        showToast("" + e);
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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
