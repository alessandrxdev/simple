package com.arr.simple.utils.Dialog;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.arr.simple.R;
import com.arr.simple.utils.profile.ImageUtils;
import com.bumptech.glide.Glide;

public class FullScreenDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        View view = inflater.inflate(R.layout.layout_view_photo_profile, container, false);

        ImageView image = view.findViewById(R.id.picture);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(getNombre());
        ImageView close = view.findViewById(R.id.close);
        close.setOnClickListener(
                v -> {
                    dismiss();
                });
        // load profile photo
        Bitmap bitmap = new ImageUtils(requireContext()).getSavedImage();
        if (bitmap != null) {
            Glide.with(requireContext())
            .load(bitmap)
            .into(image);
        }
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle arg0) {
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    public String getNombre() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sp.getString("name", "");
    }
}
