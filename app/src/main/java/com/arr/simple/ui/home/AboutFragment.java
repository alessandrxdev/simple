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
import android.widget.ListView;
import android.widget.Toast;
import com.arr.simple.R;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.arr.simple.databinding.FragmentAboutBinding;
import com.arr.ussd.Call;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.elevation.SurfaceColors;
import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;
    private List<Users> listColab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);

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

        // donar
        binding.donate.setOnClickListener(
                view -> {
                    bottomSheetDonate();
                });

        // licencias
        binding.licencias.setOnClickListener(
                view -> {
                    bottomSheetLicencias();
                });

        listColab = new ArrayList<>();
        listColab.add(new Users("Rosanna Moreno", "Colaboradora"));
        listColab.add(new Users("Harold Adan", "Corrección Ortográfica"));
        listColab.add(new Users("Beta tester", "A los usuarios que testean la app"));
        AdapterAbout adapter = new AdapterAbout(getActivity(), listColab);
        binding.list.setAdapter(adapter);
        binding.list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> arg0, View arg1, int position, long arg3) {
                        onClick(position);
                    }
                });

        return binding.getRoot();
    }

    private void onClick(int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://lnk.bio/roclahy")));
                break;
            case 1:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://t.me/haroldadan")));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class AdapterAbout extends ArrayAdapter<Users> {

        private Context context;
        private List<Users> list;

        public AdapterAbout(Context context, List<Users> list) {
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
                view = inflater.inflate(R.layout.list_item_layout, parent, false);
            }
            Users item = list.get(position);
            TextView text = (TextView) view.findViewById(R.id.text_list);
            text.setText(item.getName());

            TextView descrip = (TextView) view.findViewById(R.id.text_descrip);
            descrip.setText(item.getDescription());
            return view;
        }
    }

    private static class Users {
        private String name, descrip;

        public Users(String name, String descrip) {
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

    private void bottomSheetLicencias() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(R.layout.layout_bottom_sheet_licencias);
        dialog.getWindow()
                .setNavigationBarColor(SurfaceColors.SURFACE_1.getColor(requireContext()));

        // list view
        ListView list = (ListView) dialog.findViewById(R.id.list_view);
        List<Users> listColab = new ArrayList<>();
        listColab.add(new Users("Material Components for Android", "Apache 2.0 license"));
        listColab.add(new Users("Material Design icons by Google", "Apache 2.0 license"));
        listColab.add(new Users("Glide", "BSD, part MIT and Apache 2.0"));
        listColab.add(new Users("QRGenerator", "MIT license"));
        listColab.add(new Users("Pager Dots Indicator", "Apache-2.0 license"));
        listColab.add(new Users("Shimmer for Android", "BSD License. Meta Platforms, Inc. and affiliates."));
        listColab.add(new Users("ZXing Android Embedded", "Apache-2.0 license"));
        listColab.add(new Users("suitetecsa-sdk-kotlin", "MIT license"));
        AdapterAbout adapter = new AdapterAbout(getActivity(), listColab);
        list.setAdapter(adapter);
        dialog.show();
    }
}
