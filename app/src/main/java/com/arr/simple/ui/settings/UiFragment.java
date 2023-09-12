package com.arr.simple.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.arr.preference.M3ListPreference;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentSettingsBinding;

import com.arr.simple.preferences.M3MultiSelectPreference;
import com.arr.simple.utils.ThemeManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

            // home preference
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
    }
}
