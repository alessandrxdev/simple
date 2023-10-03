package com.arr.simple.ui.perfil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceFragmentCompat;

import com.arr.preference.BSEditTextPreference;
import com.arr.preference.M3Preference;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentProfileBinding;
import com.arr.simple.utils.profile.ImageUtils;
import com.bumptech.glide.Glide;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ImageUtils utils;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup parent, Bundle arg2) {
        binding = FragmentProfileBinding.inflate(inflater, parent, false);
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new PerfilPreference())
                .commit();

        // ImageUtils
        utils = new ImageUtils(requireContext());

        // check permissions SDK < TIRAMISU
        if (isVersionCodeTiramisu()) {
            Bitmap bitmap = utils.getSavedImage();
            if (bitmap != null) {
                Glide.with(requireContext()).load(bitmap).into(binding.profileImage);
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 20);


            } else {
                Bitmap bitmap = utils.getSavedImage();
                if (bitmap != null) {
                    Glide.with(requireContext()).load(bitmap).into(binding.profileImage);
                }
            }
        }
        // ImageUtils


        // fab select photo
        binding.addPhoto.setOnClickListener(view -> add_photo());

        return binding.getRoot();
    }

    // add photo to gallery and camera
    private void add_photo() {
        launchPicture.launch("image/*");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable errorImage() {
        return requireActivity().getDrawable(R.drawable.ic_account_circle_24px);
    }

    private void guardar(Uri uri) {
        if (isVersionCodeTiramisu()) {
            if (utils.saveImage(uri)) {
                Glide.with(requireContext())
                        .load(uri)
                        .circleCrop()
                        .into(binding.profileImage);
            }
        } else {
            // comprobar permisos
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 33);

            } else {
                if (utils.saveImage(uri)) {
                    Glide.with(requireContext())
                            .load(uri)
                            .circleCrop()
                            .into(binding.profileImage);

                }
            }

        }
    }

    // show toast
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    //  resultLauncher profile photo
    ActivityResultLauncher<String> launchPicture =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            guardar(uri);
                        }
                    });

    public static class PerfilPreference extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_perfil, rootKey);

            // navigate to accounts nauta
            M3Preference nauta = findPreference("nauta");
            if (nauta != null) {
                nauta.setOnPreferenceClickListener(
                        v -> {
                            controller().navigate(R.id.nav_mails, null, options());
                            return true;
                        });
            }

            // change name user
            BSEditTextPreference name = findPreference("name");
            if (name != null) {
                name.setOnPreferenceChangeListener(
                        (preference, value) -> false);
            }

            // change number user
            BSEditTextPreference numero = findPreference("number");
            if (numero != null && !Objects.requireNonNull(numero.getText()).isEmpty()) {
                numero.setSummary(numero.getText());
            }
        }

        private NavController controller() {
            return Navigation.findNavController(
                    requireActivity(), R.id.nav_host_fragment_content_main);
        }

        private NavOptions options() {
            return new NavOptions.Builder().setLaunchSingleTop(true).build();
        }
    }

    //TODO: comprobar si el SDK es superior a 33
    private boolean isVersionCodeTiramisu() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }
}
