package com.sixthsolution.apex.nlp.util;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public final class NumericUtils {
    //TODO support arabic digits
    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static int toInt(String str) {
        return Integer.parseInt(str);
    }
}
