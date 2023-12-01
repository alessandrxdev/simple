package com.arr.services;

import androidx.annotation.RequiresApi;
import com.arr.services.utils.ussd.SendUssdUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiresApi(28)
public class ResponseUssd {

    private final SendUssdUtils utils;
    private String testBonos =
            "Datos: ilimitados vence 26-08-23. 25.18 GB vence 26-08-23. Datos.cu 297 MB vence 14-09-23.";
    private String testBonosSaldo =
            "$831.72 vence 29-06-23. Datos: ilimitados vence 25-07-23. 24.64 GB vence 25-07-23. Datos.cu 295 MB vence 25-07-23.";

    public ResponseUssd(SendUssdUtils util) {
        this.utils = util;
    }

    // TODO: obtener saldo principal
    public String saldoMovil() {
        String response = utils.response("saldo");
        String cadena =
                "(Saldo:\\s+(?<saldo>([\\d.]+))\\s+CUP\\.\\s+([^\"]*?)?Linea activa hasta\\s+(?<activa>(\\d{2}-\\d{2}-\\d{2}))\\s+vence\\s+(?<cancelan>(\\d{2}-\\d{2}-\\d{2}))\\.)";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher != null && matcher.find()) {
            return matcher.group("saldo") + " CUP";
        } else {
            return "0.00 CUP";
        }
    }
    
    // TODO: vence saldo principal 
    public String venceSaldo(){
       String response = utils.response("saldo");
       String cadena = "(Saldo:\\s+(?<saldo>([\\d.]+))\\s+CUP\\.\\s+([^\"]*?)?Linea activa hasta\\s+(?<activa>(\\d{2}-\\d{2}-\\d{2}))\\s+vence\\s+(?<cancelan>(\\d{2}-\\d{2}-\\d{2}))\\.)";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher != null && matcher.find()) {
            return matcher.group("cancelan").replace("-","/");
        } else {
            return "00/00/00";
        }
    }

    // TODO: obtener la tarifa por consumo
    public String tarifa() {
        String message = utils.response("datos");
        Pattern dataPattern = Pattern.compile("Tarifa:\\s+(?<tarifa>[^\\.]+)");
        Matcher matcher = dataPattern.matcher(message);
        if (matcher.find()) {
            return matcher.group("tarifa");
        } else {
            return "sin obtener";
        }
    }

    // TODO: obtener cantidad de datos para todas las redes
    public String allData() {
        String response = utils.response("datos");
        String cadena =
                "(Tarifa:\\s+(?<tarifa>[^\"]*?)\\.)?(\\s+)?(Paquetes:\\s+(?<paquete1>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)?(\\s+\\+\\s+)?((?<paquete2>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)\\s+LTE)?(\\s+validos\\s+(?<vence>(\\d+\\s+dias)))?\\.)?";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("paquete1");
            return (result != null) ? result : "0 MB";
        } else {
            return "0 MB";
        }
    }

    // TODO: obtener datos LTE
    public String dataLte() {
        String response = utils.response("datos");
        String cadena =
                "(Tarifa:\\s+(?<tarifa>[^\"]*?)\\.)?(\\s+)?(Paquetes:\\s+(?<paquete1>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)?(\\s+\\+\\s+)?((?<paquete2>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)\\s+LTE)?(\\s+validos\\s+(?<vence>(\\d+\\s+dias)))?\\.)?";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("paquete2");
            return (result != null) ? result : "0 MB";
        }
        return "0 MB";
    }

    // TODO: obtener datos nacionales
    public String nacionales() {
        String response = utils.response("bonos");
        String cadena =
                "(Datos\\.cu\\s+(?<nacional>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)?(\\s+vence\\s+)?(?<vence>(\\d{2}-\\d{2}-\\d{2})))?.?";
        Pattern pattner = Pattern.compile(cadena);
        Matcher matcher = pattner.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("nacional");
            return (result != null) ? result : "0 MB";
        }
        return "0 MB";
    }

    // TODO: obtener fecha de vencimiento en cantidad de dias
    public String venceAllData() {
        String response = utils.response("datos");
        String cadena =
                "(Tarifa:\\s+(?<tarifa>[^\"]*?)\\.)?(\\s+)?(Paquetes:\\s+(?<paquete1>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)?(\\s+\\+\\s+)?((?<paquete2>(\\d+(\\.\\d+)?)(\\s)*([GMK])?B)\\s+LTE)?(\\s+validos\\s+(?<vence>(\\d+\\s+dias)))?\\.)?";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("vence");
            return (result != null) ? result : "0 dias";
        }
        return "0 dias";
    }

    // TODO: obtener cantidad de datos de la bolsa diaria
    public String diaria() {
        String response = utils.response("datos");
        String cadena = "Diaria:\\s+(?<diaria>[^\\.]+)\\s+validos\\s+(?<horas>[^\\.]+)";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("diaria");
            return (result != null) ? result : "0 MB";
        }
        return "0 MB";
    }
    
    // TODO: obtener vencimiento de la bolsa diaria
    public String venceDiaria(){
       String response = utils.response("datos");
        String cadena = "Diaria:\\s+(?<diaria>[^\\.]+)\\s+validos\\s+(?<horas>[^\\.]+)";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("horas");
            return (result != null) ? result : "0 horas";
        }
        return "0 horas";
    }
    
    // TODO: obtener cantidad de datos de la bolsa de mensajeria 
    public String mensajeria(){
        String response = utils.response("datos");
        String cadena = "Mensajeria:\\s+(?<datos>[^\\.]+)\\s+validos\\s+(?<dias>[^\\.]+)";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("datos");
                return (result != null) ? result : "0 MB";
        }
        return "0 MB";
    }
    
    // TODO: obtener fecha de vencimiento de la bolsa de mensajeria 
    public String venceMensajeria(){
        String response = utils.response("datos");
        String cadena = "Mensajeria:\\s+(?<datos>[^\\.]+)\\s+validos\\s+(?<dias>[^\\.]+)";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("dias");
                return (result != null) ? result : "0 dias";
        }
        return "0 dias";
    }
    
    // TODO: obtener cantidad de minutos 
    public String minutos(){
        String response = utils.response("min");
        String cadena = "Usted dispone de\\s+(?<minutos>(\\d+:\\d{2}:\\d{2}))\\s+MIN\\s+validos por\\s+(?<vence>(\\d+\\s+dias))";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("minutos");
                return (result != null) ? result : "00:00:00";
        }
        return "00:00:00";
    }
    
    // TODO: obtener cantidad de mensajes
    public String mensajes(){
        String response = utils.response("sms");
        String cadena = "Usted dispone de\\s+(?<mensajes>(\\d+))\\s+SMS\\s+validos por\\s+(?<dias>(\\d+\\s+dias))";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("mensajes") + " SMS";
                return (result != null) ? result : "0 SMS";
        }
        return "0 SMS";
    }
    
    // TODO: obtener vencimiento de mensajes 
    public String venceMensajes(){
        String response = utils.response("sms");
        String cadena = "Usted dispone de\\s+(?<mensajes>(\\d+))\\s+SMS\\s+validos por\\s+(?<dias>(\\d+\\s+dias))";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("dias");
                return (result != null) ? result : "0 días";
        }
        return "0 días";
    }
    
    // TODO: obtener vencimiento de minutos 
    public String venceMinutos(){
        String response = utils.response("min");
        String cadena = "Usted dispone de\\s+(?<minutos>(\\d+:\\d{2}:\\d{2}))\\s+MIN\\s+validos por\\s+(?<vence>(\\d+\\s+dias))";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("vence");
                return (result != null) ? result : "0 días";
        }
        return "";
    }
    
    // TODO: obtener bonos ilimitados 
    public String ilimitado(){
        String response = utils.response("bonos");
        String cadena = "Datos:\\s+(?<tipoDatos>[^\\s]+)\\s+vence\\s+(?<fechaVencimiento>\\d{2}-\\d{2}-\\d{2})\\.";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("tipoDatos");
            String result2 = matcher.group("fechaVencimiento");
                return (result != null) ? result + "\n" + result2 : "";
        }
        return "";
    }
    // TODO: obtener bonos de datos
    public String bonosDatos(){
        String response = utils.response("bonos");
        String cadena = "(Datos:\\s+(?:ilimitados\\s+vence\\s+)?(?:\\d{2}-\\d{2}-\\d{2}).?(?:\\s+)?(?<datos>(?:\\d+(?:\\.\\d+)?)(?:\\s)*[GMK]B)?(?:\\s+\\+\\s+)?(?<bonoLTE>(?:\\d+(?:\\.\\d+)?)(?:\\s)*[GMK]B)?\\s+vence\\s+(?<vence>\\d{2}-\\d{2}-\\d{2}))";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("datos");
            String result2 = matcher.group("vence");
                return (result != null) ? result + "\n" + result2 : "";
        }
        return "";
    }
    
    // TODO: obtener bonos de saldo
    public String bonosSaldo(){
        String response = utils.response("bonos");
        String cadena = "(\\$(?<bonoSaldo>([\\d.]+))\\s+vence\\s+(?<vence>(\\d{2}-\\d{2}-\\d{2})).)?";
        Pattern pattern = Pattern.compile(cadena);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String result = matcher.group("bonoSaldo");
            String result2 = matcher.group("vence");
                return (result != null) ? result + "\n" + result2 : "";
        }
        return "";
    }
}
