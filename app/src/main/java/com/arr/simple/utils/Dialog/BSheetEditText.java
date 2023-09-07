package com.arr.simple.utils.Dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.arr.simple.R;
import com.arr.simple.utils.Dialog.BSheetEditText;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;

public class BSheetEditText extends BottomSheetDialog {

    private Button positive, negative;
    private TextView title, precio, message;
    private String hint;
    private TextInputEditText editText;
    private TextInputLayout inputLayout;
    private BottomSheetBehavior<View> behavior;

    public BSheetEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        // TODO: Inflate layout R.layout.layout_bottom_sheet_edit_text
        @SuppressLint("InflateParams")
        View content =
                LayoutInflater.from(getContext())
                        .inflate(R.layout.layout_bottom_sheet_edit_text, null);
        setContentView(content);

        // TODO: resources
        title = content.findViewById(R.id.title);
        precio = content.findViewById(R.id.precio);
        message = content.findViewById(R.id.text_message);
        editText = content.findViewById(R.id.edit_text);
        inputLayout = content.findViewById(R.id.input_layout);
        positive = content.findViewById(R.id.positive_buttom);
        negative = content.findViewById(R.id.negative_buttom);

        // TODO: Visibility GONE
        precio.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        inputLayout.setVisibility(View.GONE);
    }

    public BSheetEditText setNavColorSurface(boolean isSurface) {
        if (isSurface) {
            Objects.requireNonNull(getWindow())
                    .setNavigationBarColor(SurfaceColors.SURFACE_1.getColor(getContext()));
        }
        return this;
    }

    public void setBehaviorExpanded(boolean expanded) {
        if (expanded) {
            View bsheet =
                    getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            behavior = BottomSheetBehavior.from(bsheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public BSheetEditText setTitle(String string) {
        if (string != null) {
            title.setText(string);
        }
        return this;
    }

    public BSheetEditText setPrecio(String string) {
        if (string != null) {
            precio.setText(string);
            precio.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public BSheetEditText setMessage(String string) {
        if (string != null) {
            message.setText(string);
            message.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public BSheetEditText setHint(String hint) {
        if (hint != null) {
            inputLayout.setHint(hint);
        }
        return this;
    }

    public BSheetEditText setInputType(String inputType) {
        if (inputType != null) {
            int inputTypeValue = InputType.TYPE_CLASS_TEXT;
            switch (inputType) {
                case "number":
                    inputTypeValue = InputType.TYPE_CLASS_NUMBER;
                    break;
                case "text":
                    inputTypeValue = InputType.TYPE_CLASS_TEXT;
                    break;
            }
            editText.setInputType(inputTypeValue);
        }

        return this;
    }

    public BSheetEditText setMaxLenght(int max) {
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(max)});
        return this;
    }

    public BSheetEditText setEnabledEditText(boolean edit) {
        if (edit) {
            inputLayout.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public String getEditTextValue() {
        if (editText != null) {
            return editText.getText().toString();
        }
        return "";
    }

    public BSheetEditText setPositiveButtom(final View.OnClickListener listener) {
        positive.setOnClickListener(
                v -> {
                    if (listener != null) {
                        listener.onClick(v);
                    }
                    dismiss();
                });
        return this;
    }

    public BSheetEditText setNegativeButtom(View.OnClickListener listener) {
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
