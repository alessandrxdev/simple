package com.arr.simple.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.ArrayList;

public class TabAdapter extends FragmentStateAdapter {

    private final ArrayList<Fragment> fragmentList = new ArrayList<>();

    public TabAdapter(@NonNull FragmentManager manager, @NonNull Lifecycle lifecycle) {
        super(manager, lifecycle);
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    @NonNull
    @Override
    public androidx.fragment.app.Fragment createFragment(int arg0) {
        return fragmentList.get(arg0);
    }
}
