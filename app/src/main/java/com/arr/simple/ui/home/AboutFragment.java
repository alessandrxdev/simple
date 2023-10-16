package com.arr.simple.ui.home;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.arr.simple.R;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.arr.simple.adapter.AboutAdapter;
import com.arr.simple.databinding.FragmentAboutAppBinding;
import com.arr.simple.databinding.FragmentAboutBinding;
import com.arr.simple.model.About;
import com.arr.simple.model.Header;
import com.arr.simple.model.Items;
import com.arr.ussd.Call;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.elevation.SurfaceColors;
import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends Fragment {

    private FragmentAboutAppBinding binding;
    private List<Items> list = new ArrayList<>();
    private AboutAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentAboutAppBinding.inflate(inflater, container, false);

        // adapter
        adapter =
                new AboutAdapter(
                        requireContext(),
                        list,
                        position -> {
                            onClick(position);
                        });
        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recycler.setAdapter(adapter);

        // list
        list.add(new Header("Gracias a"));
        list.add(new About(R.drawable.logo_roclahay, "Rosanna", "Colaboración y Moderación"));
        list.add(new About(R.drawable.ic_account_circle_24px, "Harold Adan", "Ortografía"));
        list.add(new About(R.drawable.ic_account_circle_24px, "Ordiel Victor", "Colaborador"));
        list.add(new Header("Otros"));
        list.add(new About(R.drawable.ic_google_play_24px,"Valóranos","Ayudanos a crecer en PlayStore"));
        list.add(new About(R.drawable.ic_politicas_24px, "Licencias", "Licencias de terceros"));
        list.add(new About(R.drawable.ic_favorite_24px, "Donar", "Apoyar nuestro proyecto"));
        list.add(new About(R.drawable.ic_translate_24px,"Traducir","Ayudar a traducir la aplicación"));
        
        // app version
        try {
            String version =
                    requireActivity()
                            .getPackageManager()
                            .getPackageInfo(requireActivity().getPackageName(), 0)
                            .versionName;
            binding.version.setText("v" + version);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // twitter
        binding.twitter.setOnClickListener(
                view -> {
                    startActivity(
                            new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(getString(R.string.url_twitter))));
                });

        // github
        binding.github.setOnClickListener(
                view -> {
                    startActivity(
                            new Intent(
                                    Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_github))));
                });

        // telegram
        binding.telegram.setOnClickListener(
                view -> {
                    startActivity(
                            new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(getString(R.string.url_telegram))));
                });

        return binding.getRoot();
    }

    private void onClick(int position) {
        switch (position) {
            case 1:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://lnk.bio/roclahy")));
                break;
            case 2:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://t.me/haroldadan")));
                break;
            case 3:
                startActivity(
                        new Intent(
                                Intent.ACTION_VIEW, Uri.parse("https://github.com/OrdielVictor")));
                break;
            case 5:
                startActivity(
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                        "https://play.google.com/store/apps/details?id=com.arr.simple")));
                break;
            case 6:
                bottomSheetLicencias();
                break;
            case 7:
                break;
            case 8:
                startActivity(
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://explore.transifex.com/applify/simpleapp/")));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class AdapterAbout extends ArrayAdapter<Data> {

        private Context context;
        private List<Data> list;

        public AdapterAbout(Context context, List<Data> list) {
            super(context, 0, list);
            this.context = context;
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.layout_items_list_view, parent, false);
            }
            Data item = list.get(position);
            TextView text = (TextView) view.findViewById(R.id.text_title);
            text.setText(item.getName());

            TextView descrip = (TextView) view.findViewById(R.id.text_subtitle);
            descrip.setText(item.getDescription());

            return view;
        }
    }

    private static class Data {

        private String name, descrip;

        public Data(String name, String descrip) {
            this.name = name;
            this.descrip = descrip;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return descrip;
        }
    }
    /*
        private void bottomSheetDonate() {
            BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
            dialog.setContentView(R.layout.layout_bottom_sheet_donate);
            dialog.getWindow()
                    .setNavigationBarColor(SurfaceColors.SURFACE_1.getColor(requireContext()));

            // cup
            Button cup = (Button) dialog.findViewById(R.id.button_cup);
            cup.setOnClickListener(
                    view -> {
                        ClipboardManager clipboardManager =
                                (ClipboardManager)
                                        getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("", "9205 1299 7946 3446");
                        PersistableBundle extras = new PersistableBundle();
                        extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true);
                        clipData.getDescription().setExtras(extras);
                        clipboardManager.setPrimaryClip(clipData);

                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                            Toast.makeText(
                                            getActivity(),
                                            "Tarjeta copiada al portapapeles",
                                            Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

            // cripto
            Button usdt = (Button) dialog.findViewById(R.id.button_cripto);
            usdt.setOnClickListener(
                    view -> {
                        ClipboardManager clipboardManager =
                                (ClipboardManager)
                                        getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData =
                                ClipData.newPlainText(
                                        "", "UQDXmYkjKxZJtZPgTbJvm8CCHei9R4r54Jzo9AChBUd5WNKB");
                        PersistableBundle extras = new PersistableBundle();
                        extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true);
                        clipData.getDescription().setExtras(extras);
                        clipboardManager.setPrimaryClip(clipData);

                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                            Toast.makeText(
                                            getActivity(),
                                            "Dirección de billetera copiada al portapapeles",
                                            Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

            // sqp
            Button sqp = (Button) dialog.findViewById(R.id.button_sqp);
            sqp.setOnClickListener(
                    view -> {
                        startActivity(
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://qvapay.com/payme/alessandro")));
                    });

            // saldo
            Button saldo = (Button) dialog.findViewById(R.id.button_saldo);
            saldo.setOnClickListener(
                    view -> {
                        new Call(getActivity()).code("*234*1*54250705" + Uri.encode("#"), "0");
                    });

            dialog.show();
        }
    */
    private void bottomSheetLicencias() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(R.layout.layout_bottom_sheet_licencias);
        dialog.getWindow()
                .setNavigationBarColor(SurfaceColors.SURFACE_1.getColor(requireContext()));

        // list view
        ListView list = (ListView) dialog.findViewById(R.id.list_view);
        List<Data> listColab = new ArrayList<>();
        listColab.add(new Data("Material Components for Android", "Apache 2.0 license"));
        listColab.add(new Data("Material Design icons by Google", "Apache 2.0 license"));
        listColab.add(new Data("Glide", "BSD, part MIT and Apache 2.0"));
        listColab.add(new Data("QRGenerator", "MIT license"));
        listColab.add(new Data("Pager Dots Indicator", "Apache-2.0 license"));
        listColab.add(
                new Data(
                        "Shimmer for Android",
                        "BSD License. Meta Platforms, Inc. and affiliates."));
        listColab.add(new Data("ZXing Android Embedded", "Apache-2.0 license"));
        listColab.add(new Data("suitetecsa-sdk-kotlin", "MIT license"));
        listColab.add(new Data("lottie", "Apache License 2.0"));
        AdapterAbout adapter = new AdapterAbout(getActivity(), listColab);
        list.setAdapter(adapter);
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> arg0, View arg1, int position, long arg3) {
                        onClickLicense(position);
                        Toast.makeText(getActivity(), "" + position, Toast.LENGTH_LONG).show();
                    }
                });
        dialog.show();
    }

    private void onClickLicense(int position) {
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                linkTo("https://github.com/suitetecsa/sdk-kotlin");
                break;
            case 8:
                linkTo("https://lottiefiles.com/es/");
                break;
        }
    }

    private void linkTo(String link) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }
}
