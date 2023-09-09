package com.arr.simple.ui.email;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.arr.simple.MainActivity;
import com.arr.simple.R;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.arr.nautamail.NautaMail;
import com.arr.nautamail.exception.InvalidCredentialsException;
import com.arr.nautamail.exception.MailResolverException;
import com.arr.nautamail.model.MailCount;
import com.arr.simple.databinding.FragmentMailBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteMailFragment extends Fragment {

    private FragmentMailBinding binding;
    private NautaMail mail;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @NonNull ViewGroup parent,
            Bundle savedInstanceState) {
        binding = FragmentMailBinding.inflate(inflater, parent, false);

        // TODO: import class NautaMail
        mail = new NautaMail();

        binding.buttonDelete.setEnabled(false);
        binding.buttonSync.setOnClickListener(
                view -> {
                    String email = binding.editUser.getText().toString().trim();
                    String password = binding.editPassword.getText().toString().trim();
                    if (email.isEmpty() && password.isEmpty()) {
                        showSnackBar("¡Hay campos vacíos!");
                        hideKeyboard(getActivity(), binding.editPassword);
                    } else {
                        showSnackBar("Comprobando, espere...");
                        hideKeyboard(getActivity(), binding.editPassword);
                        connect(email, password);
                    }
                });

        binding.buttonDelete.setOnClickListener(
                view -> {
                    String email = binding.editUser.getText().toString().trim();
                    String password = binding.editPassword.getText().toString().trim();
                    if (email.isEmpty() && password.isEmpty()) {
                        showSnackBar("¡Hay campos vacíos!");
                        hideKeyboard(getActivity(), binding.editPassword);
                    } else {
                        delete(email, password);
                    }
                });
        return binding.getRoot();
    }

    private void connect(String user, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(
                () -> {
                    MailCount count = new MailCount();
                    try {
                        mail.syncAllEmails(user, password);
                        count.inboxCount = mail.getInboxCount();
                        count.trashCount = mail.getTrashCount();

                        getActivity()
                                .runOnUiThread(
                                        () -> {
                                            String inbox = count.inboxCount + " correos";
                                            String trash = count.trashCount + " correos";
                                            binding.textInbox.setText(inbox);
                                            binding.textDelete.setText(trash);
                                            binding.buttonDelete.setEnabled(true);
                                            handleError("¡Correos recuperados!");
                                        });
                    } catch (MailResolverException e) {
                        handleError(e.getMessage());
                    } catch (Exception e) {
                        handleError(e.getMessage());
                    }
                });
        executor.shutdown();
    }

    private void handleError(String error) {
        getActivity()
                .runOnUiThread(
                        () -> {
                            showSnackBar(error);
                            hideKeyboard(getActivity(), binding.editPassword);
                        });
    }

    private void delete(String user, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(
                () -> {
                    MailCount count = new MailCount();
                    try {
                        mail.deleteMails(user, password);
                        count.inboxCount = mail.getInboxCount();
                        count.trashCount = mail.getTrashCount();

                        getActivity()
                                .runOnUiThread(
                                        () -> {
                                            String inbox = count.inboxCount + " correos";
                                            String trash = count.trashCount + " correos";
                                            binding.textInbox.setText(inbox);
                                            binding.textDelete.setText(trash);
                                            handleError("¡Se han eliminado!");
                                        });
                    } catch (MailResolverException e) {
                        handleError(e.getMessage());
                    } catch (Exception e) {
                        handleError(e.getMessage());
                    }
                });
        executor.shutdown();
    }

    private void hideKeyboard(Context context, TextInputEditText ediText) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ediText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showSnackBar(String message) {
        CoordinatorLayout coordinator = ((MainActivity) getActivity()).getCoordnator();
        Snackbar snack = Snackbar.make(coordinator, message, Snackbar.LENGTH_SHORT);
        snack.show();
    }
}
