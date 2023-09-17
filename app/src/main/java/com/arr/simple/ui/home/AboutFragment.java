package com.arr.simple.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.arr.simple.R;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.arr.simple.databinding.FragmentAboutBinding;
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

        listColab = new ArrayList<>();
        listColab.add(new Users("Rosanna Moreno", "Colaboradora"));
        listColab.add(new Users("Harold Adán", "Corrección Ortográfica"));
        listColab.add(new Users("Beta tester", ""));
        AdapterAbout adapter = new AdapterAbout(getActivity(), listColab);
        binding.list.setAdapter(adapter);

        return binding.getRoot();
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
}
