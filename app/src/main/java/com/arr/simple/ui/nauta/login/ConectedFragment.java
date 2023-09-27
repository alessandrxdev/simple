package com.arr.simple.ui.nauta.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import com.arr.simple.MainActivity;
import com.arr.simple.databinding.FragmentConectedLoginBinding;
import com.arr.simple.nauta.LoginNauta;
import com.arr.simple.nauta.utils.ExceptionCallback;
import com.arr.simple.utils.preferences.Data;
import com.google.android.material.snackbar.Snackbar;

public class ConectedFragment extends Fragment {

    private FragmentConectedLoginBinding binding;
    private LoginNauta nauta;
    private Data data;
    private CountDownTimer countDown;

    @Override
    public View onCreateView(LayoutInflater inflate, ViewGroup parent, Bundle arg2) {
        binding = FragmentConectedLoginBinding.inflate(inflate, parent, false);
        nauta = new LoginNauta(requireActivity());
        data = new Data(getContext());

        // info account
        binding.textAccount.setText(data.load("login", "account"));
        binding.textSaldo.setText(data.load("login", "saldo"));
        binding.textStatus.setText(data.load("login", "estado"));

        // tiempo
        timeConnected();

        // desconectarse
        binding.buttonDisconect.setOnClickListener(view -> disconect());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (countDown != null) {
            countDown.cancel();
            countDown = null;
        }
    }

    private void disconect() {
        if (countDown != null) {
            countDown.cancel();
            countDown = null;
        }
        nauta.desconectar(
                new ExceptionCallback() {
                    @Override
                    public void handlerException(Exception e) {
                        showSnackBar("Error: " + e);
                    }
                });
    }

    private void timeConnected() {
        long miliseconds = nauta.time();
        countDown =
                new CountDownTimer(miliseconds, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int h = (int) (millisUntilFinished / 3600000);
                        int m = (int) (millisUntilFinished - h * 3600000) / 60000;
                        int s = (int) (millisUntilFinished - h * 3600000 - m * 60000) / 1000;
                        String hh = h < 10 ? "0" + h : String.valueOf(h);
                        String mm = m < 10 ? "0" + m : String.valueOf(m);
                        String ss = s < 10 ? "0" + s : String.valueOf(s);
                        binding.textTime.setText(String.format("%s:%s:%s", hh, mm, ss));
                    }

                    public void onFinish() {
                        binding.textTime.setText("00:00:00");
                    }
                }.start();
    }

    private void showSnackBar(String message) {
        CoordinatorLayout view = ((MainActivity) getContext()).getCoordinator();
        if (isVisible()) {
            Snackbar snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            snack.show();
        }
    }
}
