package com.arr.simple.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import androidx.preference.PreferenceManager;
import com.arr.simple.R;
import com.arr.simple.databinding.LayoutFloatingWindowBinding;

public class TrafficFloatingWindow extends Service {

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View view;
    private static final String CHANNEL = "traffic";
    private LayoutFloatingWindowBinding binding;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    private long lastRxBytes = 0;
    private long lastTxBytes = 0;
    private long lastTime = 0;

    private final Runnable runnable =
            new Runnable() {
                public void run() {
                    long currentRxBytes = TrafficStats.getTotalRxBytes();
                    long currentTxBytes = TrafficStats.getTotalTxBytes();
                    long usedRxBytes = currentRxBytes - lastRxBytes;
                    long usedTxBytes = currentTxBytes - lastTxBytes;
                    long currentTime = System.currentTimeMillis();
                    long usedTime = currentTime - lastTime;

                    lastRxBytes = currentRxBytes;
                    lastTxBytes = currentTxBytes;
                    lastTime = currentTime;
                    binding.textSpeed.setText(calculateSpeed(usedTime, usedRxBytes, usedTxBytes));

                    handler.postDelayed(runnable, 1000);
                }

                public String calculateSpeed(long timeTaken, long downBytes, long upBytes) {
                    long downSpeed = 0;
                    long upSpeed = 0;

                    if (timeTaken > 0) {
                        downSpeed = downBytes * 1000 / timeTaken;
                        upSpeed = upBytes * 1000 / timeTaken;
                    }
                    final long mDownSpeed = downSpeed;
                    final long mUpSpeed = upSpeed;

                    String down = setSpeed(mDownSpeed);
                    String up = setSpeed(mUpSpeed);

                    StringBuilder sb = new StringBuilder();
                    sb.append("↓ ").append(down).append(" ↑ ").append(up);

                    binding.textSpeed.setText(sb.toString());
                    return sb.toString();
                }

                private String setSpeed(long speed) {
                    if (speed < 1000000) {
                        return String.format("%.1f KB", (speed / 1000.0));
                    } else if (speed >= 1000000) {
                        if (speed < 10000000) {
                            return String.format("%.1f MB", (speed / 1000000.0));
                        } else if (speed < 100000000) {
                            return String.format("%.1f MB", (speed / 1000000.0));
                        } else {
                            return "+99 MB";
                        }
                    } else {
                        return "";
                    }
                }
            };

    @Override
    public void onCreate() {
        super.onCreate();
        lastRxBytes = TrafficStats.getTotalRxBytes();
        lastTxBytes = TrafficStats.getTotalTxBytes();
        lastTime = System.currentTimeMillis();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        binding = LayoutFloatingWindowBinding.inflate(LayoutInflater.from(this));

        SharedPreferences spColor = PreferenceManager.getDefaultSharedPreferences(this);
        String selectColor = spColor.getString("floating_color", "colorPrimary");
        int color =
                getResources()
                        .getColor(
                                getResources()
                                        .getIdentifier(selectColor, "color", getPackageName()));
        GradientDrawable drawable = (GradientDrawable) binding.floating.getBackground();
        drawable.setColor(color);

        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams =
                    new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                            PixelFormat.TRANSLUCENT);
        } else {
            layoutParams =
                    new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                            PixelFormat.TRANSLUCENT);
        }
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.horizontalMargin = 0.1f;
        layoutParams.verticalMargin = 0.1f;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        
        // touch floating
        binding.floating.setOnTouchListener(
                new View.OnTouchListener() {
                    int x = 0;
                    int y = 0;

                    float touchX;
                    float touchY;

                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                x = layoutParams.x;
                                y = layoutParams.y;
                                touchX = event.getRawX();
                                touchY = event.getRawY();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                layoutParams.x = x + (int) (event.getRawX() - touchX);
                                layoutParams.y = y + (int) (event.getRawY() - touchY);
                                windowManager.updateViewLayout(binding.getRoot(), layoutParams);
                                break;
                        }
                        return true;
                    }
                });
        // view
        windowManager.addView(binding.getRoot(), layoutParams);
        
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities network =
                    connectivityManager.getNetworkCapabilities(
                            connectivityManager.getActiveNetwork());
            if (network != null) {
                if (network.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    binding.connection.setImageResource(R.drawable.ic_data_lte_20px);
                } else if (network.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    binding.connection.setImageResource(R.drawable.ic_nauta_fill_24px);
                } else {
                    binding.connection.setImageResource(R.drawable.ic_calendar_20px);
                }
            }
        }
        
        if(connected()){
           startService(new Intent(this, TrafficFloatingWindow.class));
        }else{
          stopService(new Intent(this, TrafficFloatingWindow.class));
        }

        // crear notofocacion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL, "servicios", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("description");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(
                                getString(R.string.app_name)
                                        + " se está ejecutando en segundo plano")
                        .setSmallIcon(R.drawable.ic_logo_simple);
        int NOTIFICATION_ID = 1;
        startForeground(NOTIFICATION_ID, builder.build());
        handler.post(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            windowManager.removeView(binding.getRoot());
        }
    }
    private boolean connected(){
            ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if(info != null && info.isConnected()){
                return true;
            }else{
                return false;
            }
        }
}
