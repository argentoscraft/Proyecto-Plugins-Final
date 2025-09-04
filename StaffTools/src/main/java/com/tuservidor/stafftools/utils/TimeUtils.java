package com.tuservidor.stafftools.utils;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {
    public static long parseTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return -1;
        }
        
        // Expresión regular para capturar números y sus unidades (d, h, m, s)
        Pattern pattern = Pattern.compile("(\\d+)([dhms])");
        Matcher matcher = pattern.matcher(timeString.toLowerCase());

        long totalMillis = 0;
        boolean matchFound = false;

        while (matcher.find()) {
            matchFound = true;
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "d":
                    totalMillis += TimeUnit.DAYS.toMillis(value);
                    break;
                case "h":
                    totalMillis += TimeUnit.HOURS.toMillis(value);
                    break;
                case "m":
                    totalMillis += TimeUnit.MINUTES.toMillis(value);
                    break;
                case "s":
                    totalMillis += TimeUnit.SECONDS.toMillis(value);
                    break;
            }
        }

        return matchFound ? totalMillis : -1;
    }
}