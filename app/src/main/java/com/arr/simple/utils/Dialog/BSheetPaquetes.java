package com.arr.simple.utils.Dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arr.simple.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.elevation.SurfaceColors;

import java.util.Objects;

public class BSheetPaquetes extends BottomSheetDialog {

    private TextView title, precio, allNetwork, lte, nacional, minutos, sms, vencimiento;
    private Button positive, negative;

    public BSheetPaquetes(Context context) {
        super(context);
        init();
    }

    private void init() {
        @SuppressLint("InflateParams") View content = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_bottom_sheet_paquetes, null);
        setContentView(content);

        title = content.findViewById(R.id.title);
        precio = content.findViewById(R.id.precio);
        allNetwork = content.findViewById(R.id.text_all_redes);
        lte = content.findViewById(R.id.text_lte);
        nacional = content.findViewById(R.id.text_nacional);
        minutos = content.findViewById(R.id.text_minutos);
        sms = content.findViewById(R.id.text_mensajes);
        vencimiento = content.findViewById(R.id.text_vence);

        positive = content.findViewById(R.id.positive_buttom);
        negative = content.findViewById(R.id.negative_buttom);

        // ocultar el contenido por defecto
        title.setVisibility(View.GONE);
        precio.setVisibility(View.GONE);
        allNetwork.setVisibility(View.GONE);
        lte.setVisibility(View.GONE);
        nacional.setVisibility(View.GONE);
        minutos.setVisibility(View.GONE);
        sms.setVisibility(View.GONE);
        vencimiento.setVisibility(View.GONE);
    }

    public BSheetPaquetes setNavColorSurface(boolean isSurface) {
        if (isSurface) {
            Objects.requireNonNull(getWindow()).setNavigationBarColor(SurfaceColors.SURFACE_1.getColor(getContext()));
        }
        return this;
    }

    public BSheetPaquetes setTitle(String string) {
        if (string != null && !string.isEmpty()) {
            title.setText(string);
            title.setVisibility(View.VISIBLE);
        } else {
            title.setVisibility(View.GONE);
        }
        return this;
    }

    public BSheetPaquetes setPrecio(String string) {
        if (string != null && !string.isEmpty()) {
            precio.setText(string);
            precio.setVisibility(View.VISIBLE);
        } else {
            precio.setVisibility(View.GONE);
        }
        return this;
    }

    public BSheetPaquetes setAllData(String string) {
        if (string != null && !string.isEmpty()) {
            allNetwork.setText(string);
            allNetwork.setVisibility(View.VISIBLE);
        } else {
            allNetwork.setVisibility(View.GONE);
        }
        return this;
    }

    public BSheetPaquetes setLTE(String string) {
        if (string != null && !string.isEmpty()) {
            lte.setText(string);
            lte.setVisibility(View.VISIBLE);
        } else {
            lte.setVisibility(View.GONE);
        }
        return this;
    }

    public BSheetPaquetes setNacional(String string) {
        if (string != null && !string.isEmpty()) {
            nacional.setText(string);
            nacional.setVisibility(View.VISIBLE);
        } else {
            nacional.setVisibility(View.GONE);
        }
        return this;
    }

    public BSheetPaquetes setMinutos(String string) {
        if (string != null && !string.isEmpty()) {
            minutos.setText(string);
            minutos.setVisibility(View.VISIBLE);
        } else {
            minutos.setVisibility(View.GONE);
        }
        return this;
    }

    public BSheetPaquetes setMensajes(String string) {
        if (string != null && !string.isEmpty()) {
            sms.setText(string);
            sms.setVisibility(View.VISIBLE);
        } else {
            sms.setVisibility(View.GONE);
        }
        return this;
    }

    public BSheetPaquetes setVencimiento(String string) {
        if (string != null && !string.isEmpty()) {
            vencimiento.setText(string);
            vencimiento.setVisibility(View.VISIBLE);
        } else {
            vencimiento.setVisibility(View.GONE);
        }
        return this;
    }

    public BSheetPaquetes setPositiveButtom(final View.OnClickListener listener) {
        positive.setOnClickListener(
                v -> {
                    if (listener != null) {
                        listener.onClick(v);
                    }
                    dismiss();
                });
        return this;
    }

    public BSheetPaquetes setNegativeButtom(View.OnClickListener listener) {
        negative.setOnClickListener(
                v -> {
                    if (listener != null) {
                        listener.onClick(v);
                    }
                    dismiss();
                });
        return this;
    }
}
