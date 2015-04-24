package com.vsu.amm;

/**
 * Created by Nikita Skornyakov on 19.04.2015.
 */
public class Utils {

    /**
     * Check if string is null or empty
     *
     * @param str string to check
     * @return true if {@code str} is null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        if (str == null)
            return true;
        return "".equals(str);
    }


}
