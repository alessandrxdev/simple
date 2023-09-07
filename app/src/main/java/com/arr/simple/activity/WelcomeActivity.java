package com.arr.simple.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.arr.simple.MainActivity;
import com.arr.simple.databinding.ActivityWelcomeBinding;
import com.google.android.material.elevation.SurfaceColors;

import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;
    private SharedPreferences spHome;
    private SharedPreferences bloqueo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO: navigationBarColor and StatusBarColor
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));

        bloqueo = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isEnabled = bloqueo.getBoolean("bloqueo", false);

        // TODO: Comprobar si es primera vez que se inicia en la app
        spHome = getSharedPreferences("HOME", Context.MODE_PRIVATE);
        boolean one = spHome.getBoolean("isOne", false);
        if (one) {
            if (isEnabled) {
                startActivity(new Intent(this, BloqueoActivity.class));
                finish();
            } else {
                startMainActivity();
            }
        }

        // TODO: link
        binding.textPoliticas.setMovementMethod(LinkMovementMethod.getInstance());

        // TODO: Comprobar que se hayan insertado los datos
        binding.buttonNext.setEnabled(false);

        // TODO: next button
        binding.buttonNext.setOnClickListener(this::nextButton);
        binding.editNombre.addTextChangedListener(nombre);
        binding.editNumero.addTextChangedListener(numero);
    }

    // TODO: guardar usuario y contrsenna
    public void saveData(String name, String number) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", name);
        editor.putString("number", number);
        editor.apply();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void nextButton(View view) {
        saveData(
                Objects.requireNonNull(binding.editNombre.getText()).toString(),
                Objects.requireNonNull(binding.editNumero.getText()).toString());
        SharedPreferences.Editor editorHome = spHome.edit();
        editorHome.putBoolean("isOne", true);
        editorHome.apply();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void status() {
        boolean nombreValido =
                Objects.requireNonNull(binding.editNombre.getText()).toString().length() >= 3;
        boolean numeroValido =
                Objects.requireNonNull(binding.editNumero.getText()).toString().length() >= 8;
        binding.buttonNext.setEnabled(nombreValido && numeroValido);
    }

    private final TextWatcher nombre =
            new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO: no usar
                }

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO: Comprobar si el nombre tiene un mínimo de tres caracteres
                    String nombre = arg0.toString();
                    binding.buttonNext.setEnabled(nombre.length() >= 3);
                    status();
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO: No usar
                }
            };

    private final TextWatcher numero =
            new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO: no usar
                }

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO: Comprobar si el nombre tiene un mínimo de tres caracteres
                    String nombre = arg0.toString();
                    binding.buttonNext.setEnabled(nombre.length() >= 8);
                    status();
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO: No usar
                }
            };
}
