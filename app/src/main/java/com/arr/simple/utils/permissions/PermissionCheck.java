package com.arr.simple.utils.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PermissionCheck {

    private Activity activity;
    private String permission;
    private int title;
    private int message;
    private String positiveButton;
    private Runnable codeToExecute;
    private Runnable launchCode;
    private boolean permissionGrated;

    public PermissionCheck(Activity activity) {
        this.activity = activity;
    }

    public PermissionCheck setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public PermissionCheck setTitle(int title) {
        this.title = title;
        return this;
    }

    public PermissionCheck setMessage(int message) {
        this.message = message;
        return this;
    }

    public PermissionCheck setPositiveButton(String positiveButton) {
        this.positiveButton = positiveButton;
        return this;
    }

    public PermissionCheck launchPermission(Runnable runnable) {
        this.launchCode = runnable;
        return this;
    }

    public void executeCode(Runnable codeToExecute) {
        this.codeToExecute = codeToExecute;
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                showDialogPermission();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (ComponentActivity) activity, permission)) {
                    showDialogPermission();
                } else {
                    // Ejecuta el código después de concedido el permiso
                    permissionGrated = true;
                    if (codeToExecute != null) {
                        codeToExecute.run();
                    }
                }
            }
        }
    }

    private void showDialogPermission() {
        new MaterialAlertDialogBuilder(activity)
                .setTitle(activity.getString(title))
                .setCancelable(false)
                .setMessage(activity.getString(message))
                .setPositiveButton(
                        positiveButton,
                        (dialog, w) -> {
                            /*
                                        if (codeToExecute != null) {
                                            codeToExecute.run();
                                        }
                            */
                            if (launchCode != null) {
                                launchCode.run();
                            }
                        })
                .show();
    }
}
