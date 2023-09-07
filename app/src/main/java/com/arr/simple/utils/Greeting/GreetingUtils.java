package com.arr.simple.utils.Greeting;

import java.util.Calendar;


public class GreetingUtils {

    // TODO: Cargar saludo según la hora del día
    public static String hello() {
        String greeting;
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        if (hora < 12) {
            greeting = "Buenos días";
        } else if (hora < 18) {
            greeting = "Buenas tardes";
        } else {
            greeting = "Buenas noches";
        }
        return greeting;
    }
}
