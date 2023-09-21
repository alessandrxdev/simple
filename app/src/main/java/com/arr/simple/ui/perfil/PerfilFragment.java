package com.arr.simple.ui.perfil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import com.arr.didi.Didi;
import com.arr.preference.BSEditTextPreference;
import com.arr.preference.M3Preference;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentPerfilBinding;
import com.arr.simple.utils.Dialog.BSheetProfile;
import com.arr.simple.utils.Dialog.FullScreenDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.transition.platform.MaterialSharedAxis;
import java.io.File;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private Didi didi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        MaterialSharedAxis transition = new MaterialSharedAxis(MaterialSharedAxis.Z, true);
        setEnterTransition(transition);
        setReturnTransition(transition);

        // TODO: Inflating preference
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new PerfilPreference())
                .commit();

        // didi-library
        didi = new Didi(requireContext());

        // TODO: AddImage
        binding.addPhoto.setOnClickListener(view -> add_photo());

        // load
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            loadImage();
        }

        binding.imageProfile.setOnLongClickListener(
                view -> {
                    if (didi.getBitmap() != null) {
                        DialogFragment dialog = new FullScreenDialog();
                        dialog.show(getActivity().getSupportFragmentManager(), "Dialog");
                    } else {
                        Toast.makeText(
                                        getActivity(),
                                        getString(R.string.message_not_photo),
                                        Toast.LENGTH_LONG)
                                .show();
                    }

                    return true;
                });

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
        File file = didi.getFile();
        if (file.exists()) {
            file.delete();
            didi.setImage(binding.imageProfile);
        }
    }

    // guardar imagen
    private void saveImage(Uri uri) {
        didi.saveImageTo(uri)
                .setRounded(true)
                .setSize(100, 100)
                .setDirectoryName("Profile")
                .setImage(binding.imageProfile);
    }

    private void loadImage() {
        didi.load();
        didi.setDirectoryName("Profile");
        didi.setError(R.drawable.ic_account_circle_24px);
        didi.setRounded(true);
        didi.setImage(binding.imageProfile);
    }

    private void add_photo() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(R.layout.layout_bottom_sheet_perfil);
        dialog.getWindow().setNavigationBarColor(SurfaceColors.SURFACE_1.getColor(getActivity()));
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

            BSEditTextPreference numero = findPreference("number");
            if (!numero.getText().isEmpty()) {
                numero.setSummary(numero.getText());
            }

            // nauta
            M3Preference nauta = findPreference("nauta");
            nauta.setOnPreferenceClickListener(
                    v -> {
                        Toast.makeText(getActivity(), "Próximamente...", Toast.LENGTH_LONG).show();
                        return true;
                    });
        }
    }

    private void showDialogPermission() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(getString(R.string.title_permission_read_external_storage))
                .setCancelable(false)
                .setMessage(getString(R.string.permission_read_external_storage))
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
                                            saveImage(uri);
                                            requestPermissionLauncher.launch(
                                                    Manifest.permission.READ_EXTERNAL_STORAGE);
                                        }
                                    }
                                } else {
                                    saveImage(uri);
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
                            saveImage(uri);
                        }
                    });

    // en android inferior a 11 si el permiso de almacenamiento esta dado, se carga la foto
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result) {
                                // PERMISSION GRANTED
                                loadImage();
                            }
                        }
                    });
}
