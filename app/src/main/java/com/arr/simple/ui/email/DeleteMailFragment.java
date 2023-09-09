package com.arr.simple.ui.email;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.arr.nautamail.NautaMail;
import com.arr.nautamail.exception.InvalidCredentialsException;
import com.arr.nautamail.exception.MailResolverException;
import com.arr.nautamail.model.MailCount;
import com.arr.simple.databinding.FragmentMailBinding;
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

        binding.buttonSync.setOnClickListener(
                view -> {
                    String email = binding.editUser.getText().toString().trim();
                    String password = binding.editPassword.getText().toString().trim();
                    if (email.isEmpty() && password.isEmpty()) {
                        Toast.makeText(
                                        getActivity(),
                                        "No pueden haber campos vacíos",
                                        Toast.LENGTH_LONG)
                                .show();
                    } else {
                        connect(email, password);
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
                        mail.connet(user, password);
                        count.inboxCount = mail.getInboxMailCount();
                        count.trashCount = mail.getTrashMailCount();
                        count.sendCount = mail.getSentMailCount();
                        handleError("¡Actualizando!");
                        getActivity()
                                .runOnUiThread(
                                        () -> {
                                            String inbox = count.inboxCount + " correos";
                                            String trash = count.trashCount + " correos";
                                            String send = count.sendCount + " correos";
                                            binding.textInbox.setText(inbox);
                                            binding.textDelete.setText(trash);
                                            binding.textSend.setText(send);
                                            handleError("¡Se ha actualizado!");
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
                            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                        });
    }
}
