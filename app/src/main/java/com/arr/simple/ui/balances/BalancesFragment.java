package com.arr.simple.ui.balances;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.arr.simple.MainActivity;
import com.arr.simple.R;
import com.arr.simple.adapter.SliderAdapter;
import com.arr.simple.databinding.FragmentBalancesBinding;
import com.arr.simple.model.ItemsSlider;

import com.arr.ussd.ResponseUssd;
import com.arr.ussd.utils.UssdUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RequiresApi(28)
public class BalancesFragment extends Fragment {

    private FragmentBalancesBinding binding;
    private UssdUtils ussd;
    private ResponseUssd response;
    private SliderAdapter slider;
    private final List<ItemsSlider> list = new ArrayList<>();

    private Handler handlerr;
    private final String[] ussdCodes = {
        "*222#", "*222*328#", "*222*266#", "*222*767#", "*222*869#"
    };
    private final String[] ussdKeys = {"saldo", "datos", "bonos", "sms", "min"};
    private SharedPreferences spBalance;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentBalancesBinding.inflate(inflater, container, false);

        // TODO: preferences
        spBalance = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        editor = spBalance.edit();

        // USSD
        ussd = new UssdUtils(getActivity());
        response = new ResponseUssd(ussd);

        // TODO: Actualizar
        binding.swipeRefresh.setOnRefreshListener(
                () -> {
                    showSnackBar("Consultando, espere...", false);
                    Handler handler = new Handler(Looper.getMainLooper());
                    executeUssdRequest(handler, 0);
                });

        // slider
        ViewPager viewpager = binding.viewPage;
        WormDotsIndicator dots = binding.dotsIndicator;
        slider = new SliderAdapter(getActivity());
        viewpager.setAdapter(slider);
        update();
        // TODO: Slider visibility
        slider.setContent(list);
        if (slider.getCount() == 0) {
            dots.setVisibility(View.GONE);
        } else {
            dots.setVisibility(View.VISIBLE);
            dots.attachTo(viewpager);
        }
        handlerr = new Handler(Looper.getMainLooper());
        Runnable runnable =
                new Runnable() {
                    public void run() {
                        int currentPosition = viewpager.getCurrentItem();
                        int nextPosition = currentPosition + 1;
                        if (nextPosition >= slider.getCount()) {
                            nextPosition = 0;
                        }
                        viewpager.setCurrentItem(nextPosition);
                        handlerr.postDelayed(this, 6000);
                    }
                };
        handlerr.postDelayed(runnable, 6000);

