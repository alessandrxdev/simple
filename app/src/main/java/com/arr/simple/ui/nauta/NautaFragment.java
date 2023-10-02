package com.arr.simple.ui.nauta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.arr.simple.adapter.TabAdapter;
import com.arr.simple.R;
import com.arr.simple.databinding.FragmentNautaBinding;
import com.arr.simple.ui.nauta.login.LoginFragment;
import com.arr.simple.ui.nauta.portal.PortalFragment;
import com.google.android.material.tabs.TabLayoutMediator;

public class NautaFragment extends Fragment {

    private FragmentNautaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle arg2) {
        binding = FragmentNautaBinding.inflate(inflater, parent, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle arg1) {
        super.onViewCreated(view, arg1);

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
