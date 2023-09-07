package com.arr.simple.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.arr.simple.MainActivity;
import com.arr.simple.R;
import androidx.appcompat.app.AppCompatActivity;
import com.arr.fingerprint.Interface.BiometricCallback;
import com.arr.fingerprint.Manager.BiometricManager;
import com.arr.simple.databinding.ActivityBloqueoBinding;
import com.google.android.material.elevation.SurfaceColors;
import java.util.Timer;
import java.util.TimerTask;

public class BloqueoActivity extends AppCompatActivity {

    private ActivityBloqueoBinding binding;
    private BiometricManager mBiometricManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBloqueoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));

        // start authenticatio
        new BiometricManager.BiometricBuilder(this)
                .setTitle("Verifique su identidad")
                .setSubtitle("")
                .setDescription("Use su huella digital para verificar su identidad")
                .setNegativeButtonText("Cancelar")
                .build()
                .authenticate(biometricCallback);

        // buttom recharge biometric
        binding.firgenprint.setOnClickListener(
                view -> {
                    new BiometricManager.BiometricBuilder(this)
                            .setTitle("Verifique su identidad")
                            .setSubtitle("")
                            .setDescription("Use su huella digital para verificar su identidad")
                            .setNegativeButtonText("Cancelar")
                            .build()
                            .authenticate(biometricCallback);
                });
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void viewIconFirgentprint() {
        Timer timer = new Timer();
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable changeImageTask =
                () -> {
                    binding.firgenprint.setImageResource(R.drawable.ic_huella_24px);
                    binding.textMessage.setText("Presione para desbloquear");
                };
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(changeImageTask);
                    }
                },
                2000);
    }

    private BiometricCallback biometricCallback =
            new BiometricCallback() {
                @Override
                public void onSdkVersionNotSupported() {
                    /*
                     *  Will be called if the device sdk version does not support Biometric authentication
                     */
                }

                @Override
                public void onBiometricAuthenticationNotSupported() {
                    /*
                     *  Will be called if the device does not contain any fingerprint sensors
                     */
                }

                @Override
                public void onBiometricAuthenticationNotAvailable() {
                    /*
                     *  The device does not have any biometrics registered in the device.
                     */
                }

                @Override
                public void onBiometricAuthenticationPermissionNotGranted() {
                    /*
                     *  android.permission.USE_BIOMETRIC permission is not granted to the app
                     */
                }

                @Override
                public void onBiometricAuthenticationInternalError(String error) {
                    /*
                     *  This method is called if one of the fields such as the title, subtitle,
                     * description or the negative button text is empty
                     */
                }

                @Override
                public void onAuthenticationFailed() {
                    /*
                     * When the fingerprint doesn’t match with any of the fingerprints registered on the device,
                     * then this callback will be triggered.
                     */
                }

                @Override
                public void onAuthenticationCancelled() {
                    binding.textMessage.setText("Has cancelado la operación de huella digital.");
                    binding.firgenprint.setImageResource(R.drawable.ic_cancelle_24px);
                    viewIconFirgentprint();
                    /*
                     * The authentication is cancelled by the user.
                     */
                }

                @Override
                public void onAuthenticationSuccessful() {
                    startMainActivity();
                    /*
                     * When the fingerprint is has been successfully matched with one of the fingerprints
                     * registered on the device, then this callback will be triggered.
                     */
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    /*
                     * This method is called when a non-fatal error has occurred during the authentication
                     * process. The callback will be provided with an help code to identify the cause of the
                     * error, along with a help message.
                     */
                }

                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    binding.textMessage.setText(errString);
                    binding.firgenprint.setImageResource(R.drawable.ic_cancelle_24px);
                    viewIconFirgentprint();
                    /*
                     * When an unrecoverable error has been encountered and the authentication process has
                     * completed without success, then this callback will be triggered. The callback is provided
                     * with an error code to identify the cause of the error, along with the error message.
                     */
                }
            };
}
