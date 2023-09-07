package com.arr.simple.ui.nauta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.arr.simple.R;
import androidx.viewpager2.widget.ViewPager2;
import com.arr.simple.adapter.TabAdapter;
import com.arr.simple.databinding.FragmentComprasBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class NautaFragment extends Fragment {

    private FragmentComprasBinding binding;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @NonNull ViewGroup parent,
            Bundle savedInstanceState) {
        binding = FragmentComprasBinding.inflate(inflater, parent, false);

        TabAdapter adapter = new TabAdapter(getChildFragmentManager(), getLifecycle());
        adapter.addFragment(new LoginFragment());
        adapter.addFragment(new PortalFragment());

        binding.viewPage.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewPage.setAdapter(adapter);
        TabLayoutMediator mediator =
                new TabLayoutMediator(
                        binding.tablayout,
                        binding.viewPage,
                        (tab, position) -> {
                            switch (position) {
                                case 0:
                                    tab.setText(getString(R.string.nauta_login));
                                    break;
                                case 1:
                                    tab.setText(getString(R.string.nauta_portal));
                                    break;
                            }
                        });
        mediator.attach();

        return binding.getRoot();
    }
}
