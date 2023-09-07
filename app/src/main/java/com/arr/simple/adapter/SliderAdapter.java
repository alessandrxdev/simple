package com.arr.simple.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.arr.simple.R;
import com.arr.simple.model.ItemsSlider;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends PagerAdapter {

    private final Context mContext;
    private List<ItemsSlider> content;

    public SliderAdapter(Context context) {
        this.mContext = context;
        this.content = new ArrayList<>();
    }

    public void setContent(List<ItemsSlider> contentList) {
        this.content = contentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.slider_item_bonos, container, false);
        ItemsSlider item = content.get(position);

        // contenido
        ((ImageView) view.findViewById(R.id.icon)).setImageResource(item.getImage());
        ((TextView) view.findViewById(R.id.tipo)).setText(item.getTitle());
        ((TextView) view.findViewById(R.id.bono)).setText(item.getBono());
        ((TextView) view.findViewById(R.id.vence)).setText(item.getFecha());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
        return arg0 == arg1;
    }
}
