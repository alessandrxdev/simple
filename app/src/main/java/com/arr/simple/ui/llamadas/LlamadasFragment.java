package com.arr.simple.ui.llamadas;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;
import com.arr.simple.adapter.TabAdapter;
import com.arr.simple.databinding.FragmentComprasBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class LlamadasFragment extends Fragment {

    private FragmentComprasBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentComprasBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabAdapter adapter = new TabAdapter(getChildFragmentManager(), getLifecycle());
        adapter.addFragment(new LlamarFragment());
        adapter.addFragment(new UtilesFragment());

        binding.viewPage.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewPage.setAdapter(adapter);
        TabLayoutMediator mediator =
                new TabLayoutMediator(
                        binding.tablayout,
                        binding.viewPage,
                        (tab, position) -> {
                            switch (position) {
                                case 0:
                                    tab.setText("Llamar");
                                    break;
                                case 1:
                                    tab.setText("Útiles");
                                    break;
                            }
                        });
        mediator.attach();
    }
}
