package com.arr.simple.utils.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arr.simple.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class M3BottomSheetList extends BottomSheetDialog {

    private Context mContext;
    private int resourceArray;
    private CharSequence[] stringArray;
    private RecyclerView list;
    private String title, message;
    private OnItemClickListener itemClickListener;

    public M3BottomSheetList(Context context, int resourceArray, String title, String message) {
        super(context);
        mContext = context;
        this.resourceArray = resourceArray;
        this.message = message;
        this.title = title;
        init();
    }

    private void init() {
        View content =
                LayoutInflater.from(mContext).inflate(R.layout.layout_list_bottom_sheet, null);
        setContentView(content);

        // title
        TextView textTitle = content.findViewById(R.id.title);
        textTitle.setText(title);

        // message
        TextView textMessage = content.findViewById(R.id.text_message);
        textMessage.setText(message);

        // listview to bottomsheets
        list = content.findViewById(R.id.listView);
        list.setLayoutManager(new LinearLayoutManager(mContext));
        List<String> array = new ArrayList<>(Arrays.asList(getArrayList()));
        ListAdapter adapter =
                new ListAdapter(
                        mContext,
                        array,
                        position -> {
                            handleItemClick(position);
                        });
        list.setAdapter(adapter);
    }

    private String[] getArrayList() {
        if (resourceArray != 0) {
            return mContext.getResources().getStringArray(resourceArray);
        } else {
            return new String[0];
        }
    }

    public M3BottomSheetList setItemClick(OnItemClickListener listener) {
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

    public static class Builder {
        private Context context;
        private int resourceArray;
        private String title, message;
        
        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setArrayList(int resourceArray) {
            this.resourceArray = resourceArray;
            return this;
        }

        public M3BottomSheetList build() {
            return new M3BottomSheetList(context, resourceArray, title, message);
        }
    }

    class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewList> {

        private Context context;
        private List<String> list;
        private M3BottomSheetList.OnItemClickListener onclick;

        public ListAdapter(
                Context context, List<String> list, M3BottomSheetList.OnItemClickListener onclick) {
            this.context = context;
            this.list = list;
            this.onclick = onclick;
        }

        @Override
        public ViewList onCreateViewHolder(ViewGroup parent, int arg1) {
            View view =
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_bsheet_list, parent, false);
            return new ViewList(view);
        }

        @Override
        public void onBindViewHolder(ViewList holder, int position) {
            String item = list.get(position);
            holder.title.setText(item);
            holder.itemView.setOnClickListener(
                    view -> {
                        onclick.onClick(position);
                    });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewList extends RecyclerView.ViewHolder {

            private TextView title;

            public ViewList(View viewType) {
                super(viewType);
                this.title = viewType.findViewById(R.id.itemTextView);
            }
        }
    }
}
