package com.arr.simple.ui.llamadas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.arr.simple.adapter.ViewAdapter;
import com.arr.simple.databinding.FragmentLlamarBinding;
import com.arr.simple.databinding.FragmentPaquetesBinding;
import com.arr.simple.model.Items;
import java.util.ArrayList;

public class LlamarFragment extends Fragment {
    
    private FragmentLlamarBinding binding;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentLlamarBinding.inflate(inflater, container, false);
        
        
        
        return binding.getRoot();
    }
}
