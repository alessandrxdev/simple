package com.arr.simple.preferences;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.arr.simple.R;
import androidx.preference.MultiSelectListPreference;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.HashSet;
import java.util.Set;

public class M3MultiSelectPreference extends MultiSelectListPreference {

    private Set<String> values = new HashSet();

    public M3MultiSelectPreference(Context context, AttributeSet attr) {
        super(context, attr);
        setLayoutResource(R.layout.layout_preference_m3);
    }

    @Override
    protected void onClick() {
        final CharSequence[] entries = getEntries();
        final CharSequence[] entryValues = getEntryValues();
        final boolean[] checkedItems = getSelectedItems();
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(getDialogTitle())
                .setMultiChoiceItems(
                        entries,
                        checkedItems,
                        (dlg, which, isChecked) -> {
                            if (isChecked) values.add(entryValues[which].toString());
                            else values.remove(entryValues[which].toString());
                            if (callChangeListener(values)) {
                                setValues(values);
                            }
                        })
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        // title preference
        TextView title = (TextView) holder.findViewById(R.id.title);
        if (getTitle() != null) {
            title.setText(getTitle());
        } else {
            title.setVisibility(View.GONE);
        }

        // summary preference
        TextView summary = (TextView) holder.findViewById(R.id.summary);
        if (getSummary() != null) {
            summary.setText(getSummary());
        } else {
            summary.setVisibility(View.GONE);
        }

        // icon preference
        ImageView icon = (ImageView) holder.findViewById(R.id.icon);
        if (getIcon() != null) {
            icon.setImageDrawable(getIcon());
        } else {
            icon.setVisibility(View.GONE);
        }
    }
}
