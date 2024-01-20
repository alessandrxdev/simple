package com.arr.simple.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.arr.simple.databinding.LayoutViewItemsBonosBalanceBinding;
import com.arr.simple.model.Balances;
import java.util.List;

public class BalancesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Balances> list;

    public BalancesAdapter(Context context, List<Balances> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof Bonos) {
            Balances model = list.get(position);
            Bonos view = (Bonos) holder;

            view.binding.icon.setImageResource(model.getIcon());
            view.binding.typeBono.setText(model.getTitle());
            view.binding.expire.setText(model.getVence());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutViewItemsBonosBalanceBinding view =
                LayoutViewItemsBonosBalanceBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new Bonos(view);
    }

    class Bonos extends RecyclerView.ViewHolder {

        private LayoutViewItemsBonosBalanceBinding binding;

        public Bonos(LayoutViewItemsBonosBalanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
