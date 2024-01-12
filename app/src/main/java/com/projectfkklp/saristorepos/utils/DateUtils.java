package com.projectfkklp.saristorepos.utils;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
    );

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
    );
    public static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat(
            "dd-MMM",
            Locale.getDefault()
    );

    public static Date addDays(Date date, int daysToAdd){
        // Create a Calendar object and set it to the current date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Add daysToAdd days
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);

        // Return updated Date
        return calendar.getTime();
    }

    @NonNull
    public static String formatTimestamp(Date date) {
        return TIMESTAMP_FORMAT.format(date);
    }

    @NonNull
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static Date parse(String str) throws ParseException {
        return TIMESTAMP_FORMAT.parse(str);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        return localDate1.isEqual(localDate2);
    }
    public static long calculateDaysDifference(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(localDate1, localDate2);
    }

}
