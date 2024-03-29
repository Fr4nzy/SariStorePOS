package com.projectfkklp.saristorepos.utils;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class StringUtils {
    public static int getLinesCount(String str){
        return str.split("\n").length;
    }

    public static String formatToPesoWithMetricPrefix(float number){
        return "₱"+formatWithMetricPrefix((long)number);
    }

    public static String formatToPeso(float number){
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return "₱"+decimalFormat.format(number);
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String formatWithMetricPrefix(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatWithMetricPrefix(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatWithMetricPrefix(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static boolean isNullOrEmpty(String str) {   return str == null || str.isEmpty();    }

    public static String getRandomGreeting() {
        String[] GREETINGS = {
                "Welcome back to SariStore POS!",
                "Hello! \nReady to explore SariStore POS?",
                "Great to see you again! Let's get started with SariStore POS.",
                "Hi there! \nLogin to access SariStore POS.",
                "Greetings! \nSign in to your SariStore POS account.",
                "Welcome aboard! \nLogin now to experience SariStore POS.",
                "Hello, traveler! \nYour journey with SariStore POS begins here.",
                "Welcome back! \nYour adventure with SariStore POS continues.",
                "Hey! \nIt's SariStore POS time. Sign in to begin.",
                "Good to have you here! \nLet's dive into SariStore POS together."
        };

        Random random = new Random();
        int index = random.nextInt(GREETINGS.length);
        return GREETINGS[index];
    }

}
