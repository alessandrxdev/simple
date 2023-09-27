package com.arr.simple.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arr.simple.databinding.LayoutAboutListItemsBinding;
import com.arr.simple.databinding.LayoutHeaderViewBinding;
import com.arr.simple.model.About;
import com.arr.simple.model.Header;
import com.arr.simple.model.Items;
import java.util.List;

public class AboutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Items> list;

    public AboutAdapter(Context context, List<Items> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == About.VIEW_ABOUT) {
            LayoutAboutListItemsBinding binding =
                    LayoutAboutListItemsBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false);
            return new vAbout(binding);

        } else if (viewType == Header.VIEW_HEADER) {
            LayoutHeaderViewBinding binding =
                    LayoutHeaderViewBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false);
            return new vHeader(binding);
        }
        throw new RuntimeException("RuntimeException");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof vAbout) {
            About gridModel = (About) list.get(position);
            vAbout about = (vAbout) holder;
            about.binding.imageIcon.setImageResource(gridModel.getIcon());
            about.binding.textList.setText(gridModel.getName());
            about.binding.textDescrip.setText(gridModel.getDescription());

        } else if (holder instanceof vHeader) {
            Header headerModel = (Header) list.get(position);
            vHeader header = (vHeader) holder;
            header.binding.header.setText(headerModel.getHeader());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class vAbout extends RecyclerView.ViewHolder {

        private final LayoutAboutListItemsBinding binding;

        public vAbout(LayoutAboutListItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class vHeader extends RecyclerView.ViewHolder {

        private final LayoutHeaderViewBinding binding;

        public vHeader(LayoutHeaderViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getViewType();
    }
}
