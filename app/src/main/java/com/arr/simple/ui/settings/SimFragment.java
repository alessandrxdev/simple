package com.arr.simple.ui.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.arr.preference.M3SwitchPreference;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentSettingsBinding;

import com.arr.ussd.Call;

public class SimFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, new SIMPreference())
                .commit();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class SIMPreference extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_sim_card, rootKey);

            // Find the tariff preference
            M3SwitchPreference tarifa = (M3SwitchPreference) findPreference("tarifa");

            // Add a click listener
            tarifa.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            // Verifica si el switch está encendido
                            boolean isChecked = (Boolean) newValue;
                            if (isChecked) {
                                // La tarifa está activa
                                if (ContextCompat.checkSelfPermission(
                                                getActivity(), Manifest.permission.CALL_PHONE)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    new Call(getActivity())
                                            .code("*133*1*1*1" + Uri.encode("#"), "0");

                                } else {
                                    requestPermissionLauncher.launch(
                                            Manifest.permission.CALL_PHONE);
                                    return false;
                                }
                            } else {
                                // La tarifa está desactivada
                                new Call(getActivity()).code("*133*1*1*2" + Uri.encode("#"), "0");
                            }

                            return true;
                        }
                    });
        }

        private ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        new ActivityResultCallback<Boolean>() {
                            @Override
                            public void onActivityResult(Boolean result) {
                                if (result) {
                                    // PERMISSION GRANTED
                                    new Call(getActivity())
                                            .code("*133*1*1*1" + Uri.encode("#"), "0");
                                } 
                            }
                        });
    }
}
