package com.arr.simple.ui.nauta.correo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.arr.nautaclear.NautaMail;
import com.arr.nautaclear.model.MailCount;
import com.arr.nautaclear.utils.EmailsCallback;
import com.arr.simple.MainActivity;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentMailBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class ClearMailFragment extends Fragment {

    private FragmentMailBinding binding;
    private NautaMail nauta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle arg2) {
        binding = FragmentMailBinding.inflate(inflater, parent, false);

        // nauta mail
        nauta = new NautaMail(requireActivity());
        binding.buttonDelete.setEnabled(false);

        // TODO: sincronizar cantidad de correos
        binding.buttonSync.setOnClickListener(
                view -> {
                    String email = binding.editUser.getText().toString().trim();
                    String password = binding.editPassword.getText().toString().trim();
                    if (email.isEmpty() && password.isEmpty()) {
                        showSnackBar(getString(R.string.user_and_password_empty));
                        hideKeyboard(binding.editUser);
                    } else {
                        showSnackBar("Cargando correos, espere...");
                        hideKeyboard(binding.editPassword);
                        nauta.obtainsEmail(
                                email,
                                password,
                                new EmailsCallback() {
                                    @Override
                                    public void updateUI(MailCount count) {
                                        String inbox = count.inboxCount + " correos";
                                        String trash = count.trashCount + " correos";
                                        if (isVisible()) {
                                            binding.textInbox.setText(inbox);
                                            binding.textDelete.setText(trash);
                                            binding.buttonDelete.setEnabled(true);
                                        }
                                        showSnackBar("Correos cargados");
                                    }

                                    @Override
                                    public void handleException(Exception e) {
                                        showSnackBar("Error: " + e);
                                    }
                                });
                    }
                });

        // TODO: Eliminar correos del servidor
        binding.buttonDelete.setOnClickListener(
                view -> {
                    String email = binding.editUser.getText().toString().trim();
                    String password = binding.editPassword.getText().toString().trim();
                    if (email.isEmpty() && password.isEmpty()) {
                        showSnackBar(getString(R.string.user_and_password_empty));
                        hideKeyboard(binding.editPassword);
                    } else {
                        showSnackBar("Eliminando correos, espere...");
                        hideKeyboard(binding.editPassword);
                        nauta.deleteEmails(
                                email,
                                password,
                                new EmailsCallback() {
                                    @Override
                                    public void updateUI(MailCount count) {
                                        String inbox = count.inboxCount + " correos";
                                        String trash = count.trashCount + " correos";
                                        if (isVisible()) {
                                            binding.textInbox.setText(inbox);
                                            binding.textDelete.setText(trash);
                                        }
                                        showSnackBar("Correos eliminados");
                                    }

                                    @Override
                                    public void handleException(Exception e) {
                                        showSnackBar("Error: " + e);
                                    }
                                });
                    }
                });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showSnackBar(String message) {
        if (isVisible()) {
            CoordinatorLayout coordinator = ((MainActivity) requireActivity()).getCoordinator();
            if (coordinator != null) {
                Snackbar.make(coordinator, message, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void hideKeyboard(TextInputEditText ediText) {
        InputMethodManager imm =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ediText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
