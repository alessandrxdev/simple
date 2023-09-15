package com.arr.simple.ui.compras;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.arr.simple.adapter.ViewAdapter;
import com.arr.simple.databinding.FragmentPaquetesBinding;
import com.arr.simple.model.Grid;
import com.arr.simple.model.Header;
import com.arr.simple.model.Items;
import com.arr.simple.utils.Dialog.BSheetPaquetes;

import com.arr.ussd.Call;
import java.util.ArrayList;

public class PlanesFragment extends Fragment {

    private FragmentPaquetesBinding binding;
    private ViewAdapter adapter;
    private ArrayList<Items> list = new ArrayList<>();
    private SharedPreferences sp;
    private String SIM;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPaquetesBinding.inflate(inflater, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SIM = sp.getString("sim", "0");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: RecyclerView and Adapter
        adapter =
                new ViewAdapter(
                        getActivity(),
                        list,
                        position -> {
                            onClick(position);
                        });

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

        // TODO: list de items
        list.add(new Header("Consulta"));
        list.add(new Grid("SMS", "Consultar"));
        list.add(new Grid("VOZ", "Consultar"));
        list.add(new Header("Minutos"));
        list.add(new Grid("5", "45.50 CUP / 30 días"));
        list.add(new Grid("10", "72.50 CUP / 30 días"));
        list.add(new Grid("15", "105 CUP / 30 días"));
        list.add(new Grid("25", "162.50 CUP / 30 días"));
        list.add(new Grid("40", "250 CUP / 30 días"));
        list.add(new Header("Mensajes"));
        list.add(new Grid("20", "15 CUP / 30 días"));
        list.add(new Grid("50", "30 CUP / 30 días"));
        list.add(new Grid("90", "50 CUP / 30 días"));
        list.add(new Grid("120", "60 CUP / 30 días"));
    }

    // TODO: Manejar los onclick
    private void onClick(int position) {
        switch (position) {
            case 1:
                new Call(getActivity()).code("*222*767" + Uri.encode("#"), SIM);
                break;
            case 2:
                new Call(getActivity()).code("*222*869" + Uri.encode("#"), SIM);
                break;
            case 4:
                five_minutos();
                break;
            case 5:
                ten_minutos();
                break;
            case 6:
                fifteen_minutos();
                break;
            case 7:
                twenty_five_minutos();
                break;
            case 8:
                forty_minutos();
                break;
            case 10:
                mensajes_1();
                break;
            case 11:
                mensajes_2();
                break;
            case 12:
                mensajes_3();
                break;
            case 13:
                mensajes_4();
                break;
        }
    }

    // TODO: BottomSheet con los paquetes de Minutos
    private void five_minutos() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle("Minutos")
                .setPrecio("35.50 CUP")
                .setMinutos(
                        "Paquete de 5 minutos nacionales para realizar llamadas hacia la red móvil/fija y desde la red fija.")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*3*1" + confirm + Uri.encode("#"), SIM);
                        })
                .setNegativeButtom(null)
                .show();
    }

    private void ten_minutos() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle("Minutos")
                .setPrecio("72.50 CUP")
                .setMinutos(
                        "Paquete de 10 minutos nacionales para realizar llamadas hacia la red móvil/fija y desde la red fija.")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*3*2" + confirm + Uri.encode("#"), SIM);
                        })
                .setNegativeButtom(null)
                .show();
    }

    private void fifteen_minutos() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle("Minutos")
                .setPrecio("105 CUP")
                .setMinutos(
                        "Paquete de 15 minutos nacionales para realizar llamadas hacia la red móvil/fija y desde la red fija.")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*3*3" + confirm + Uri.encode("#"), SIM);
                        })
                .setNegativeButtom(null)
                .show();
    }

    private void twenty_five_minutos() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle("Minutos")
                .setPrecio("162.50 CUP")
                .setMinutos(
                        "Paquete de 25 minutos nacionales para realizar llamadas hacia la red móvil/fija y desde la red fija.")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*3*4" + confirm + Uri.encode("#"), SIM);
                        })
                .setNegativeButtom(null)
                .show();
    }

    private void forty_minutos() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle("Minutos")
                .setPrecio("250 CUP")
                .setMinutos(
                        "Paquete de 40 minutos nacionales para realizar llamadas hacia la red móvil/fija y desde la red fija.")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*3*5" + confirm + Uri.encode("#"), SIM);
                        })
                .setNegativeButtom(null)
                .show();
    }

    // TODO: BottomSheet con los paquetes de Mensajes
    private void mensajes_1() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle("Mensajes")
                .setPrecio("15.00 CUP")
                .setMensajes(
                        "Obtienes 20 mensajes para la red nacional, no incluye mensajes de entuMovil")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*2*1" + confirm + Uri.encode("#"), SIM);
                        })
                .setNegativeButtom(null)
                .show();
    }

    private void mensajes_2() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle("Mensajes")
                .setPrecio("30.00 CUP")
                .setMensajes(
                        "Obtienes 50 mensajes para la red nacional, no incluye mensajes de entuMovil")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*2*2" + confirm + Uri.encode("#"), SIM);
                        })
                .setNegativeButtom(null)
                .show();
    }

    private void mensajes_3() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle("Mensajes")
                .setPrecio("50.00 CUP")
                .setMensajes(
                        "Obtienes 90 mensajes para la red nacional, no incluye mensajes de entuMovil")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*2*3" + confirm + Uri.encode("#"), SIM);
                        })
                .setNegativeButtom(null)
                .show();
    }

    private void mensajes_4() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle("Mensajes")
                .setPrecio("60.00 CUP")
                .setMensajes(
                        "Obtienes 120 mensajes para la red nacional, no incluye mensajes de entuMovil")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*2*4" + confirm + Uri.encode("#"), SIM);
                        })
                .setNegativeButtom(null)
                .show();
    }
}
