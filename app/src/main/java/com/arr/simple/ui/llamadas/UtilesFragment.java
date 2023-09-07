package com.arr.simple.ui.llamadas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import com.arr.simple.adapter.ViewAdapter;
import com.arr.simple.databinding.FragmentPaquetesBinding;
import com.arr.simple.model.Grid;
import com.arr.simple.model.Header;
import com.arr.simple.model.Items;
import com.arr.ussd.Call;
import java.util.ArrayList;

public class UtilesFragment extends Fragment {

    private FragmentPaquetesBinding binding;
    private ViewAdapter adapter;
    private ArrayList<Items> list = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentPaquetesBinding.inflate(inflater, container, false);

        // TODO: Adapter in recyclerView
        binding.recyclerView.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setSpanSizeLookup(
                new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int viewType = adapter.getItemViewType(position);
                        if (viewType == Header.VIEW_HEADER) {
                            return 2;
                        } else {
                            return 1;
                        }
                    }
                });
        binding.recyclerView.setLayoutManager(manager);
        adapter =
                new ViewAdapter(
                        getActivity(),
                        list,
                        position -> {
                            // onclick
                            onClick(position);
                        });
        binding.recyclerView.setAdapter(adapter);

        // list
        list.add(new Header("Emergencia"));
        list.add(new Grid("103", "Antidrogas"));
        list.add(new Grid("104", "Ambulancia"));
        list.add(new Grid("105", "Bomberos"));
        list.add(new Grid("106", "Policía"));
        list.add(new Grid("107", "Resc. Salvamento Marítimo"));
        list.add(new Header("Útiles"));
        list.add(new Grid("Operadora", "Atención al cliente"));
        list.add(new Grid("Nauta", "Operadora Nauta Hogar"));
        list.add(new Grid("Reportes", "Reporte de telefonía fija"));
        list.add(new Grid("Emp. Eléctrica", "Atención al cliente"));
        list.add(new Grid("Telefonía Móvil", "Quejas y Reclamos"));

        return binding.getRoot();
    }

    private void onClick(int position) {
        switch (position) {
            case 1:
                new Call(getActivity()).code("103", "0");
                break;
            case 2:
                new Call(getActivity()).code("104", "0");
                break;
            case 3:
                new Call(getActivity()).code("105", "0");
                break;
            case 4:
                new Call(getActivity()).code("106", "0");
                break;
            case 5:
                new Call(getActivity()).code("107", "0");
                break;
            case 7:
                new Call(getActivity()).code("52642266", "0");
                break;
            case 8:
                new Call(getActivity()).code("80043434", "0");
                break;
            case 9:
                new Call(getActivity()).code("114", "0");
                break;
            case 10:
                new Call(getActivity()).code("18888", "0");
                break;
            case 11:
                new Call(getActivity()).code("118", "0");
                break;
        }
    }
}
