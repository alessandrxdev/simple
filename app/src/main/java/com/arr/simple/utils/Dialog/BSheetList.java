package com.arr.simple.utils.Dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arr.simple.R;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.elevation.SurfaceColors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class BSheetList extends BottomSheetDialog {

    private Button positive, negative;
    private TextView title, message;
    private CharSequence[] array;
    private int resource;
    private BottomSheetBehavior<View> behavior;
    private RecyclerView recycler;
    private Context mContext;
    private OnItemClickListener itemClickListener;

    public BSheetList setArray(@ArrayRes int arrayRes) {
        this.resource = arrayRes;
        array = getContext().getResources().getStringArray(R.array.clima);
        return this;
    }

    public BSheetList(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        @SuppressLint("InflateParams")
        View content =
                LayoutInflater.from(getContext()).inflate(R.layout.layout_list_bottom_sheet, null);
        setContentView(content);
        Log.w("Errrorrrr", "obtener array" + resource);
        /*
                // TODO: resources
                recycler = content.findViewById(R.id.recycler_view_bsheet);
                recycler.setLayoutManager(new LinearLayoutManager(mContext));
             array = getContext().getResources().getStringArray(resource);
                ArrayList<CharSequence> list = new ArrayList<>(Arrays.asList(array));
                ListAdapter adapter =
                        new ListAdapter(
                                list,
                                position -> {
                                    handleItemClick(position);
                                });
                recycler.setAdapter(adapter);
        */
        title = content.findViewById(R.id.title);
        message = content.findViewById(R.id.text_message);
        positive = content.findViewById(R.id.positive_buttom);
        negative = content.findViewById(R.id.negative_buttom);

        // TODO: visibility
        message.setVisibility(View.GONE);
        negative.setVisibility(View.GONE);
    }

    public BSheetList setNavColorSurface(boolean isSurface) {
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

    public BSheetList setTitle(String string) {
        if (string != null && !string.isEmpty()) {
            title.setText(string);
            title.setVisibility(View.VISIBLE);
        } else {
            title.setVisibility(View.GONE);
        }
        return this;
    }

    public BSheetList setMessage(String string) {
        if (string != null) {
            message.setText(string);
            message.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public BSheetList setPositiveButtom(final View.OnClickListener listener) {
        positive.setOnClickListener(
                v -> {
                    if (listener != null) {
                        listener.onClick(v);
                    }
                    dismiss();
                });
        return this;
    }

    public BSheetList setNegativeButtom(View.OnClickListener listener) {
        negative.setOnClickListener(
                v -> {
                    if (listener != null) {
                        listener.onClick(v);
                    }
                    dismiss();
                });
        return this;
    }

    public BSheetList setItemClick(OnItemClickListener listener) {
        itemClickListener = listener;
        return this;
    }

    private void handleItemClick(int position) {
        if (itemClickListener != null) {
            itemClickListener.onClick(position);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    private class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private ArrayList<CharSequence> dataList;
        private BSheetList.OnItemClickListener itemClick;

        public ListAdapter(ArrayList<CharSequence> data, BSheetList.OnItemClickListener itemClick) {
            this.dataList = data;
            this.itemClick = itemClick;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view =
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_bsheet_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(dataList.get(position));
            holder.itemView.setOnClickListener(
                    view -> {
                        itemClick.onClick(position);
                    });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.itemTextView);
            }

            public void bindData(String data) {
                textView.setText(data);
            }
        }
    }
}
