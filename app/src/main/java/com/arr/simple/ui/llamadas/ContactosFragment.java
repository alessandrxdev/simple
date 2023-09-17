package com.arr.simple.ui.llamadas;

import android.Manifest;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import com.arr.simple.adapter.ContactAdapter;
import com.arr.simple.databinding.FragmentContactosBinding;
import com.arr.simple.model.Contact;
import com.arr.simple.model.Header;
import com.arr.simple.model.Items;
import com.arr.ussd.Call;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ContactosFragment extends Fragment {

    private FragmentContactosBinding binding;
    private ContactAdapter adapter;
    private List<Items> list = new ArrayList<>();
    private SharedPreferences sp;
    private String SIM;

    private int paginaActual = 1;
    private int elementosPorPagina = 20;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentContactosBinding.inflate(inflater, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SIM = sp.getString("sim", "0");

        // TODO: Adapter and recyclerView
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter =
                new ContactAdapter(
                        requireContext(),
                        list,
                        privado -> {
                            // TODO: onClick private
                            callPrivate(privado);
                        },
                        revertido -> {
                            // TODO: onClick revertido
                            callAsterisck(revertido);
                        });
        binding.recyclerView.setAdapter(adapter);
        checkPermission();

        return binding.getRoot();
    }

    private void callAsterisck(Contact item) {
        String number = item.getNumber();
        Call call = new Call(requireContext());
        call.code("*99" + number, SIM);
    }

    private void callPrivate(Contact item) {
        String number = item.getNumber();
        Call call = new Call(requireContext());
        call.code(Uri.encode("#") + "31" + Uri.encode("#") + number, SIM);
    }

    private void loadSiguiente() {
        int offset = (paginaActual - 1) * elementosPorPagina;
        load(offset, elementosPorPagina);
        paginaActual++;
    }

    @SuppressWarnings("deprecation")
    private void load(int offset, int limit) {
        new AsyncTask<Void, Void, List<Items>>() {
            @Override
            protected List<Items> doInBackground(Void... voids) {
                return obtenerContactosPaginados(offset, limit);
            }

            @Override
            protected void onPostExecute(List<Items> nuevosContactos) {
                if (nuevosContactos != null) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    list.addAll(nuevosContactos);
                    adapter.setContactList(list);
                    adapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }

    private List<Items> obtenerContactosPaginados(int offset, int limit) {
        Set<Contact> contactSet = new HashSet<>();
        List<Contact> contactFavorite = new ArrayList<>();
        ContentResolver content = requireContext().getContentResolver();
        if (content != null) {
            Cursor cursor =
                    content.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String id =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name =
                            cursor.getString(
                                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(
                                    cursor.getString(
                                            cursor.getColumnIndex(
                                                    ContactsContract.Contacts.HAS_PHONE_NUMBER)))
                            > 0) {
                        Cursor phoneCursor =
                                content.query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                        new String[] {id},
                                        null);

                        boolean contactAded = false;
                        while (phoneCursor.moveToNext()) {
                            String number =
                                    phoneCursor.getString(
                                            phoneCursor.getColumnIndex(
                                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String photoURI =
                                    phoneCursor.getString(
                                            phoneCursor.getColumnIndex(
                                                    ContactsContract.CommonDataKinds.Phone
                                                            .PHOTO_URI));

                            String replace =
                                    number.replace("+53", "")
                                            .replace(" ", "")
                                            .replace("(", "")
                                            .replace(")", "")
                                            .replace("#", "")
                                            .replace("*", "");
                            if (replace.length() >= 8) {
                                replace = replace.substring(replace.length() - 8);
                            }

                            String numero = null;
                            String nombre = null;
                            if (replace.startsWith("5") && replace.length() == 8) {
                                numero = replace;
                                nombre = name;
                            }

                            if (numero != null && nombre != null) {
                                boolean isFavorite = isContactFavorite(id);
                                Contact contact = new Contact(nombre, numero, photoURI, isFavorite);
                                if (!contactAded) {
                                    contactSet.add(contact);
                                    contactAded = true;
                                }
                            }
                        }
                        phoneCursor.close();
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        List<Items> sortedContacts = new ArrayList<>(contactSet);
        List<Items> finalContacts = new ArrayList<>();
        boolean hasFavorite = false;
        for (Items item : sortedContacts) {
            if (item instanceof Contact && ((Contact) item).isFavorite()) {
                hasFavorite = true;
                break;
            }
        }
        if (hasFavorite) {
            finalContacts.add(new Header("Favoritos"));
            for (Items item : sortedContacts) {
                if (item instanceof Contact && ((Contact) item).isFavorite()) {
                    finalContacts.add(item);
                }
            }
        }
        finalContacts.add(new Header("Contactos"));
        for (Items item : sortedContacts) {
            if (!(item instanceof Contact) || !((Contact) item).isFavorite()) {
                finalContacts.add(item);
            }
        }
        Collections.sort(
                finalContacts,
                (item1, item2) -> {
                    if (item1 instanceof Contact && item2 instanceof Contact) {
                        Contact contact1 = (Contact) item1;
                        Contact contact2 = (Contact) item2;
                        if (contact1.getViewType() == Contact.VIEW_GRID
                                && contact2.getViewType() == Contact.VIEW_GRID) {
                            if (contact1.isFavorite() && !contact2.isFavorite()) {
                                return -1;
                            } else if (!contact1.isFavorite() && contact2.isFavorite()) {
                                return 1;
                            }
                            return contact1.getName().compareToIgnoreCase(contact2.getName());
                        }
                    }
                    return 0;
                });
        return finalContacts;
    }

    private boolean isContactFavorite(String contactId) {
        if (isAdded()) {
            ContentResolver content = getContext().getContentResolver();
            if (content != null) {
                Cursor cursor =
                        content.query(
                                ContactsContract.Contacts.CONTENT_URI,
                                new String[] {ContactsContract.Contacts.STARRED},
                                ContactsContract.Contacts._ID + " = ?",
                                new String[] {contactId},
                                null);

                if (cursor != null && cursor.moveToFirst()) {
                    int starred =
                            cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.STARRED));
                    cursor.close();
                    return starred == 1;
                }
            }
        }

        return false;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                showDialogPermission();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        getActivity(), Manifest.permission.READ_CONTACTS)) {
                    showDialogPermission();
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
                }
            }
        }
    }

    private void showDialogPermission() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Contactos")
                .setCancelable(false)
                .setMessage(
                        "Necesitamos que usted conceda los permisos necesarios para acceder a sus contactos y así usted pueda llamar con *99 o número privado.")
                .setPositiveButton(
                        "Conceder",
                        (dialog, w) -> {
                            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
                        })
                .show();
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            if (result) {
                                loadSiguiente();
                            }
                        }
                    });
}
