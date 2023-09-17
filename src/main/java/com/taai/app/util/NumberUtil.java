package com.taai.app.util;

public class NumberUtil {

    private NumberUtil() {
    }

    public static boolean isNullOrZero(Number number) {
        return number == null || number.doubleValue() == 0;
    }

}