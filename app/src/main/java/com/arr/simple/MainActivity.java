package com.arr.simple;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.arr.bugsend.BugSend;
import com.arr.didi.Didi;
import com.arr.simple.activity.BugActivity;
import com.arr.simple.databinding.ActivityMainBinding;
import com.arr.simple.databinding.NavRailHeaderBinding;
import com.arr.simple.services.TrafficFloatingWindow;
import com.arr.simple.utils.Greeting.GreetingUtils;
import com.arr.simple.utils.Scanner.CustomScanner;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private DrawerLayout drawer;
    private ActivityMainBinding binding;
    private NavRailHeaderBinding header;
    private String code = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        // TODO: Quitar el foco de los TextInputEditText al entrar a una Activity o Fragment
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // TODO: Crash Reporter
        StringBuilder builder = new StringBuilder();
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            builder.append("VERSION: ").append(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String versionName = builder.toString();
        new BugSend(this)
                .setTitle(getString(R.string.title_bug))
                .setIcon(R.drawable.ic_bug_report_24px)
                .setMessage(getString(R.string.message_bug))
                .setEmail("soporteapplify@gmail.com")
                .setSubject("REPORTE-SIMPLE")
                .setExtraText(versionName)
                .show();

        // TODO:StatusBarColor
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));

        // TODO: navigationBarColor
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        // load name and greeat
        binding.appBarMain.textSaludo.setText(GreetingUtils.hello());
        if (getNombre().isEmpty()) {
            binding.appBarMain.textName.setText("Usuario");
        } else {
            binding.appBarMain.textName.setText(getNombre());
        }

        drawer = binding.drawerLayout;
        NavigationRailView railView = binding.navRail;
        NavigationView navigationView = binding.navView;
        railView.setOnItemSelectedListener(
                menuItem -> {
                    int id = menuItem.getItemId();
                    if (id == R.id.nav_servicios) {
                        navController.navigate(id, null);
                    }
                    if (id == R.id.nav_correo) {
                        navController.navigate(id, null);
                    }
                    if (id == R.id.nav_telepuntos) {
                        openGoogleMap();
                    }
                    if (id == R.id.nav_settings) {
                        navController.navigate(id, null);
                    }
                    if (id == R.id.nav_about) {
                        navController.navigate(id, null);
                    }
                    return false;
                });

        // TODO: Ocultar balances para android inferior a 8
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            binding.appBarMain
                    .bottomNavigation
                    .getMenu()
                    .findItem(R.id.nav_balance)
                    .setVisible(false);
        }

        // TODO: Acceder a escanear QR con numero de móvil
        View view = railView.getHeaderView();
        header = NavRailHeaderBinding.bind(view);
        header.scanner.setOnClickListener(
                v -> {
                    ScanOptions scanner = new ScanOptions();
                    scanner.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                    scanner.setCaptureActivity(CustomScanner.class);
                    scanner.setPrompt("Centre el QR para escanear el numero movil");
                    scanner.setOrientationLocked(true);
                    scanner.setBeepEnabled(false);
                    scanner.setTimeout(15000);
                    barcodeLauncher.launch(scanner);
                });

        // TODO: Passing each menu ID as a set of Ids because each
        // TODO: menu should be considered as top level destinations.
        mAppBarConfiguration =
                new AppBarConfiguration.Builder(
                                R.id.nav_home,
                                R.id.nav_balance,
                                R.id.nav_compras,
                                R.id.nav_llamadas,
                                R.id.nav_nauta)
                        .setOpenableLayout(drawer)
                        .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        supporNavigateBottomNavigation();
        NavigationUI.setupWithNavController(navigationView, navController);
        // loadProfile();

        // TODO: Navigation destination
        navController.addOnDestinationChangedListener(
                (controller, destination, arguments) -> {
                    int id = destination.getId();
                    if (id == R.id.nav_settings
                            || id == R.id.nav_ui
                            || id == R.id.nav_pref_balance
                            || id == R.id.nav_security
                            || id == R.id.nav_sim
                            || id == R.id.nav_perfil
                            || id == R.id.nav_correo
                            || id == R.id.nav_servicios
                            || id == R.id.nav_info_nauta
                            || id == R.id.nav_conectado
                            || id == R.id.nav_about) {
                        binding.appBarMain.contentToolbar.setVisibility(View.GONE);
                        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));
                        binding.appBarMain.bottomNavigation.setVisibility(View.GONE);
                        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            binding.drawerLayout.closeDrawer(GravityCompat.START);
                        }
                    }
                    if (id == R.id.nav_home
                            || id == R.id.nav_balance
                            || id == R.id.nav_compras
                            || id == R.id.nav_llamadas
                            || id == R.id.nav_nauta) {
                        binding.appBarMain.contentToolbar.setVisibility(View.VISIBLE);
                        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
                        binding.appBarMain.bottomNavigation.setVisibility(View.VISIBLE);
                        binding.appBarMain.toolbar.setNavigationIcon(loadProfile());
                    }
                });
    }

    private void openGoogleMap() {
        startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.view_telepuntos))));
    }

    private Drawable loadProfile() {
        Bitmap image =
                new Didi(this).load().setDirectoryName("Profile").setRounded(true).getBitmap();
        if (image != null) {
            int width = 100;
            int height = 100;
            Bitmap profile = Bitmap.createScaledBitmap(image, width, height, true);
            return new BitmapDrawable(getResources(), profile);
        } else {
            return getDrawable(R.drawable.ic_account_circle_24px);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void supporNavigateBottomNavigation() {
        NavigationUI.setupWithNavController(binding.appBarMain.bottomNavigation, navController);
    }

    // TODO: Cargar nombre de la persona
    public String getNombre() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getString("name", "");
    }

    public CoordinatorLayout getCoordnator() {
        return binding.appBarMain.coordinator;
    }

    public BottomNavigationView getBottomNavigation() {
        return binding.appBarMain.bottomNavigation;
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public String getCode() {
        return code;
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(
                    new ScanContract(),
                    result -> {
                        if (result.getContents() != null) {
                            code = result.getContents().toString();
                            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                binding.drawerLayout.closeDrawer(GravityCompat.START);
                            }
                        }
                    });

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences spFloating = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isActive = spFloating.getBoolean("traffic", false);
        if (isActive) {
            Intent intent = new Intent(this, TrafficFloatingWindow.class);
            startService(intent);
        }
    }
}
