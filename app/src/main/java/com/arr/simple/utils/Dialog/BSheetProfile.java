package com.arr.simple.utils.Dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;
import androidx.preference.PreferenceManager;
import com.arr.didi.Didi;
import com.arr.simple.R;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.elevation.SurfaceColors;
import java.util.Objects;

public class BSheetProfile extends BottomSheetDialog {

    private ImageView mimage, close;
    private Context mContext;
    private BottomSheetBehavior<View> behavior;
    private Didi didi;

    public BSheetProfile(Context context) {
        super(context);
        init();
    }

    private void init() {
        @SuppressLint("InflateParams")
        View content =
                LayoutInflater.from(getContext()).inflate(R.layout.layout_view_photo_profile, null);
        setContentView(content);
        setFullScreen();
                
        // image
        mimage = content.findViewById(R.id.picture);
        TextView title = (TextView) content.findViewById(R.id.title);
        title.setText(getNombre());
        close = content.findViewById(R.id.close);
        close.setOnClickListener(
                v -> {
                    dismiss();
                });

        // load pic
        didi =
                new Didi(getContext())
                        .load()
                        .setDirectoryName("Profile")
                        .setImage(mimage);
    }

    public BSheetProfile setNavColorSurface(boolean isSurface) {
        if (isSurface) {
            Objects.requireNonNull(getWindow())
                    .setNavigationBarColor(SurfaceColors.SURFACE_1.getColor(getContext()));
        }
        return this;
    }

    public void setBehaviorExpanded(boolean expanded) {
        if (expanded) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                View bsheet = getWindow().findViewById(R.id.bottom_sheet_profile);
                behavior = BottomSheetBehavior.from(bsheet);
                ViewGroup.LayoutParams layoutParams = bsheet.getLayoutParams();
                int windowHeight = getWindowHeight();
                if (layoutParams != null) {
                    layoutParams.height = windowHeight;
                }
                bsheet.setLayoutParams(layoutParams);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    private void setFullScreen() {
        if (getWindow() != null) {
            getWindow()
                    .setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public String getNombre() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sp.getString("name", "");
    }
}