        // TODO: Update hora
        String updateHora = spBalance.getString("actualizado", "");
        if (updateHora != null && !updateHora.isEmpty()) {
            binding.textHoraVencimiento.setText(updateHora);
        } else {
            binding.textHoraVencimiento.setText("sin actualizar");
        }
        return binding.getRoot();
    }

    private void update() {
        if (!response.getError().isEmpty()) {
            new MaterialAlertDialogBuilder(getActivity())
                    .setMessage(
                            "Ah ocurrido un error al actualizar algunos de sus balances, por favor vuelva a intentarlo.\n\n "
                                    + response.getError())
                    .setPositiveButton(
                            "Ok",
                            (dialog, w) -> {
                                response.getClearError(getActivity());
                            })
                    .show();
        }

        if (!response.getBonoIlimitado().isEmpty()) {
            list.add(
                    new ItemsSlider(
                            R.drawable.ic_data_ilimitado_24px,
                            "Ilimitados",
                            response.getBonoIlimitado(),
                            response.getTirmpoIlimitado()));
        }
        if (!response.getBonosDatos().isEmpty()) {
            list.add(
                    new ItemsSlider(
                            R.drawable.ic_datos_24px,
                            "Paquete",
                            response.getBonosDatos(),
                            response.getBonosDatosVence()));
        }
        binding.textTarifa.setText(response.getTarifaConsumo());
        binding.textDatos.setText(response.getDataAll());
        binding.textDatosLte.setText("/ " + response.getLTE());
        binding.textVenceDatos.setText(response.getVenceData());

        binding.mensajes.setText(response.getMensajes() + " / " + response.getVenceMensajes());
        binding.textMinutos.setText(response.getMinutos() + " / " + response.getVenceMinutos());

        // TODO: datos nacionales
        binding.textDatosCu.setText(response.getDataCu());
        // TODO: mensajeria
        binding.textMensajeria.setText(response.getDataMensajeria());

        // fechas de vencimiento
        // dias restantes datos
        String diasDatos = response.getVenceData();
        if (diasDatos != null) {
            int dias = Integer.parseInt(diasDatos.replace(" dias", ""));
            updateProgress(dias);
        } else {
            updateProgress(2);
        }

        // TODO: dias restante de sms
        String smsDias = response.getVenceMensajes();
        if (smsDias != null) {
            int dias = Integer.parseInt(smsDias.replace(" días", ""));
            updateLinearProgress(dias, binding.smsLinearProgress);
        } else {
            updateLinearProgress(2, binding.smsLinearProgress);
        }

        // TODO: dias restante de voz
        String vozDias = response.getVenceMinutos();
        if (vozDias != null) {
            int dias = Integer.parseInt(vozDias.replace(" días", ""));
            updateLinearProgress(dias, binding.progressLinearVoz);
        } else {
            updateLinearProgress(2, binding.progressLinearVoz);
        }

        String venceNacional = response.getVenceDataCu();
        if (!venceNacional.isEmpty()) {
            int dias = Integer.parseInt(venceNacional.replace(" días", ""));
            updateLinearProgress(dias, binding.progressDatosCu);
        } else {
            updateLinearProgress(2, binding.progressDatosCu);
        }
    }

    private void executeUssdRequest(Handler handler, int index) {
        if (index >= ussdCodes.length) {
            // Se han realizado todas las consultas
            showSnackBar("Consulta completada!", true);
            binding.swipeRefresh.setRefreshing(false);
            update();
            updateHora();
            boolean isCheck = spBalance.getBoolean("vence", true);
            if (!isCheck) {
                String datos = response.getVenceData();
                String days = calculateDays(datos);
                addReminding(days);
            }

            return;
        }
        String ussdCode = ussdCodes[index];
        String ussdKey = ussdKeys[index];
        ussd.execute(0, ussdCode, ussdKey);
        handler.postDelayed(
                () -> {
                    String response = ussd.response(ussdKey);
                    if (!response.isEmpty()) {
                        executeUssdRequest(handler, index + 1);
                    } else {
                        executeUssdRequest(handler, index);
                    }
                },
                6000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int diasRestantes(int dias) {
        return (int) (Math.random() * dias);
    }

    private void updateProgress(int diasRestantes) {
        int progress = (int) ((diasRestantes / (float) 30) * 100);
        binding.progressDatos.setMax(100);
        binding.progressDatos.setProgress(progress);
    }

    private void updateLinearProgress(int dias, LinearProgressIndicator progress) {
        int rest = (int) ((dias / (float) 30) * 100);
        progress.setMax(100);
        progress.setProgress(rest);
    }

    private void updateHora() {
        Calendar calendar = Calendar.getInstance();
        Date dat = calendar.getTime();
        SimpleDateFormat datFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String hActual = datFormat.format(dat);
        editor.putString("actualizado", "Última actualización: " + hActual.toString());
        editor.apply();
        String hor = spBalance.getString("actualizado", "");
        binding.textHoraVencimiento.setText(hor);
    }

    public void showSnackBar(String message, boolean dimissable) {
        CoordinatorLayout coordinator = ((MainActivity) getActivity()).getCoordnator();
        BottomNavigationView nav = ((MainActivity) getActivity()).getBottomNavigation();
        Snackbar snack = Snackbar.make(coordinator, message, Snackbar.LENGTH_SHORT);
        snack.setAnchorView(nav);
        if (dimissable) {
            snack.dismiss();
        } else {
            snack.setDuration(Snackbar.LENGTH_SHORT);
        }
        snack.show();
    }

    public String calculateDays(String string) {
        Calendar calendar = Calendar.getInstance();

        // Obtén el número de días del string
        int dias = Integer.parseInt(string.split(" ")[0]);

        // Suma los días a la fecha actual
        calendar.add(Calendar.DAY_OF_MONTH, dias);

        // Obtén la fecha exacta
        Date fechaExacta = calendar.getTime();

        // Crea un formato para mostrar la fecha
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");

        // Convierte la fecha en una cadena formateada
        String fechaFormateada = formatoFecha.format(fechaExacta);
        return fechaFormateada;
    }

    private void addReminding(String fechaString) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar fechaActual = Calendar.getInstance();
        try {
            Date fechaObjeto = dateFormat.parse("01-10-2023");
            Calendar fechaObjetoCal = Calendar.getInstance();
            fechaObjetoCal.setTime(fechaObjeto);
            fechaObjetoCal.add(Calendar.DAY_OF_YEAR, -5);
            if (fechaActual.compareTo(fechaObjetoCal) >= 0) {
                createNotification("SIMple", "Tiene paquetes próximos a vencer");
            } else {
                // No se requiere notificación
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void createNotification(String title, String message) {
        // Crear un canal de notificación para Android 8.0 y versiones posteriores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    getContext().getSystemService(NotificationManager.class);
            NotificationChannel channel =
                    new NotificationChannel(
                            "Paquetes",
                            "Paquetes próximos a vencer",
                            NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Crear la notificación utilizando NotificationCompat.Builder
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getContext(), "Paquetes")
                        .setSmallIcon(R.drawable.ic_calendar_20px)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        // Mostrar la notificación
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getContext());
        notificationManager.notify(0, builder.build());
    }
}
