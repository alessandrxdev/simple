package com.arr.simple.ui.settings;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.preference.PreferenceFragmentCompat;
import com.arr.preference.M3ListPreference;
import com.arr.preference.M3SwitchPreference;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentSettingsBinding;

import com.arr.simple.services.TrafficFloatingWindow;
import com.arr.simple.utils.ThemeManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class UiFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, new UiPreference())
                .commit();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class UiPreference extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_ui, rootKey);

            // theme in app
            M3ListPreference theme = findPreference("theme");
            if (theme.getValue() == null) {
                theme.setValue(ThemeManager.Mode.system.name());
            }
            theme.setOnPreferenceChangeListener(
                    (preference, value) -> {
                        ThemeManager.apply(ThemeManager.Mode.valueOf((String) value));
                        return true;
                    });

            // color floating
            M3ListPreference color = findPreference("floating_color");
            color.setOnPreferenceChangeListener(
                    (preference, value) -> {
                        boolean service = isServiceRunning(TrafficFloatingWindow.class);
                        if (service) {
                            stopServiceFloating();
                            startServiceFloating();
                        }
                        return true;
                    });

            // traffic preference
            M3SwitchPreference traffic = findPreference("traffic");
            traffic.setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        boolean isCheck = (Boolean) newValue;
                        if (isCheck) {
                            if (!canDrawOverlay()) {
                                Intent i = checkPerm();
                                activityResult.launch(i);
                                return false;
                            } else {
                                startServiceFloating();
                            }
                        } else {
                            stopServiceFloating();
                        }
                        return true;
                    });
            /*
            M3MultiSelectPreference home = findPreference("home");
            home.setVisible(false);
            home.setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        Set<String> values = (Set<String>) newValue;
                        if (values.contains("consulta")) {
                            Toast.makeText(getActivity(), values + "value", Toast.LENGTH_LONG)
                                    .show();
                        }
                        return true;
                    });
            */
        }

        private boolean isServiceRunning(Class<?> serviceClass) {
            ActivityManager manager =
                    (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service :
                    manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }

        private Intent checkPerm() {
            Intent intent =
                    new Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getActivity().getPackageName()));
            return intent;
        }

        private boolean canDrawOverlay() {
            return Settings.canDrawOverlays(getContext());
        }

        private void startServiceFloating() {
            Intent intent = new Intent(getActivity(), TrafficFloatingWindow.class);
            getActivity().startService(intent);
            
        }

        private void stopServiceFloating() {
            Intent intent = new Intent(getActivity(), TrafficFloatingWindow.class);
            getActivity().stopService(intent);
        }

        private void dialogPermission(String title, String message) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(
                            "Aceptar",
                            ((dialog, w) -> {
                                Intent intent =
                                        new Intent(
                                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                Uri.parse(
                                                        "package:"
                                                                + getActivity().getPackageName()));
                                activityResult.launch(intent);
                            }))
                    .show();
        }
        // floating preference permissions
        ActivityResultLauncher<Intent> activityResult =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (canDrawOverlay()) {
                                    startServiceFloating();
                                } else {
                                    dialogPermission(
                                            "Superposición",
                                            "Para poder usar esta funcion la aplicación necesita permisos concedidos por usted.");
                                }
                            }
                        });
    }
}
