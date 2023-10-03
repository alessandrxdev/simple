package com.arr.simple.ui.settings;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import com.arr.preference.M3SwitchPreference;
import com.arr.simple.R;

public class SecurityPreference extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, new Security())
                .commit();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public static class Security extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(
                @Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            setPreferencesFromResource(R.xml.preference_security, rootKey);

            // TODO: Obtener confirmacion
            M3SwitchPreference check = findPreference("confirma");
            assert check != null;
            check.setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        boolean isCheck = (Boolean) newValue;
                        return true;
                    });

            // Rellenado
            M3SwitchPreference complete = findPreference("autocomplete");
            complete.setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        boolean isCheck = (Boolean) newValue;
                        return true;
                    });
            
            // bloqueo
            // TODO: confirmar si tiene fingerprint activo
            M3SwitchPreference bloqueo = findPreference("bloqueo");
            FingerprintManager manager =
                    (FingerprintManager)
                            requireActivity().getSystemService(Context.FINGERPRINT_SERVICE);
            if (manager.isHardwareDetected()) {
                if (!manager.hasEnrolledFingerprints()) {
                    if (bloqueo != null) bloqueo.setEnabled(false);
                } else {
                    if (bloqueo != null) bloqueo.setEnabled(true);
                }
            } else {
                if (bloqueo != null) bloqueo.setEnabled(false);
            }
        }
    }
}
