package org.mfk.tools.sqlprint.agent.utils;

import java.util.regex.Pattern;

public class StringUtils {
    public static boolean containsDebug(String input) {
        return contains(input,"debug") ;
    }
    public static boolean contains(String input,String key) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        String[] parts = input.split(";");
        for (String part : parts) {
            if (key.equalsIgnoreCase(part.trim())) {
                return true;
            }
        }
        return false;
    }

}
