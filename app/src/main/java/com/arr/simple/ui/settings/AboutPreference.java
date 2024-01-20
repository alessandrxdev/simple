package com.arr.simple.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceFragmentCompat;
import com.arr.preference.M3Preference;
import com.arr.simple.R;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.arr.simple.databinding.FragmentSettingsBinding;

public class AboutPreference extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if (root != null) {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, new About())
                    .commit();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class About extends PreferenceFragmentCompat {

        private NavController nav;

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // navigation controller
            nav =
                    Navigation.findNavController(
                            requireActivity(), R.id.nav_host_fragment_content_main);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_about, rootKey);

            // soporte
            M3Preference support = findPreference("support");
            support.setOnPreferenceClickListener(
                    (preference) -> {
                        startActivity(
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://t.me/soporteapplifybot?start=reporte")));
                        return true;
                    });

            // legal
            M3Preference legal = findPreference("legal");
            legal.setOnPreferenceClickListener(
                    (preference) -> {
                        startActivity(
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://site-8n0.pages.dev/politicas")));
                        return true;
                    });

            // about
            M3Preference about = findPreference("about");
            about.setOnPreferenceClickListener(
                    (preference) -> {
                        nav.navigate(
                                R.id.nav_rail_about,
                                null,
                                new NavOptions.Builder().setLaunchSingleTop(true).build());
                        return true;
                    });
        }
    }
}
