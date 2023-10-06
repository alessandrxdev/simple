package com.arr.simple.ui.servicios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;
import com.arr.simple.adapter.TabAdapter;
import com.arr.simple.adapter.ViewAdapter;
import com.arr.simple.databinding.FragmentComprasBinding;
import com.arr.simple.databinding.FragmentServiciosBinding;
import com.arr.simple.model.Grid;
import com.arr.simple.model.Header;
import com.arr.simple.model.Items;
import com.arr.simple.R;
import com.arr.simple.utils.Dialog.BSheetEditText;
import com.arr.simple.utils.Dialog.BSheetList;
import com.arr.simple.utils.Dialog.M3BottomSheetList;
import com.arr.ussd.sms.SendSMS;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;

public class SmsFragment extends Fragment {

    private FragmentServiciosBinding binding;
    private ViewAdapter adapter;
    private ArrayList<Items> list = new ArrayList<>();
    private SendSMS sms;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServiciosBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: send sms class
        sms = new SendSMS(getContext());

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

        // lista
        list.add(new Header("Gratis"));
        list.add(new Grid("Oferta", "Ofertas móviles",0));
        list.add(new Grid("Tarifa", "Tarifas móviles",0));
        list.add(new Grid("4G", "Activar servicio 4G",0));
        list.add(new Grid("Red", "Redes que soporta su móvil", 0));
        list.add(new Header("De pago"));
        list.add(new Grid("DHL", "Rastree su paquete",0));
        list.add(new Grid("Clima", "Estado del tiempo",0));
        list.add(new Grid("Vuelos", "Información de vuelos",0));
        list.add(new Grid("Embajadas", "Embajadas en Cuba",0));
        list.add(new Grid("Cambio", "Tasa de cambio oficial",0));
        list.add(new Grid("Apagón", "Apagón en La Habana",0));
        list.add(new Grid("Cubadebate", "Titulares de noticias",0));
        list.add(new Grid("Grannma", "Titulares de noticias",0));
    }

    private void onClick(int position) {
        switch (position) {
            case 1:
                sms.send("2266", "OFERTA");
                break;
            case 2:
                sms.send("2266", "TARIFA");
                break;
            case 3:
                sms.send("2266", "LTE");
                break;
            case 4:
                redes();
                break;
            case 6:
                dhl();
                break;
            case 7:
                clima();
                break;
            case 8:
                vuelos();
                break;
            case 9:
                embajadas();
                break;
            case 10:
                cambio();
                break;
            case 11:
                apagones();
                break;
            case 12:
                cubadenate();
                break;
            case 13:
                granma();
                break;
        }
    }

    private void redes() {
        BSheetEditText dialog = new BSheetEditText(getContext());
        dialog.setTitle("Tipo de Red");
        dialog.setMessage(
                "Escriba los 8 primeros dígitos de su IMEI. Para obtenerlo marque *#06# o en Ajustes > Acerca del dispositivo");
        dialog.setEnabledEditText(true);
        dialog.setBehaviorExpanded(true);
        dialog.setHint("IMEI");
        dialog.setInputType("number");
        dialog.setMaxLenght(8);
        dialog.setNavColorSurface(true);
        dialog.setPositiveButtom(
                view -> {
                    String numero = dialog.getEditTextValue();
                    if (!numero.isEmpty()) {
                        sms.send("2266", numero);
                    }
                });
        dialog.show();
    }

    private void dhl() {
        BSheetEditText dialog = new BSheetEditText(getContext());
        dialog.setTitle("DHL");
        dialog.setMessage("Inserte su código de rastreo");
        dialog.setEnabledEditText(true);
        dialog.setBehaviorExpanded(true);
        dialog.setHint("Código de rastreo");
        dialog.setInputType("text");
        dialog.setNavColorSurface(true);
        dialog.setPositiveButtom(
                view -> {
                    String numero = dialog.getEditTextValue();
                    if (!numero.isEmpty()) {
                        sms.send("8888", "DHL " + numero);
                    }
                });
        dialog.show();
    }

    private void clima() {
        M3BottomSheetList dialog =
                new M3BottomSheetList.Builder(getActivity())
                        .setArrayList(R.array.clima)
                        .setMessage("Estado del tiempo por provincias")
                        .setTitle("Clima")
                        .build();
        dialog.setItemClick(
                position -> {
                    switch (position) {
                        case 0:
                            sms.send("8888", "VLC");
                            break;
                        case 1:
                            sms.send("8888", "GTM");
                            break;
                        case 2:
                            sms.send("8888", "HLG");
                            break;
                        case 3:
                            sms.send("8888", "ART");
                            break;
                        case 4:
                            sms.send("8888", "GRM");
                            break;
                        case 5:
                            sms.send("8888", "LHA");
                            break;
                        case 6:
                            sms.send("8888", "MAY");
                            break;
                        case 7:
                            sms.send("8888", "IJU");
                            break;
                        case 8:
                            sms.send("8888", "CMG");
                            break;
                        case 9:
                            sms.send("8888", "CFG");
                            break;
                        case 10:
                            sms.send("8888", "PRL");
                            break;
                        case 11:
                            sms.send("8888", "SCU");
                            break;
                        case 12:
                            sms.send("8888", "SSP");
                            break;
                        case 13:
                            sms.send("8888", "LTU");
                            break;
                        case 14:
                            sms.send("8888", "MTZ");
                            break;
                        case 15:
                            sms.send("8888", "CAV");
                            break;
                    }
                });
        dialog.show();
    }

    private void vuelos() {
        BSheetEditText dialog = new BSheetEditText(getContext());
        dialog.setTitle("Vuelos");
        dialog.setMessage("Inserte su número de vuelo");
        dialog.setEnabledEditText(true);
        dialog.setBehaviorExpanded(true);
        dialog.setHint("WN3952");
        dialog.setInputType("text");
        dialog.setNavColorSurface(true);
        dialog.setPositiveButtom(
                view -> {
                    String numero = dialog.getEditTextValue();
                    if (!numero.isEmpty()) {
                        sms.send("8888", "VUELO " + numero);
                    }
                });
        dialog.show();
    }

    private void embajadas() {
        BSheetEditText dialog = new BSheetEditText(getContext());
        dialog.setTitle("Embajada");
        dialog.setMessage(
                "Para embajadas que contengan (ñ) debe escribirse con (nn). Ejemplo: Espanna");
        dialog.setEnabledEditText(true);
        dialog.setBehaviorExpanded(true);
        dialog.setHint("Embajada");
        dialog.setInputType("text");
        dialog.setNavColorSurface(true);
        dialog.setPositiveButtom(
                view -> {
                    String numero = dialog.getEditTextValue();
                    if (!numero.isEmpty()) {
                        sms.send("8888", "EMBAJADA " + numero);
                    }
                });
        dialog.show();
    }

    private void cambio() {
        M3BottomSheetList dialog =
                new M3BottomSheetList.Builder(getActivity())
                        .setArrayList(R.array.cambio)
                        .setMessage("Tasas de cambio oficial")
                        .setTitle("Cambio")
                        .build();
        dialog.setItemClick(
                position -> {
                    switch (position) {
                        case 0:
                            sms.send("8888", "EUR");
                            break;
                        case 1:
                            sms.send("8888", "USD");
                            break;
                        case 2:
                            sms.send("8888", "MXN");
                            break;
                        case 3:
                            sms.send("8888", "CAD");
                            break;
                        case 4:
                            sms.send("8888", "GBP");
                            break;
                        case 5:
                            sms.send("8888", "JPY");
                            break;
                        case 6:
                            sms.send("8888", "CHF");
                            break;
                        case 7:
                            sms.send("8888", "DKK");
                            break;
                        case 8:
                            sms.send("8888", "NOK");
                            break;
                        case 9:
                            sms.send("8888", "SEK");
                            break;
                    }
                });
        dialog.show();
    }

    private void apagones() {
        M3BottomSheetList dialog =
                new M3BottomSheetList.Builder(getActivity())
                        .setArrayList(R.array.apagon)
                        .setMessage("Información con la programación de apagones en La Habana.")
                        .setTitle("Apagón")
                        .build();
        dialog.setItemClick(
                position -> {
                    switch (position) {
                        case 0:
                            sms.send("8888", "APAGON B1");
                            break;
                        case 1:
                            sms.send("8888", "APAGON B2");
                            break;
                        case 2:
                            sms.send("8888", "APAGON B3");
                            break;
                        case 3:
                            sms.send("8888", "APAGON B4");
                            break;
                    }
                });
        dialog.show();
    }

    private void cubadenate() {
        M3BottomSheetList dialog =
                new M3BottomSheetList.Builder(getActivity())
                        .setArrayList(R.array.periodicos)
                        .setMessage("Reciba mensajes con los titulares de Cubadebate.")
                        .setTitle("Cubadebate")
                        .build();
        dialog.setItemClick(
                position -> {
                    switch (position) {
                        case 0:
                            sms.send("8100", "CUBADEBATE");
                            break;
                        case 1:
                            sms.send("8888", "CUBADEBATE BAJA");
                            break;
                    }
                });
        dialog.show();
    }

    private void granma() {
        M3BottomSheetList dialog =
                new M3BottomSheetList.Builder(getActivity())
                        .setArrayList(R.array.periodicos)
                        .setMessage("Reciba mensajes con los titulares del periódico Granma.")
                        .setTitle("Granma")
                        .build();
        dialog.setItemClick(
                position -> {
                    switch (position) {
                        case 0:
                            sms.send("8100", "GRANMA");
                            break;
                        case 1:
                            sms.send("8888", "GRANMA BAJA");
                            break;
                    }
                });
        dialog.show();
    }
}
