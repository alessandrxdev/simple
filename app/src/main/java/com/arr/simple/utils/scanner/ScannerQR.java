package com.arr.simple.utils.scanner;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.arr.simple.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

public class ScannerQR extends Activity implements DecoratedBarcodeView.TorchListener {
    
    private CaptureManager capture;
    private DecoratedBarcodeView view;
    private ViewfinderView viewFinder;
    private ImageView imageFlash;
    private CheckBox flash;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        fullScreen();
        setContentView(R.layout.layout_cutom_scanner);
        
        view = findViewById(R.id.zxing_barcode_scanner);
        view.setTorchListener(this);
        viewFinder = findViewById(R.id.zxing_viewfinder_view);
        flash = findViewById(R.id.switch_flashlight);
        
        
        capture = new CaptureManager(this, view);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.setShowMissingCameraPermissionDialog(true);
        capture.decode();
        
        if(!hasFlash()){
            flash.setVisibility(View.GONE);
        }
        
        flash.setOnCheckedChangeListener((buttonView, isChecked) ->{
            if(isChecked){
                view.setTorchOn();
            }else{
                view.setTorchOff();
            }
        });
        
        // background 
        changeMaskColor(null);
        changeLaserVisibility(false);
    }

    private void fullScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    
    private boolean hasFlash(){
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        
    }
    
    @Override
    public void onTorchOn() {
        
    }
    
    @Override
    public void onTorchOff() {
        
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return view.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
    
    public void changeMaskColor(View view) {
        int color = Color.parseColor("#88000000");
        viewFinder.setMaskColor(color);
    }

    public void changeLaserVisibility(boolean visible) {
        viewFinder.setLaserVisibility(visible);
    }
    
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
