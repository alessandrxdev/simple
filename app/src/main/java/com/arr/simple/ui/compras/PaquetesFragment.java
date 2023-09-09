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
import com.arr.simple.R;
import com.arr.simple.model.Grid;
import com.arr.simple.model.Header;
import com.arr.simple.model.Items;
import com.arr.simple.utils.Dialog.BSheetPaquetes;
import com.arr.ussd.Call;
import java.util.ArrayList;

public class PaquetesFragment extends Fragment {

    private FragmentPaquetesBinding binding;
    private ViewAdapter adapter;
    private ArrayList<Items> list = new ArrayList<>();
    private SharedPreferences sp;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPaquetesBinding.inflate(inflater, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
                        int totalSpanCount = manager.getSpanCount();
                        int itemCount = manager.getItemCount();
                        int viewType = adapter.getItemViewType(position);
                        if (viewType == Header.VIEW_HEADER) {
                            return 2;
                        } else {
                            return 1;
                        }
                    }
                });
        binding.recyclerView.setLayoutManager(manager);

        // TODO: list
        list.add(new Header("Combinados"));
        list.add(new Grid("1.4 GB", "110 CUP / 30 días"));
        list.add(new Grid("3.5 GB", "250 CUP / 30 días"));
        list.add(new Grid("8 GB", "500 CUP / 30 días"));
        list.add(new Header("LTE"));
        list.add(new Grid("1 GB", "100 CUP / 30 días"));
        list.add(new Grid("2.5 GB", "200 CUP / 30 días"));
        list.add(new Grid("16 GB", "950 CUP / 30 días"));
        list.add(new Header("Bolsas"));
        list.add(new Grid("600 MB", "25 CUP / 30 días"));
        list.add(new Grid("200 MB", "25 CUP / 24 horas"));
    }

    private void onClick(int position) {
        switch (position) {
            case 1:
                plan_basico();
                break;
            case 2:
                plan_medio();
                break;
            case 3:
                plan_extra();
                break;
            case 5:
                lte_basico();
                break;
            case 6:
                lte_medio();
                break;
            case 7:
                lte_extra();
                break;
            case 9:
                bolsa_mendajeria();
                break;
            case 10:
                bolsa_diaria();
                break;
        }
    }

    private void plan_basico() {
        new BSheetPaquetes(getActivity())
                .setTitle("Plan Básico")
                .setPrecio("110 CUP")
                .setAllData("600 MB para todas las redes")
                .setLTE("800 MB solo para la red lte")
                .setNacional("300 MB para la red nacional")
                .setMinutos("15 minutos nacionales")
                .setMensajes("20 mensajes nacionales")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*5*1" + confirm + Uri.encode("#"), "0");
                        })
                .setNegativeButtom(v -> {})
                .setNavColorSurface(true)
                .show();
    }

    private void plan_medio() {
        new BSheetPaquetes(getActivity())
                .setTitle("Plan Medio")
                .setPrecio("250 CUP")
                .setAllData("1.5 GB para todas las redes")
                .setLTE("2 GB solo para la red lte")
                .setNacional("300 MB para la red nacional")
                .setMinutos("35 minutos nacionales")
                .setMensajes("40 mensajes nacionales")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*5*2" + confirm + Uri.encode("#"), "0");
                        })
                .setNegativeButtom(v -> {})
                .setNavColorSurface(true)
                .show();
    }

    private void plan_extra() {
        new BSheetPaquetes(getActivity())
                .setTitle("Plan Extra")
                .setPrecio("500 CUP")
                .setAllData("3.5 GB para todas las redes")
                .setLTE("4.5 GB solo para la red lte")
                .setNacional("300 MB para la red nacional")
                .setMinutos("75 minutos nacionales")
                .setMensajes("80 mensajes nacionales")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*5*3" + confirm + Uri.encode("#"), "0");
                        })
                .setNegativeButtom(v -> {})
                .setNavColorSurface(true)
                .show();
    }

    private void lte_basico() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle("LTE")
                .setPrecio("100 CUP")
                .setLTE("1 GB solo para la red lte")
                .setNacional("300 MB para la red nacional")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*1*4*1" + confirm + Uri.encode("#"), "0");
                        })
                .setNegativeButtom(null)
                .show();
    }

    private void lte_medio() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle("LTE")
                .setPrecio("200 CUP")
                .setLTE("2.5 GB solo para la red lte")
                .setNacional("300 MB para la red nacional")
                .setVencimiento("30 días a partir de su uso")
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*1*4*2" + confirm + Uri.encode("#"), "0");
                        })
                .setNegativeButtom(null)
                .show();
    }

    private void lte_extra() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle(getString(R.string.paquetes))
                .setPrecio(getString(R.string.lte_extra_precio))
                .setAllData(getString(R.string.lte_extra_paquete_all))
                .setLTE(getString(R.string.lte_paquete_lte))
                .setNacional(getString(R.string.lte_extra_nacional))
                .setVencimiento(getString(R.string.lte_extra_vencimiento))
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*1*4*3" + confirm + Uri.encode("#"), "0");
                        })
                .setNegativeButtom(null)
                .show();
    }

    private void bolsa_mendajeria() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle(getString(R.string.mensajeria_title))
                .setPrecio(getString(R.string.mensajeria))
                .setNacional(getString(R.string.mensajeria_datos))
                .setVencimiento(getString(R.string.mensajeria_vencimiento))
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*1*2" + confirm + Uri.encode("#"), "0");
                        })
                .setNegativeButtom(null)
                .show();
    }

    private void bolsa_diaria() {
        new BSheetPaquetes(getActivity())
                .setNavColorSurface(true)
                .setTitle(getString(R.string.diaria_title))
                .setPrecio(getString(R.string.precio_diaria))
                .setLTE(getString(R.string.diaria_lte))
                .setVencimiento(getString(R.string.diaria_vencimiento))
                .setPositiveButtom(
                        view -> {
                            boolean isConfirm = !sp.getBoolean("confirma", false);
                            String confirm = isConfirm ? "" : "*1";
                            new Call(getActivity())
                                    .code("*133*1*3" + confirm + Uri.encode("#"), "0");
                        })
                .setNegativeButtom(null)
                .show();
    }
}
