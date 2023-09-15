package com.arr.simple.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import androidx.preference.PreferenceManager;
import com.arr.simple.MainActivity;
import com.arr.simple.databinding.FragmentHomeBinding;
import com.arr.simple.utils.Scanner.CustomScanner;
import com.arr.simple.utils.permissions.PermissionCheck;
import com.arr.ussd.Call;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.transition.platform.MaterialSharedAxis;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.util.Set;
import com.arr.simple.R;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SharedPreferences sp;
    private boolean isConfirm;
    private String SIM;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        MaterialSharedAxis transition = new MaterialSharedAxis(MaterialSharedAxis.Z, true);
        setEnterTransition(transition);
        setReturnTransition(transition);

        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SIM = sp.getString("sim", "0");

        // TODO: consulta adelanta
        isConfirm = !sp.getBoolean("pospago", false);
        if (isConfirm) {
            binding.content.iconAdelanta.setImageDrawable(
                    getActivity().getDrawable(R.drawable.ic_adelanta_24px));
            binding.content.textAdelanta.setText("Adelanta");
        } else {
            binding.content.iconAdelanta.setImageDrawable(
                    getActivity().getDrawable(R.drawable.ic_pospago_24px));
            binding.content.textAdelanta.setText("Pospago");
        }
        binding.content.cardAdealnta.setOnClickListener(
                v -> {
                    String consulta = isConfirm ? "*222*233" : "*111";
                    new Call(getActivity()).code(consulta + Uri.encode("#"), SIM);
                });

        // TODO: Consultar saldo principal
        binding.content.cardSaldo.setOnClickListener(
                view -> {
                    new Call(getActivity()).code("*222" + Uri.encode("#"), SIM);
                });

        // TODO: Consultar bonos
        binding.content.cardBonos.setOnClickListener(
                view -> {
                    new Call(getActivity()).code("*222*266" + Uri.encode("#"), SIM);
                });

        // TODO: Consulto datos
        binding.content.cardDatos.setOnClickListener(
                view -> {
                    new Call(getActivity()).code("*222*328" + Uri.encode("#"), SIM);
                });

        // TODO: Scanner QR recarga
        binding.inputLayoutRecarga.setEndIconOnClickListener(
                view -> {
                    ScanOptions scanner = new ScanOptions();
                    scanner.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                    scanner.setCaptureActivity(CustomScanner.class);
                    scanner.setPrompt("Centre el QR para escanear el código de recarga");
                    scanner.setOrientationLocked(true);
                    scanner.setTorchEnabled(false);
                    scanner.setBeepEnabled(false);
                    scanner.setTimeout(15000);
                    barcodeLauncher.launch(scanner);
                });

        // TODO: Validation TextWatcher
        binding.editTextNumero.addTextChangedListener(
                new ValidationTextWatcher(binding.editTextNumero));
        binding.inputEditTextMonto.addTextChangedListener(
                new ValidationTextWatcher(binding.inputEditTextMonto));
        binding.inputEditTextClave.addTextChangedListener(
                new ValidationTextWatcher(binding.inputEditTextClave));
        // onClick buttom transferir
        binding.buttonTransferir.setOnClickListener(
                v -> {
                    if (!validateTransferirNumero()) {
                        showSnackBar("Inserte un número", true);
                        hideKeyboard(getActivity(), binding.editTextNumero);
                        return;
                    } else if (!validateTransferirClave()) {
                        showSnackBar("Inserte su clave", true);
                        hideKeyboard(getActivity(), binding.inputEditTextClave);
                        return;
                    } else if (!validateTransferirMonto()) {
                        return;
                    } else {
                        String numero = binding.editTextNumero.getText().toString().trim();
                        String clave = binding.inputEditTextClave.getText().toString().trim();
                        String monto = binding.inputEditTextMonto.getText().toString().trim();
                        new Call(getActivity())
                                .code(
                                        "*234*1*"
                                                + numero
                                                + "*"
                                                + clave
                                                + "*"
                                                + monto
                                                + Uri.encode("#"),
                                        SIM);
                    }
                    binding.inputEditTextClave.getText().clear();
                    binding.inputEditTextMonto.getText().clear();
                    binding.editTextNumero.getText().clear();
                });

        // Recarga
        binding.inputEditTextRecarga.addTextChangedListener(
                new ValidationTextWatcher(binding.inputEditTextRecarga));
        binding.buttonRecargar.setOnClickListener(
                view -> {
                    if (!validateTextRecarga()) {
                        showSnackBar("Inserte el código de recarga", true);
                        hideKeyboard(getActivity(), binding.inputEditTextRecarga);
                        return;
                    } else {
                        String code =
                                binding.inputEditTextRecarga
                                        .getText()
                                        .toString()
                                        .replace("-", "")
                                        .trim();
                        new Call(getActivity()).code("*662" + code + Uri.encode("#"), SIM);
                    }
                    binding.inputEditTextRecarga.getText().clear();
                });
        // select contact
        binding.inputLayoutNumero.setEndIconOnClickListener(
                view -> {
                    new PermissionCheck(getActivity())
                            .setTitle(R.string.title_contact_transfer)
                            .setMessage(R.string.message_contact_transfer)
                            .setPermission(Manifest.permission.READ_CONTACTS)
                            .setPositiveButton("Aceptar")
                            .launchPermission(
                                    () -> {
                                        requestPermissionLauncher.launch(
                                                Manifest.permission.READ_CONTACTS);
                                    })
                            .executeCode(
                                    () -> {
                                        pickContact.launch(null);
                                    });
                });

        // TODO : action adelanta saldo
        binding.buttonAdelanta.setOnClickListener(
                v -> {
                    if (!validateAdelanta()) {
                        showSnackBar("Seleccione una cantiad", true);
                        return;
                    } else {
                        String cantidad =
                                binding.inputEditTextAdelanta
                                        .getText()
                                        .toString()
                                        .replace(".00 CUP", "")
                                        .trim();
                        /*
                        se comprueba si el SwitchPreference esta activo para agrehar "*1"
                            asi saltarse el mensaje de confirmación de lo contrario se agrega
                            en blanco y no significaria un cambio
                            */
                        isConfirm = !sp.getBoolean("confirma", false);
                        String confirm = isConfirm ? "" : "*1";
                        new Call(getActivity())
                                .code("*234*3*1*" + cantidad + confirm + Uri.encode("#"), SIM);
                    }
                    binding.inputEditTextAdelanta.getText().clear();
                    binding.inputEditTextAdelanta.clearFocus();
                });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private boolean validateTextRecarga() {
        String numero = binding.inputEditTextRecarga.getText().toString().replaceAll("\\s+", "");
        if (numero.trim().isEmpty()) {
            binding.inputEditTextRecarga.clearFocus();
            binding.inputLayoutRecarga.setErrorEnabled(true);
            binding.inputLayoutRecarga.setError(null);
            return false;
        } else if (numero.length() < 16) {
            binding.inputLayoutRecarga.setError("Faltan dígitos");
            binding.inputLayoutRecarga.requestFocus();
            return false;
        } else {
            binding.inputLayoutRecarga.setErrorEnabled(true);
            binding.inputLayoutRecarga.setError(null);
        }
        return true;
    }

    private boolean validateTransferirNumero() {
        String numero = binding.editTextNumero.getText().toString().trim();
        if (numero.isEmpty()) {
            binding.editTextNumero.clearFocus();
            binding.inputLayoutNumero.setErrorEnabled(true);
            binding.inputLayoutNumero.setError(null);
            return false;
        } else if (numero.length() < 8) {
            binding.inputLayoutNumero.setError("Faltan dígitos");
            binding.inputLayoutNumero.requestFocus();
            return false;
        } else {
            binding.inputLayoutNumero.setErrorEnabled(true);
            binding.inputLayoutNumero.setError(null);
        }

        return true;
    }

    private boolean validateTransferirClave() {
        String clave = binding.inputEditTextClave.getText().toString().trim();
        if (clave.isEmpty()) {
            binding.inputEditTextClave.clearFocus();
            binding.inputLayoutClave.setErrorEnabled(true);
            binding.inputLayoutClave.setError(null);
            return false;
        } else if (clave.length() < 4) {
            binding.inputLayoutClave.setError("Faltan dígitos");
            binding.inputLayoutClave.requestFocus();
            return false;
        } else {
            binding.inputLayoutClave.setErrorEnabled(true);
            binding.inputLayoutClave.setError(null);
        }
        return true;
    }

    private boolean validateTransferirMonto() {
        String monto = binding.inputEditTextMonto.getText().toString().trim();
        if (monto.contains(".")) {
            showSnackBar("Para envíar centavos déje el monto en blanco", true);
            binding.inputEditTextMonto.getText().clear();
            binding.inputEditTextMonto.clearFocus();
            hideKeyboard(getActivity(), binding.inputEditTextMonto);
            binding.inputLayoutMonto.setErrorEnabled(true);
            binding.inputLayoutMonto.setError(null);
            return false;
        }

        return true;
    }

    private boolean validateAdelanta() {
        String monto = binding.inputEditTextAdelanta.getText().toString().trim();
        if (monto.isEmpty()) {
            binding.inputEditTextAdelanta.clearFocus();
            return false;
        }
        return true;
    }

    // formatear numero de recarga en grupos de a cuatro dígitos
    private String formatNumero(String numero) {
        StringBuilder formattedNumero = new StringBuilder();
        for (int i = 0; i < numero.length(); i++) {
            formattedNumero.append(numero.charAt(i));
            if ((i + 1) % 4 == 0 && i != numero.length() - 1) {
                formattedNumero.append(" ");
            }
        }

        return formattedNumero.toString();
    }

    private class ValidationTextWatcher implements TextWatcher {
        private View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            int id = view.getId();
            if (id == R.id.input_edit_text_recarga) {
                String numero =
                        binding.inputEditTextRecarga.getText().toString().replaceAll("\\s+", "");
                String numeroFormateado = formatNumero(numero);
                if (!numeroFormateado.equals(binding.inputEditTextRecarga.getText().toString())) {
                    binding.inputEditTextRecarga.setText(numeroFormateado);
                    binding.inputEditTextRecarga.setSelection(numeroFormateado.length());
                }
                validateTextRecarga();
                return;
            }
            if (id == R.id.edit_text_numero) {
                validateTransferirNumero();
                return;
            }
            if (id == R.id.input_edit_text_clave) {
                validateTransferirClave();
                return;
            }
            if (id == R.id.input_edit_text_monto) {
                validateTransferirMonto();
                return;
            }
        }
    }

    public void onOptionsSelected(Set<String> select) {
        if (select.contains("recarga")) {
            binding.recarga.setVisibility(View.GONE);
        } else {
            binding.recarga.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // TODO: Obtener resultado del codigo QR escaneado.
        MainActivity main = (MainActivity) getActivity();
        String code = main.getCode().toString();
        if (code != null && !code.isEmpty()) {
            binding.editTextNumero.setText(code);
        }
    }

    public void showSnackBar(String message, boolean dimissable) {
        CoordinatorLayout coordinator = ((MainActivity) getActivity()).getCoordnator();
        BottomNavigationView nav = ((MainActivity) getActivity()).getBottomNavigation();
        Snackbar snack = Snackbar.make(coordinator, message, Snackbar.LENGTH_SHORT);
        snack.setAnchorView(nav);
        if (dimissable) {
            snack.dismiss();
        } else {
            snack.setDuration(Snackbar.LENGTH_SHORT);
        }
        snack.show();
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(
                    new ScanContract(),
                    result -> {
                        if (result.getContents() != null) {
                            String code = result.getContents().toString();
                            binding.inputEditTextRecarga.setText(code);
                        }
                    });

    // launcher select contacts
    private final ActivityResultLauncher<Void> pickContact =
            registerForActivityResult(
                    new ActivityResultContracts.PickContact(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri uri) {
                            if (uri != null) {
                                try {
                                    Cursor cursor =
                                            getActivity()
                                                    .getContentResolver()
                                                    .query(uri, null, null, null, null);
                                    if (cursor != null && cursor.getCount() > 0) {
                                        while (cursor.moveToNext()) {
                                            String id =
                                                    cursor.getString(
                                                            cursor.getColumnIndex(
                                                                    ContactsContract.Contacts._ID));
                                            String name =
                                                    cursor.getString(
                                                            cursor.getColumnIndex(
                                                                    ContactsContract.Contacts
                                                                            .DISPLAY_NAME));

                                            if (hasPhoneNumber(cursor)) {
                                                binding.inputLayoutNumero.setHelperText(name);
                                                // Retrieve and process phone numbers
                                                processPhoneNumbers(id, name);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    binding.inputLayoutNumero.setHelperText(null);
                                    binding.editTextNumero.setText(null);
                                }
                            }
                        }
                    });

    private boolean hasPhoneNumber(Cursor cursor) {
        int phoneNumberColumnIndex =
                cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        String phoneNumberValue = cursor.getString(phoneNumberColumnIndex);
        return Integer.parseInt(phoneNumberValue) > 0;
    }

    private void processPhoneNumbers(String contactId, String contactName) {
        Cursor phoneCursor =
                getActivity()
                        .getContentResolver()
                        .query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[] {contactId},
                                null);

        while (phoneCursor.moveToNext()) {
            String number =
                    phoneCursor.getString(
                            phoneCursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
            String cleanedNumber = cleanPhoneNumber(number);
            if (isValidMobileNumber(cleanedNumber)) {
                binding.editTextNumero.setText(cleanedNumber);
            } else {
                displayInvalidMobileNumberToast(contactName);
                binding.inputLayoutNumero.setHelperText(null);
                binding.editTextNumero.setText(null);
            }
        }

        phoneCursor.close();
    }

    private String cleanPhoneNumber(String number) {
        return number.replace("+53", "")
                .replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .replace("#", "")
                .replace("*", "");
    }

    private boolean isValidMobileNumber(String number) {
        final String VALID_MOBILE_START = "5";
        final int VALID_MOBILE_LENGTH = 8;
        return number.startsWith(VALID_MOBILE_START) && number.length() == VALID_MOBILE_LENGTH;
    }

    private void displayInvalidMobileNumberToast(String contactName) {
        showSnackBar(contactName + " no es un número válido!", true);
    }

    private void hideKeyboard(Context context, TextInputEditText ediText) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ediText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result) {
                                // PERMISSION GRANTED
                                pickContact.launch(null);
                            } else {
                                // PERMISSION NOT GRANTED
                            }
                        }
                    });
}
