package com.arr.simple.ui.settings;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import com.arr.didi.Didi;
import com.arr.preference.M3Preference;
import com.arr.preference.WaPerfilPreference;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentSettingsBinding;

import com.arr.simple.utils.profile.ImageUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.transition.platform.MaterialSharedAxis;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private Didi didi;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if (root != null) {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, new SettingsPreference())
                    .commit();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class SettingsPreference extends PreferenceFragmentCompat {

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
            setPreferencesFromResource(R.xml.preferences, rootKey);

            // TODO: Perfil
            WaPerfilPreference perfil = findPreference("perfil");
            if (getNombre().isEmpty() && getNumero().isEmpty()) {
                perfil.setTitle(getResources().getString(R.string.title_perfil_preference));
                perfil.setSummary(getResources().getString(R.string.summary_perfil_preference));
            } else {
                perfil.setTitle(getNombre());
                perfil.setSummary(getNumero());
            }

            Bitmap bitmap = new ImageUtils(requireActivity()).setRounded(true).getSavedImage();
            if (bitmap != null) {
                perfil.setIconBitmap(bitmap);
            } else {
                perfil.setIcon(R.drawable.ic_account_circle_24px);
            }

            // TODO: Mostrar QR con el número de móvil
            perfil.setOnEditClickListener(
                    view -> {
                        if (!getNumero().isEmpty()) {
                            BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
                            dialog.setContentView(R.layout.layout_bottom_sheet_code_qr);
                            dialog.getWindow()
                                    .setNavigationBarColor(
                                            SurfaceColors.SURFACE_1.getColor(getContext()));
                            ImageView code = dialog.findViewById(R.id.imageQr);
                            generateQr(getNumero().toString(), code);
                            dialog.show();
                        }
                    });

            perfil.setOnPreferenceClickListener(
                    (preference) -> {
                        nav.navigate(
                                R.id.nav_profile,
                                null,
                                new NavOptions.Builder().setLaunchSingleTop(true).build());
                        return true;
                    });

            // TODO: inflating ui preference
            M3Preference ui = findPreference("ui");
            ui.setOnPreferenceClickListener(
                    (preference) -> {
                        nav.navigate(
                                R.id.nav_ui,
                                null,
                                new NavOptions.Builder().setLaunchSingleTop(true).build());
                        return true;
                    });

            // TODO: inflating balances preference
            M3Preference balances = findPreference("balance");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                balances.setVisible(false);
            }
            balances.setOnPreferenceClickListener(
                    (preference) -> {
                        nav.navigate(
                                R.id.nav_pref_balance,
                                null,
                                new NavOptions.Builder().setLaunchSingleTop(true).build());
                        return true;
                    });

            // TODO: simcard
            M3Preference sim = findPreference("simcard");
            sim.setOnPreferenceClickListener(
                    (preference) -> {
                        nav.navigate(
                                R.id.nav_sim,
                                null,
                                new NavOptions.Builder().setLaunchSingleTop(true).build());
                        return true;
                    });

            // TODO: inflating security preference
            M3Preference security = findPreference("seguridad");
            security.setOnPreferenceClickListener(
                    (preference) -> {
                        nav.navigate(
                                R.id.nav_security,
                                null,
                                new NavOptions.Builder().setLaunchSingleTop(true).build());
                        return true;
                    });

            M3Preference lenguaje = findPreference("idioma");
            lenguaje.setOnPreferenceClickListener(
                    v -> {
                        Toast.makeText(getActivity(), "Próximamente...", Toast.LENGTH_LONG).show();
                        return true;
                    });

            M3Preference info = findPreference("help");
            info.setOnPreferenceClickListener(
                    v -> {
                        Toast.makeText(getActivity(), "Próximamente...", Toast.LENGTH_LONG).show();
                        return true;
                    });
        }

        private void generateQr(String inputValue, ImageView image) {
            QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, 500);
            qrgEncoder.setColorBlack(getResources().getColor(R.color.colorOnSurface));
            qrgEncoder.setColorWhite(Color.TRANSPARENT);
            try {
                Bitmap bitmap = qrgEncoder.getBitmap(0);
                image.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.v("TAG", e.toString());
            }
        }

        public String getNombre() {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            return sp.getString("name", "");
        }

        public String getNumero() {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            return sp.getString("number", "");
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            nav = null;
        }
    }
}
