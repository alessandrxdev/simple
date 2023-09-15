package com.arr.simple.ui.compras;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.arr.simple.R;
import com.arr.simple.adapter.ViewAdapter;
import com.arr.simple.databinding.FragmentPaquetesBinding;
import com.arr.simple.model.Grid;
import com.arr.simple.model.Header;
import com.arr.simple.model.Items;
import com.arr.simple.utils.Dialog.BSheetEditText;

import com.arr.ussd.Call;
import java.util.ArrayList;

public class AmigoFragment extends Fragment {

    private FragmentPaquetesBinding binding;
    private ViewAdapter adapter;
    private final ArrayList<Items> list = new ArrayList<>();
    private SharedPreferences sp;
    private String SIM;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPaquetesBinding.inflate(inflater, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SIM = sp.getString("sim", "0");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: RecyclerView and Adapter
        adapter = new ViewAdapter(getActivity(), list, this::onClick);

        binding.recyclerView.setAdapter(adapter);
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

        // TODO: lista de items
        list.add(new Header("Consulta"));
        list.add(new Grid("Estado", "Plan Amigo"));
        list.add(new Grid("Lista", "Lista de números"));
        list.add(new Header("Opciones"));
        list.add(new Grid("Activar", "Activar servicio"));
        list.add(new Grid("Desactivar", "Desactivar servicio"));
        list.add(new Grid("Agregar", "Agregar número"));
        list.add(new Grid("Eliminar", "Eliminar número"));
    }

    // TODO: Manejar los onclick
    private void onClick(int position) {
        switch (position) {
            case 1:
                new Call(getActivity()).code("*222*264" + Uri.encode("#"), SIM);
                break;
            case 2:
                new Call(getActivity()).code("*133*4*3*" + Uri.encode("#"), SIM);
                break;
            case 4:
                activate_amigo();
                break;
            case 5:
                desactivate_amigo();
                break;
            case 6:
                agregar_numero();
                break;
            case 7:
                eliminar_numero();
                break;
        }
    }

    private void activate_amigo() {
        BSheetEditText bSheet = new BSheetEditText(getActivity());
        bSheet.setTitle(getString(R.string.title_activar_amigo));
        bSheet.setPrecio(getString(R.string.precio_activar_amigo));
        bSheet.setMessage(getString(R.string.message_activar_amigo));
        bSheet.setPositiveButtom(
                view -> {
                    boolean isConfirm = !sp.getBoolean("confirma", false);
                    String confirm = isConfirm ? "" : "*1";
                    new Call(getActivity()).code("*133*4*1*1" + confirm + Uri.encode("#"), SIM);
                });
        bSheet.setNegativeButtom(null);
        bSheet.setNavColorSurface(true);
        bSheet.show();
    }

    private void desactivate_amigo() {
        BSheetEditText bSheet = new BSheetEditText(getActivity());
        bSheet.setTitle(getString(R.string.title_disable_amigo));
        bSheet.setMessage(getString(R.string.message_disable_amigo));
        bSheet.setPositiveButtom(
                view -> {
                    boolean isConfirm = !sp.getBoolean("confirma", false);
                    String confirm = isConfirm ? "" : "*1";
                    new Call(getActivity()).code("*133*4*1*2" + confirm + Uri.encode("#"), SIM);
                });
        bSheet.setNegativeButtom(null);
        bSheet.setNavColorSurface(true);
        bSheet.show();
    }

    private void agregar_numero() {
        BSheetEditText bSheet = new BSheetEditText(getActivity());
        bSheet.setTitle(getString(R.string.title_add_amigo));
        bSheet.setPrecio(getString(R.string.precio_add_amigo));
        bSheet.setMessage(getString(R.string.message_add_amigo));
        bSheet.setEnabledEditText(true);
        bSheet.setBehaviorExpanded(true);
        bSheet.setHint(getString(R.string.hint_numero));
        bSheet.setInputType("number");
        bSheet.setMaxLenght(8);
        bSheet.setPositiveButtom(
                view -> {
                    String numero = bSheet.getEditTextValue();
                    new Call(getActivity()).code("*133*4*2*1" + numero + Uri.encode("#"), SIM);
                });
        bSheet.setNegativeButtom(null);
        bSheet.setNavColorSurface(true);
        bSheet.show();
    }

    private void eliminar_numero() {
        BSheetEditText bSheet = new BSheetEditText(getActivity());
        bSheet.setTitle(getString(R.string.title_delete_amigo));
        bSheet.setMessage(getString(R.string.message_delete_amigo));
        bSheet.setEnabledEditText(true);
        bSheet.setBehaviorExpanded(true);
        bSheet.setHint(getString(R.string.hint_numero));
        bSheet.setInputType("number");
        bSheet.setMaxLenght(8);
        bSheet.setPositiveButtom(
                view -> {
                    String numero = bSheet.getEditTextValue();
                    new Call(getActivity()).code("*133*4*2*2" + numero + Uri.encode("#"), SIM);
                });
        bSheet.setNegativeButtom(null);
        bSheet.setNavColorSurface(true);
        bSheet.show();
    }
}
