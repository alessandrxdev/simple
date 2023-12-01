package com.arr.simple.ui.settings;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import androidx.preference.PreferenceManager;
import com.arr.preference.M3ListPreference;
import com.arr.preference.M3SwitchPreference;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentSettingsBinding;

import com.arr.ussd.Call;
import com.arr.ussd.utils.SimUtils;

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

        private SimUtils sim;
        private SharedPreferences spSIM;
        private String SIM;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_sim_card, rootKey);

            // simUtils
            sim = new SimUtils(getContext());
            spSIM = PreferenceManager.getDefaultSharedPreferences(requireActivity());
            SIM = spSIM.getString("sim", "0");

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
                                            .code("*133*1*1*1" + Uri.encode("#"), SIM);

                                } else {
                                    requestPermissionLauncher.launch(
                                            Manifest.permission.CALL_PHONE);
                                    return false;
                                }
                            } else {
                                // La tarifa está desactivada
                                new Call(getActivity()).code("*133*1*1*2" + Uri.encode("#"), SIM);
                            }

                            return true;
                        }
                    });

            // comprobar si el dispositivo es dualSIM
            /*
            M3ListPreference dualSim = findPreference("sim");
            if (hasReadPhoneStatePermission()) {
                boolean isDualSIM = sim.isDualSIM();
                if (isDualSIM) {
                    dualSim.setEnabled(true);
                } else {
                    dualSim.setEnabled(false);
                    dualSim.setSummary(R.string.is_not_dual_sim);
                }
            } else {
                requestReadPhoneStatePermission();
            }*/
        }

        private boolean hasReadPhoneStatePermission() {
            return ContextCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED;
        }

        private void requestReadPhoneStatePermission() {
            readContact.launch(Manifest.permission.READ_PHONE_STATE);
        }

        // permission launcher READ_PHONE_STATE
        private ActivityResultLauncher<String> readContact =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        new ActivityResultCallback<Boolean>() {
                            @Override
                            public void onActivityResult(Boolean result) {
                                if (result) {
                                    M3ListPreference dualSim = findPreference("sim");
                                    boolean isDualSIM = sim.isDualSIM();
                                    if (isDualSIM) {
                                        dualSim.setEnabled(true);
                                    } else {
                                        dualSim.setEnabled(false);
                                        dualSim.setSummary(R.string.is_not_dual_sim);
                                    }
                                }
                            }
                        });

        // permission launcher tarifa por consumo
        private ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        new ActivityResultCallback<Boolean>() {
                            @Override
                            public void onActivityResult(Boolean result) {
                                if (result) {
                                    // PERMISSION GRANTED
                                    new Call(getActivity())
                                            .code("*133*1*1*1" + Uri.encode("#"), SIM);
                                }
                            }
                        });
    }
}
