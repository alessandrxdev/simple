package com.arr.simple.ui.perfil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import com.arr.imagepicker.PhotoPicker;
import com.arr.preference.BSEditTextPreference;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentPerfilBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.transition.platform.MaterialSharedAxis;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PhotoPicker picker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);

        // TODO: PhotoPicker
        picker = new PhotoPicker(getActivity());

        // TODO: Inflating preference
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new PerfilPreference())
                .commit();

        MaterialSharedAxis transition = new MaterialSharedAxis(MaterialSharedAxis.Z, true);
        setEnterTransition(transition);
        setReturnTransition(transition);

        // TODO: AddImage
        binding.addPhoto.setOnClickListener(view -> add_photo());

        // load
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            picker.load(binding.imageProfile);
        }

        return binding.getRoot();
    }

    private void galleryPhoto() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            launchPicture.launch("image/*");
        } else {
            pickMedia.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
        }
    }

    private void deletePhoto() {
        picker.delete(binding.imageProfile);
    }

    private void add_photo() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(R.layout.layout_bottom_sheet_perfil);

        MaterialCardView gallery = dialog.findViewById(R.id.cardGallery);
        gallery.setOnClickListener(
                view -> {
                    galleryPhoto();
                    dialog.dismiss();
                });

        MaterialCardView delete = dialog.findViewById(R.id.cardDelete);
        delete.setOnClickListener(
                view -> {
                    deletePhoto();
                    dialog.dismiss();
                });
        dialog.show();
    }

    public static class PerfilPreference extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_perfil, rootKey);
            MaterialSharedAxis transition = new MaterialSharedAxis(MaterialSharedAxis.Z, true);
            setEnterTransition(transition);
            setReturnTransition(transition);

            // TODO: name user
            BSEditTextPreference name = findPreference("name");
            name.setOnPreferenceChangeListener(
                    (preference, value) -> {
                        return false;
                    });
        }
    }

    private void showDialogPermission() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Archivos")
                .setCancelable(false)
                .setMessage(
                        "Necesitamos que usted conceda los permisos necesarios para acceder a sus archivos y así usted pueda cargar su foto de perfil.")
                .setPositiveButton(
                        "Conceder",
                        (dialog, w) -> {
                            requestPermissionLauncher.launch(
                                    Manifest.permission.READ_EXTERNAL_STORAGE);
                        })
                .show();
    }

    ActivityResultLauncher<String> launchPicture =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri uri) {
                            if (uri != null) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                    if (ContextCompat.checkSelfPermission(
                                                    requireContext(),
                                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        showDialogPermission();
                                    } else {
                                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                                getActivity(),
                                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                            showDialogPermission();
                                        } else {
                                            picker.save(uri, binding.imageProfile);
                                            requestPermissionLauncher.launch(
                                                    Manifest.permission.READ_EXTERNAL_STORAGE);
                                        }
                                    }
                                } else {
                                    picker.save(uri, binding.imageProfile);
                                }
                            }
                        }
                    });
    // TODO: Photo picker in Android 11 +
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(
                    new ActivityResultContracts.PickVisualMedia(),
                    uri -> {
                        if (uri != null) {
                            picker.save(uri, binding.imageProfile);
                        }
                    });

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result) {
                                // PERMISSION GRANTED
                                picker.load(binding.imageProfile);
                            } else {
                                // PERMISSION NOT GRANTED
                            }
                        }
                    });
}
