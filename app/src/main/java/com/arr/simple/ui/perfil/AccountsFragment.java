package com.arr.simple.ui.perfil;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.preference.PreferenceManager;
import com.arr.simple.R;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.arr.simple.databinding.FragmentAccountBinding;

public class AccountsFragment extends Fragment {
    
    private FragmentAccountBinding binding;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle arg2) {
        binding = FragmentAccountBinding.inflate(inflater, parent, false);
        setHasOptionsMenu(true);
        
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        binding.editInternet.setText(sp.getString("internet", ""));
        binding.editPassordInternet.setText(sp.getString("passInternet", ""));
        binding.editCorreo.setText(sp.getString("correo", ""));
        binding.editPassordCorreo.setText(sp.getString("passCorreo", ""));
        
        return binding.getRoot();
    
    }
    private void save(){
     String internet = binding.editInternet.getText().toString().trim();
     String passInternet = binding.editPassordInternet.getText().toString().trim();
     String correo = binding.editCorreo.getText().toString().trim();
     String passCorreo = binding.editPassordCorreo.getText().toString().trim();
     saveDatos(internet, passInternet, correo, passCorreo);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save_accounts, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.save){
            save();
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveDatos(String internet, String password, String correo, String passCorreo){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("internet", internet);
        editor.putString("passInternet", password);
        editor.putString("correo", correo);
        editor.putString("passCorreo", passCorreo);
        editor.apply();
    }
}
