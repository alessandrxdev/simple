package com.arr.simple.utils.Scanner;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import com.arr.simple.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

public class CustomScanner extends Activity implements DecoratedBarcodeView.TorchListener {

    private CaptureManager manager;
    private DecoratedBarcodeView decorate;
    private ImageView flash;
    private ViewfinderView viewFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow()
                .setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_cutom_scanner);

        decorate = findViewById(R.id.zxing_barcode_scanner);
        decorate.setTorchListener(this);

        flash = findViewById(R.id.switch_flashlight);
        viewFinder = findViewById(R.id.zxing_viewfinder_view);

        if (!hasFlash()) {
            flash.setVisibility(View.GONE);
        }

        manager = new CaptureManager(this, decorate);
        manager.initializeFromIntent(getIntent(), savedInstanceState);
        manager.setShowMissingCameraPermissionDialog(false);
        manager.decode();

        changeMaskColor(null);
        changeLaserVisibility(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        manager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return decorate.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * Check if the device's camera has a Flashlight.
     *
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext()
                .getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
        if (flash.getDrawable().getConstantState()
                == getResources().getDrawable(R.drawable.ic_palette_24px).getConstantState()) {
            decorate.setTorchOn();
        } else {
            decorate.setTorchOff();
        }
    }

    public void changeMaskColor(View view) {
        int color = Color.parseColor("#88000000");
        viewFinder.setMaskColor(color);
    }

    public void changeLaserVisibility(boolean visible) {
        viewFinder.setLaserVisibility(visible);
    }

    @Override
    public void onTorchOn() {
        flash.setImageResource(R.drawable.ic_about_24px);
    }

    @Override
    public void onTorchOff() {
        flash.setImageResource(R.drawable.ic_about_24px);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        manager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
