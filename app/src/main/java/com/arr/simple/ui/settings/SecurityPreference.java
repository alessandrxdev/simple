package com.arr.simple.ui.settings;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
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
            check.setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        boolean isCheck = (Boolean) newValue;
                        return true;
                    });

            M3SwitchPreference bloqueo = findPreference("bloqueo");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Si la versión de Android es 6.0 o posterior, verifica si el dispositivo tiene un
                // sensor de huellas digitales
                FingerprintManager fingerprintManager =
                        (FingerprintManager)
                                getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
                if (fingerprintManager.isHardwareDetected()
                        && fingerprintManager.hasEnrolledFingerprints()) {
                    // El dispositivo tiene un sensor de huellas digitales y hay al menos una huella
                    // digital registrada
                    bloqueo.setEnabled(true);
                } else {
                    // El dispositivo no admite la autenticación biométrica
                    bloqueo.setEnabled(false);
                }
            } else {
                // La versión de Android es anterior a 6.0, la autenticación biométrica no es
                // compatible
                bloqueo.setEnabled(false);
                bloqueo.setSummary("Su dispositivo no es compatible con esta opción");
            }
        }
    }
}
