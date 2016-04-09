package de.slava.schoolaccounting.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author by V.Sysoltsev
 */
public class DateUtils {
    public final static SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Converts calendar to String (contains date only)
     * @param cal
     * @return
     */
    public static String dateToString(Calendar cal) {
        return cal != null ? dfDate.format(cal.getTime()) : null;
    }

    /**
     * Converts calendar to String (contains date and time)
     * @param cal
     * @return
     */
    public static String dateTimeToString(Calendar cal) {
        return cal != null ? dfDateTime.format(cal.getTime()) : null;
    }

}
