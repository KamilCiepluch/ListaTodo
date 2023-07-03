package com.example.listapplication_final;

import android.icu.text.SimpleDateFormat;
import android.net.ParseException;

import java.util.Date;

public class TimeCalculator {

    public static long calculateTimeDifference(String dateString, String offset) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Date currentDate = new Date(); // Obecna data i czas
        try {
            Date targetDate = format.parse(dateString); // Konwertowanie daty z String na obiekt Date
            long differenceInMillis = targetDate.getTime() -
                    convertTimeToMilliseconds(offset)  - currentDate.getTime()  ; // Różnica w milisekundach
            return differenceInMillis;
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private static long convertTimeToMilliseconds(String timeString) {
        long milliseconds = 0;
        try {
            int value = Integer.parseInt(timeString.replaceAll("[^0-9]", "")); // Pobranie liczbowej wartości z ciągu znaków
            if (timeString.contains("min")) {
                milliseconds =  value * 60L * 1000L; // Przeliczenie minut na milisekundy
            } else if (timeString.contains("h")) {
                milliseconds =  value * 60L * 60L * 1000L; // Przeliczenie godzin na milisekundy
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }
}
