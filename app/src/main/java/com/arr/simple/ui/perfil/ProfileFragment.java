package com.arr.simple.ui.perfil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceFragmentCompat;
import com.arr.preference.BSEditTextPreference;
import com.arr.preference.M3Preference;
import com.arr.simple.R;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.arr.simple.databinding.FragmentProfileBinding;
import com.arr.simple.utils.profile.ImageUtils;
import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.github.drjacky.imagepicker.listener.DismissListener;
import com.github.drjacky.imagepicker.util.ImageUtil;
import java.io.File;
import kotlin.Unit;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ImageUtils utils;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @NonNull ViewGroup parent, Bundle arg2) {
        binding = FragmentProfileBinding.inflate(inflater, parent, false);
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new PerfilPreference())
                .commit();

        // ImageUtils
        utils = new ImageUtils(requireContext());

        Bitmap bitmap = utils.getSavedImage();
        if (bitmap != null) {
            Glide.with(requireContext()).load(bitmap).into(binding.profileImage);
        } else {
            binding.profileImage.setImageDrawable(errorImage());
        }

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

    private Drawable errorImage() {
        return requireActivity().getDrawable(R.drawable.ic_account_circle_24px);
    }

    // show toast
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    //  resultLauncher profile photo
    ActivityResultLauncher<String> launchPicture =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri uri) {
                            if (uri != null) {
                                if (utils.saveImage(uri)) {
                                    Glide.with(requireContext())
                                            .load(uri)
                                            .circleCrop()
                                            .into(binding.profileImage);
                                    showToast("Imagen guardada");
                                } else {
                                    showToast("Imagen no guardada");
                                }
                            }
                        }
                    });

    public static class PerfilPreference extends PreferenceFragmentCompat {

        private NavController nav;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_perfil, rootKey);

            // navigate to accounts nauta
            M3Preference nauta = findPreference("nauta");
            nauta.setOnPreferenceClickListener(
                    v -> {
                        controller().navigate(R.id.nav_mails, null, options());
                        return true;
                    });

            // change name user
            BSEditTextPreference name = findPreference("name");
            name.setOnPreferenceChangeListener(
                    (preference, value) -> {
                        return false;
                    });

            // change number user
            BSEditTextPreference numero = findPreference("number");
            if (!numero.getText().isEmpty()) {
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
}
