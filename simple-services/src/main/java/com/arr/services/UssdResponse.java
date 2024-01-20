package com.arr.services;

import android.view.View;
import android.widget.TextView;

import com.arr.services.utils.ussd.SendUssdUtils;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UssdResponse {

    private SendUssdUtils utils;
    private String datos = "Tarifa: No activa. Paquetes: 8.58 GB + 8.20 GB LTE validos 1 hora.";
    private String mensajeria =
            "Tarifa: No activa. Mensajeria: 600 MB validos 26 dias. Diaria: 200 MB validos 24 horas. Paquetes: 8.58 GB + 8.20 GB LTE validos 1 dia.";
    private String soloDiaria = "Tarifa: No activa. Diaria: 200 MB validos 24 horas.";
    private String cu = "Datos.cu 297 MB vence 14-09-23.";
    private String testBonos =
            "Datos: ilimitados vence 26-08-23. 25.18 GB vence 26-08-23. Datos.cu 297 MB vence 14-09-23.";
    private String testBonosSaldo =
            "$831.72 vence 29-06-23. Datos: ilimitados vence 25-07-23. 24.64 GB vence 25-07-23. Datos.cu 295 MB vence 25-07-23.";
    private String newDatos =
            "Datos: 19.91 GB + 24.02 GB LTE vence 06-01-24. Voz: 05:06:05 vence 06-01-24. SMS: 399 vence 06-01-24. Datos.cu 300 MB vence 06-01-24.";

    public UssdResponse(SendUssdUtils util) {
        this.utils = util;
    }

    public void balancesSaldo(
            TextView saldo,
            TextView expireSaldo,
            TextView minutos,
            TextView mensajes,
            TextView minSmsExpire) {
        expireSaldo.setText("Expira: " + getExpireSIM());
        saldo.setText(getSaldoMovil() + " CUP");
        minutos.setText(getMinutos());
        mensajes.setText(getMensajes() + " SMS");

        // toma los valores de uno de los dos String
        if (getExpireMensajes().isBlank()) {
            minSmsExpire.setText(getExpireMinutos());
        } else {
            minSmsExpire.setText(getExpireMensajes());
        }
    }

    public void balancesDatos(
            TextView tarifa,
            TextView allData,
            TextView lte,
            TextView mensajeria,
            TextView diaria,
            TextView dtaNacional) {
        tarifa.setText(getTarifa());
        allData.setText(getData());
        lte.setText(getLTE());
        mensajeria.setText(getMensajeria());
        diaria.setText(getDiaria());
        dtaNacional.setText(getNacional());
    }

    public void balanceVencimiento(
            TextView dataExpire, TextView mensajeriaExpire, TextView diariaExpire) {
        dataExpire.setText(getDataExpire());
        mensajeriaExpire.setText(getExpireMensajeria());
        diariaExpire.setText(getExpireDiaria());
    }

    public void balanceBonos(
            MaterialCardView cardBonos,
            TextView ilimitados,
            TextView bonoSaldo,
            TextView bonosDatos,
            TextView bonosLte,
            TextView bonoVoz,
            TextView bonoSMS) {
        if (getIlimitados() != null
                || getBonosDatos() != null
                || getBonoSaldo() != null
                || getBonosLTE() != null
                || getBonosSMS() != null
                || getBonosVOZ() != null) {
            cardBonos.setVisibility(View.VISIBLE);
        } else {
            cardBonos.setVisibility(View.GONE);
        }

        if (getIlimitados() != null) {
            ilimitados.setVisibility(View.VISIBLE);
            ilimitados.setText(getIlimitados());
        } else {
            ilimitados.setVisibility(View.GONE);
        }

        // bono saldo
        if (getBonoSaldo() != null) {
            bonoSaldo.setVisibility(View.VISIBLE);
            bonoSaldo.setText("+ " + getBonoSaldo());
        } else {
            bonoSaldo.setVisibility(View.GONE);
        }

        // bonos datos
        if (getBonosDatos() != null) {
            bonosDatos.setVisibility(View.VISIBLE);
            bonosDatos.setText(getBonosDatos());
        } else {
            bonosDatos.setVisibility(View.GONE);
        }

        if (getBonosLTE() != null) {
            bonosLte.setVisibility(View.VISIBLE);
            bonosLte.setText(getBonosLTE());
        } else {
            bonosLte.setVisibility(View.GONE);
        }

        if (getBonosVOZ() != null) {
            bonoVoz.setVisibility(View.VISIBLE);
            bonoVoz.setText(getBonosVOZ());
        } else {
            bonoVoz.setVisibility(View.GONE);
        }

        if (getBonosSMS() != null) {
            bonoSMS.setVisibility(View.VISIBLE);
            bonoSMS.setText(getBonosSMS() + " SMS");
        } else {
            bonoSMS.setVisibility(View.GONE);
        }
    }

    /* obtener tarifa por consumo */
    private String getTarifa() {
        String response = utils.response("datos");
        String data = "Tarifa:\\s+(?<tarifa>[^\\.]+)";
        Pattern pattern = Pattern.compile(data);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String tarifa = matcher.group("tarifa");
            return (tarifa != null) ? tarifa : "sin obtener";
        }
        return "sin obtener";
    }

    /* obtener datos de todas las redes */
    private String getData() {
        String response = utils.response("datos");
        String data = "Paquetes:\\s+(?<data>(\\d+(\\.\\d+)?)(\\s)*(G|M|K)?B)";
        Pattern pattern = Pattern.compile(data);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String datos = matcher.group("data");
            if (datos.isEmpty() || datos != null) {
                return datos;
            }
        }
        return "0 MB";
    }

    /* obtener datos lte */
    private String getLTE() {
        String response = utils.response("datos");
        String data = "\\+\\s+(?<lte>(\\d+(\\.\\d+)?)(\\s)*(G|M|K)?B)";
        Pattern pattern = Pattern.compile(data);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String datos = matcher.group("lte");
            if (datos.isEmpty() || datos != null) {
                return datos;
            }
        }
        return "0 MB";
    }

    /* obtener datos de mensajeria */
    private String getMensajeria() {
        String response = utils.response("datos");
        String data = "Mensajeria:\\s+(?<mensajeria>(\\d+(\\.\\d+)?)(\\s)*(G|M|K)?B)";
        Pattern pattern = Pattern.compile(data);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String datos = matcher.group("mensajeria");
            if (datos.isEmpty() || datos != null) {
                return datos;
            }
        }
        return "0 MB";
    }

    /* obtener cabtidad de datos de bolsa diaria */
    private String getDiaria() {
        String response = utils.response("datos");
        String data = "Diaria:\\s+(?<diaria>(\\d+(\\.\\d+)?)(\\s)*(G|M|K)?B)";
        Pattern pattern = Pattern.compile(data);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String datos = matcher.group("diaria");
            if (datos.isEmpty() || datos != null) {
                return datos;
            }
        }
        return "0 MB";
    }

    /* obtener cabtidad de datos nacionales */
    private String getNacional() {
        String response = utils.response("bonos");
        String data = "Datos.cu\\s+(?<nacional>(\\d+(\\.\\d+)?)(\\s)*(G|M|K)?B)";
        Pattern pattern = Pattern.compile(data);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String datos = matcher.group("nacional");
            if (datos.isEmpty() || datos != null) {
                return datos;
            }
        }
        return "0 MB";
    }

    private String getDataExpire() {
        String response = utils.response("datos");
        String data =
                "Paquetes:\\s+(?<paquete1>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)?(\\s+\\+\\s+)?((?<paquete2>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)\\s+LTE)?(\\s+validos\\s+(?<expire>(\\d+\\s+(dias|dia|horas|hora))))?\\.";
        Pattern pattern = Pattern.compile(data);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String datos = matcher.group("expire");
            if (datos.isEmpty() || datos != null) {
                return datos;
            }
        }
        return "0 dias";
    }

    /* expire all data view in progressbar */
    public int expireDaysProgress() {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(getDataExpire());
        while (matcher.find()) {
            String data = matcher.group();
            return Integer.parseInt(data);
        }

        return 0;
    }

    /* expire mensajeria */
    private String getExpireMensajeria() {
        String response = utils.response("datos");
        String data =
                "Mensajeria:\\s+(?<mensajeria>(\\d+(\\.\\d+)?)(\\s)*(G|M|K)?B)?(\\s+validos\\s+(?<expire>(\\d+\\s(dias|dia))))?\\.";
        Pattern pattern = Pattern.compile(data);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String datos = matcher.group("expire");
            if (datos.isEmpty() || datos != null) {
                return datos;
            }
        }
        return "0 dias";
    }

    /* expire bolsa diaria */
    private String getExpireDiaria() {
        String response = utils.response("datos");
        String data =
                "Diaria:\\s+(?<diaria>(\\d+(\\.\\d+)?)(\\s)*(G|M|K)?B)?(\\s+validos\\s+(?<expire>(\\d+\\s(horas|hora))))?\\.";
        Pattern pattern = Pattern.compile(data);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String datos = matcher.group("expire");
            if (datos.isEmpty() || datos != null) {
                return datos;
            }
        }
        return "0 dias";
    }

    /* extraer cantidad de minutos */
    private String getMinutos() {
        String response = utils.response("min");
        String obtains = "Usted dispone de\\s+(?<minutos>(\\d+:\\d{2}:\\d{2}))";
        Pattern pattern = Pattern.compile(obtains);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String datos = matcher.group("minutos");
            if (datos.isEmpty() || datos != null) {
                return datos;
            }
        }
        return "00:00:00";
    }

    /* extraer cantidad de mensajes */
    private String getMensajes() {
        String response = utils.response("sms");
        String cadena =
                "Usted dispone de\\s+(?<mensajes>(\\d+))\\s+SMS\\s+validos por\\s+(?<dias>(\\d+\\s+dias))";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("mensajes");
            if (result.isEmpty() || result != null) {
                return result;
            }
        }
        return "0";
    }

    /* extraer informacion de expiracion de minutos */
    private String getExpireMinutos() {
        String response = utils.response("min");
        String cadena = "validos por\\s+(?<dias>(\\d+\\s+dias))";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("dias");
            if (result.isEmpty() || result != null) {
                return result;
            }
        }
        return "0 dias";
    }

    /* extraer informacion de expiracion de mensajes */
    private String getExpireMensajes() {
        String response = utils.response("sms");
        String cadena = "validos por\\s+(?<dias>(\\d+\\s+dias))";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("dias");
            if (result.isEmpty() || result != null) {
                return result;
            }
        }
        return "0 dias";
    }

    /* extraer saldo principal */
    private String getSaldoMovil() {
        String response = utils.response("saldo");
        String cadena = "Saldo:\\s(?<balance>([\\d.]+))";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("balance");
            if (result.isEmpty() || result != null) {
                return result;
            }
        }
        return "0.00";
    }

    /* extraer fecha de vencimiento de la sim */
    private String getExpireSIM() {
        String response = utils.response("saldo");
        String cadena =
                "(Saldo:\\s+(?<saldo>([\\d.]+))\\s+CUP\\.\\s+([^\"]*?)?Linea activa hasta\\s+(?<activa>(\\d{2}-\\d{2}-\\d{2}))\\s+vence\\s+(?<expire>(\\d{2}-\\d{2}-\\d{2}))\\.)";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("expire");
            if (result.isEmpty() || result != null) {
                return result.replace("-", "/");
            }
        }
        return "00/00/00";
    }

    /* extraer datos ilimitados de bonos em promoción */
    private String getIlimitados() {
        String response = utils.response("bonos");
        String cadena =
                "Datos:\\s+(?<ilimitados>[^\\s]+)\\s+vence\\s+(?<fechaVencimiento>\\d{2}-\\d{2}-\\d{2})\\.";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("ilimitados");
            String result2 = matcher.group("fechaVencimiento");
            if (result != null || result.isEmpty()) {
                return result;
            }
        }
        return null;
    }

    public String getBonoSaldo() {
        String response = utils.response("bonos");
        String cadena =
                "(\\$(?<bonoSaldo>([\\d.]+))\\s+vence\\s+(?<vence>(\\d{2}-\\d{2}-\\d{2})).)?";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("bonoSaldo");
            String result2 = matcher.group("vence");
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public String getBonosDatos() {
        String response = utils.response("bonos");
        String regex = "Datos:\\s+(?<datos>(\\d+(\\.\\d+)?)(\\s)*(G|M|K)?B)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("datos");
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public String getBonosLTE() {
        String response = utils.response("bonos");
        String regex = "\\+\\s+(?<lte>(\\d+(\\.\\d+)?)(\\s)*(G|M|K)?B)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("lte");
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public String getBonosVOZ() {
        String response = utils.response("bonos");
        String regex = "Voz:\\s(?<voz>(\\d+:\\d{2}:\\d{2}))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("voz");
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public String getBonosSMS() {
        String response = utils.response("bonos");
        String regex = "SMS:\\s+(?<sms>(\\d+))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("sms");
            if (result != null) {
                System.out.println("SMS " + result);
                return result;
            }
        }
        return null;
    }
}
