package com.arr.simple.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arr.simple.databinding.LayoutGridViewBinding;
import com.arr.simple.databinding.LayoutHeaderViewBinding;
import com.arr.simple.model.Grid;
import com.arr.simple.model.Header;
import com.arr.simple.model.Items;

import java.util.ArrayList;

public class ViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<Items> list;
    public final Context context;
    private final OnItemClickListener onItemClickListener;

    public ViewAdapter(
            Context context, ArrayList<Items> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Grid.VIEW_GRID) {
            LayoutGridViewBinding binding =
                    LayoutGridViewBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false);
            return new VGrid(binding);
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
        if (holder instanceof VGrid) {
            Grid gridModel = (Grid) list.get(position);
            VGrid grid = (VGrid) holder;
            grid.binding.title.setText(gridModel.getTitle());
            grid.binding.subtitle.setText(gridModel.getSubtitle());
            if(gridModel.getIcon() == 0){
                grid.binding.icon.setVisibility(View.GONE);
            }else{
                grid.binding.icon.setImageResource(gridModel.getIcon());
            }

            // TODO: onClick item position
            grid.binding.card.setOnClickListener(v -> onItemClickListener.onItemClick(position));
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    static class VGrid extends RecyclerView.ViewHolder {

        private final LayoutGridViewBinding binding;

        public VGrid(LayoutGridViewBinding binding) {
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
