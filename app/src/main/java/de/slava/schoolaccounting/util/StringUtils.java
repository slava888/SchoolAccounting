package de.slava.schoolaccounting.util;

/**
 * @author by V.Sysoltsev
 */
public class StringUtils {
    /**
     * Returns true if the string is null or contains only blanks
     * @param s
     * @return
     */
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
