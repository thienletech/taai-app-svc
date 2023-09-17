package com.taai.app.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class TimeUtil {
    public static final String DATE_FORMAT = "yyyyMMdd";

    private TimeUtil() {
    }

    public static LocalDate parseDate(String date, String format) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
    }

    public static String formatDate(LocalDate date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    public static boolean isWeekDay(LocalDate date) {
        var dayOfWeek = date.getDayOfWeek();
        return !dayOfWeek.equals(DayOfWeek.SATURDAY) && !dayOfWeek.equals(DayOfWeek.SUNDAY);
    }

    public static List<LocalDate> getRecentWeekDays(int count) {
        return IntStream.rangeClosed(0, count - 1)
                .mapToObj(i -> LocalDate.now().minusDays(i))
                .filter(TimeUtil::isWeekDay)
                .toList();
    }
}
