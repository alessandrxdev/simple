package com.arr.simple.ui.email;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.arr.nautaclear.NautaMail;
import com.arr.nautaclear.model.MailCount;
import com.arr.nautaclear.utils.EmailsCallback;
import com.arr.simple.MainActivity;
import com.arr.simple.R;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.arr.simple.databinding.FragmentMailBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteMailFragment extends Fragment {

    private FragmentMailBinding binding;
    private NautaMail mail;
    private Exception err;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @NonNull ViewGroup parent,
            Bundle savedInstanceState) {
        binding = FragmentMailBinding.inflate(inflater, parent, false);

        // TODO: import class NautaMail
        mail = new NautaMail(requireActivity());

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
                        mail.obtainsEmail(
                                email,
                                password,
                                new EmailsCallback() {
                                    @Override
                                    public void updateUI(MailCount count) {
                                        String inbox = count.inboxCount + " correos";
                                        String trash = count.trashCount + " correos";
                                        binding.textInbox.setText(inbox);
                                        binding.textDelete.setText(trash);
                                        binding.buttonDelete.setEnabled(true);
                                        showSnackBar("Correos cargados");
                                    }

                                    @Override
                                    public void handleException(Exception e) {
                                        showSnackBar("Error: " + e);
                                    }
                                });
                    }
                });

        // delete mails
        binding.buttonDelete.setOnClickListener(
                view -> {
                    String email = binding.editUser.getText().toString().trim();
                    String password = binding.editPassword.getText().toString().trim();
                    if (email.isEmpty() && password.isEmpty()) {
                        showSnackBar("¡Hay campos vacíos!");
                        hideKeyboard(getActivity(), binding.editPassword);
                    } else {
                        mail.deleteEmails(
                                email,
                                password,
                                new EmailsCallback() {
                                    @Override
                                    public void updateUI(MailCount count) {
                                        String inbox = count.inboxCount + " correos";
                                        String trash = count.trashCount + " correos";
                                        binding.textInbox.setText(inbox);
                                        binding.textDelete.setText(trash);
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

    private void hideKeyboard(Context context, TextInputEditText ediText) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ediText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showSnackBarAction(String message, String error, boolean isAnchor) {
        CoordinatorLayout coordinator = ((MainActivity) getActivity()).getCoordnator();
        BottomNavigationView nav = ((MainActivity) getActivity()).getBottomNavigation();
        Snackbar snack = Snackbar.make(coordinator, message, Snackbar.LENGTH_SHORT);
        if (isAnchor) {
            snack.setAnchorView(nav);
        }
        snack.setAction(
                "Enviar",
                (click) -> {
                    sendCrashReport(error);
                });
        snack.show();
    }

    public void showSnackBar(String message) {
        CoordinatorLayout coordinator = ((MainActivity) getActivity()).getCoordnator();
        Snackbar snack = Snackbar.make(coordinator, message, Snackbar.LENGTH_SHORT);
        snack.show();
    }

    private void sendCrashReport(String report) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"soporteapplify@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "NAUTA");
        intent.putExtra(Intent.EXTRA_TEXT, report);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:"));
        getActivity().startActivity(Intent.createChooser(intent, "Enviar reporte"));
    }
}
