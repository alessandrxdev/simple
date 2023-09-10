package com.arr.simple.ui.servicios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.arr.simple.adapter.TabAdapter;
import com.arr.simple.databinding.FragmentComprasBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class SmsFragment extends Fragment {

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
        adapter.addFragment(new FreeSmsFragment());
        adapter.addFragment(new EnTuMovilFragment());

        binding.viewPage.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewPage.setAdapter(adapter);
        TabLayoutMediator mediator =
                new TabLayoutMediator(
                        binding.tablayout,
                        binding.viewPage,
                        (tab, position) -> {
                            switch (position) {
                                case 0:
                                    tab.setText("SMS");
                                    break;
                                case 1:
                                    tab.setText("entuMóvil");
                                    break;
                            }
                        });
        mediator.attach();
    }
}
