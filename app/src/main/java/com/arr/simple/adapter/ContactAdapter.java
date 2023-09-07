package com.arr.simple.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arr.simple.databinding.LayoutHeaderViewBinding;
import com.arr.simple.databinding.LayoutSingleContactViewBinding;
import com.arr.simple.model.Contact;
import com.arr.simple.model.Header;
import com.arr.simple.model.Items;
import com.bumptech.glide.Glide;
import java.util.List;
import com.arr.simple.R;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<Items> list;
    private OnItemClickListener onItemClickListener, onItemClickRevertido;

    public ContactAdapter(
            Context mContext,
            List<Items> list,
            OnItemClickListener onItemClickListener,
            OnItemClickListener onItemClickRevertido) {
        this.mContext = mContext;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
        this.onItemClickRevertido = onItemClickRevertido;
    }

    public void setContactList(List<Items> item) {
        this.list = item;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Header.VIEW_HEADER) {
            LayoutHeaderViewBinding binding =
                    LayoutHeaderViewBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false);
            return new HeaderVH(binding);

        } else if (viewType == Contact.VIEW_GRID) {
            LayoutSingleContactViewBinding binding =
                    LayoutSingleContactViewBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false);
            return new VHContact(binding);
        }
        throw new RuntimeException("Exeption");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderVH) {
            Header model = (Header) list.get(position);
            HeaderVH view = (HeaderVH) holder;

            view.binding.header.setText(model.getHeader());

        } else if (holder instanceof VHContact) {
            Contact model = (Contact) list.get(position);
            VHContact view = (VHContact) holder;

            // set
            view.binding.name.setText(model.getName());
            view.binding.number.setText(model.getNumber());
            if (model.getPhoto() != null) {
                Glide.with(mContext).load(model.getPhoto()).circleCrop().into(view.binding.perfil);
            } else {
                view.binding.perfil.setImageResource(R.drawable.ic_contacts_40px);
            }
            view.binding.llamadaPrivada.setOnClickListener(
                    v -> {
                        onItemClickListener.onItemClick(model);
                    });

            view.binding.llamadaRevertida.setOnClickListener(
                    v -> {
                        onItemClickRevertido.onItemClick(model);
                    });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getViewType();
    }

    public interface OnItemClickListener {
        void onItemClick(Contact position);
    }

    public static class VHContact extends RecyclerView.ViewHolder {

        private LayoutSingleContactViewBinding binding;

        public VHContact(@NonNull LayoutSingleContactViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class HeaderVH extends RecyclerView.ViewHolder {

        private LayoutHeaderViewBinding binding;

        public HeaderVH(@NonNull LayoutHeaderViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
