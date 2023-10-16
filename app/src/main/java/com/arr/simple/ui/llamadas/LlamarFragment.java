package com.arr.simple.ui.llamadas;

import android.Manifest;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import com.arr.simple.R;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import com.arr.simple.MainActivity;
import com.arr.simple.adapter.ViewAdapter;
import com.arr.simple.databinding.FragmentLlamarBinding;
import com.arr.simple.model.Grid;
import com.arr.simple.model.Header;
import com.arr.simple.model.Items;
import com.arr.simple.utils.permissions.PermissionCheck;
import com.arr.ussd.Call;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;

public class LlamarFragment extends Fragment {

    private FragmentLlamarBinding binding;
    private SharedPreferences sp;
    private String SIM;

    private ViewAdapter adapter;
    private ArrayList<Items> list = new ArrayList<>();
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentLlamarBinding.inflate(inflater, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SIM = sp.getString("sim", "0");

        // TODO: endIconOnClick
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

        // llamar con *99
        binding.buttonAsterisco.setOnClickListener(
                view -> {
                    String number = binding.inputEditTextNumero.getText().toString();
                    if (number.isEmpty()) {
                        showSnackBar("Inserte un número", true);
                    } else {
                        new Call(getContext()).code("*99" + number, SIM);
                    }
                });

        // llamar con número privado
        binding.buttonPrivado.setOnClickListener(
                view -> {
                    String number = binding.inputEditTextNumero.getText().toString();
                    if (number.isEmpty()) {
                        showSnackBar("Inserte un número", true);
                    } else {
                        new Call(getContext()).code(Uri.encode("#") + "31" + Uri.encode("#") + number, SIM);
                    }
                });
        
        
        // TODO: Adapter in recyclerView
        binding.recyclerView.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setSpanSizeLookup(
                new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int viewType = adapter.getItemViewType(position);
                        if (viewType == Header.VIEW_HEADER) {
                            return 2;
                        } else {
                            return 1;
                        }
                    }
                });
        binding.recyclerView.setLayoutManager(manager);
        adapter =
                new ViewAdapter(
                        getActivity(),
                        list,
                        position -> {
                            // onclick
                            onClick(position);
                        });
        binding.recyclerView.setAdapter(adapter);
        
        // list
        list.add(new Header("Emergencia"));
        list.add(new Grid("103", "Antidrogas",0));
        list.add(new Grid("104", "Ambulancia",0));
        list.add(new Grid("105", "Bomberos",0));
        list.add(new Grid("106", "Policía",0));
        list.add(new Grid("107", "Resc. Salvamento Marítimo",0));
        list.add(new Header("Útiles"));
        list.add(new Grid("Operadora", "Atención al cliente",0));
        list.add(new Grid("Nauta", "Operadora Nauta Hogar",0));
        list.add(new Grid("Reportes", "Reporte de telefonía fija",0));
        list.add(new Grid("Emp. Eléctrica", "Atención al cliente",0));
        list.add(new Grid("Telefonía Móvil", "Quejas y Reclamos",0));

        
        return binding.getRoot();
    }
private void onClick(int position) {
        switch (position) {
            case 1:
                new Call(getActivity()).code("103", SIM);
                break;
            case 2:
                new Call(getActivity()).code("104", SIM);
                break;
            case 3:
                new Call(getActivity()).code("105", SIM);
                break;
            case 4:
                new Call(getActivity()).code("106", SIM);
                break;
            case 5:
                new Call(getActivity()).code("107", SIM);
                break;
            case 7:
                new Call(getActivity()).code("52642266", SIM);
                break;
            case 8:
                new Call(getActivity()).code("80043434", SIM);
                break;
            case 9:
                new Call(getActivity()).code("114", SIM);
                break;
            case 10:
                new Call(getActivity()).code("18888", SIM);
                break;
            case 11:
                new Call(getActivity()).code("118", SIM);
                break;
        }
    }
    
    public void showSnackBar(String message, boolean dimissable) {
        CoordinatorLayout coordinator = ((MainActivity) getActivity()).getCoordinator();
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
                                    binding.inputEditTextNumero.setText(null);
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
                binding.inputEditTextNumero.setText(cleanedNumber);
            } else {
                displayInvalidMobileNumberToast(contactName);
                binding.inputLayoutNumero.setHelperText(null);
                binding.inputEditTextNumero.setText(null);
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

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result) {
                                // PERMISSION GRANTED
                                pickContact.launch(null);
                            }
                        }
                    });
}
