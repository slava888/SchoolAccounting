package de.slava.schoolaccounting.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import de.slava.schoolaccounting.functional.Consumer;

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
     * Converts calendar to String (contains date only)
     * @param cal
     * @param df
     * @return
     */
    public static String dateToString(Calendar cal, DateFormat df) {
        return cal != null ? df.format(cal.getTime()) : null;
    }

    /**
     * Converts calendar to String (contains date and time)
     * @param cal
     * @return
     */
    public static String dateTimeToString(Calendar cal) {
        return cal != null ? dfDateTime.format(cal.getTime()) : null;
    }

    private static class CalendarClearer {
        public int field;
        public Consumer<Calendar> clearer;

        public CalendarClearer(int field, Consumer<Calendar> clearer) {
            this.field = field;
            this.clearer = clearer;
        }
    }

    private static final CalendarClearer calendarFields [] = {
            new CalendarClearer(Calendar.MILLISECOND, cal -> cal.set(Calendar.MILLISECOND, 0)),
            new CalendarClearer(Calendar.SECOND, cal -> cal.set(Calendar.SECOND, 0)),
            new CalendarClearer(Calendar.MINUTE, cal -> cal.set(Calendar.MINUTE, 0)),
            new CalendarClearer(Calendar.HOUR_OF_DAY, cal -> cal.set(Calendar.HOUR_OF_DAY, 0)),
            new CalendarClearer(Calendar.DAY_OF_WEEK, cal -> cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)),
            new CalendarClearer(Calendar.DAY_OF_MONTH, cal -> cal.set(Calendar.DAY_OF_MONTH, 0)),
            new CalendarClearer(Calendar.MONTH, cal -> cal.set(Calendar.MONTH, 0)),
            new CalendarClearer(Calendar.YEAR, cal -> cal.set(Calendar.YEAR, 0))
    };

    /**
     * Returns a new calendar as source rounded to the field.
     * Possible field values:
     *   Calendar.MILLISECOND,
     *   Calendar.SECOND,
     *   Calendar.MINUTE,
     *   Calendar.HOUR_OF_DAY,
     *   Calendar.DAY_OF_WEEK_IN_MONTH,
     *   Calendar.DAY_OF_MONTH,
     *   Calendar.MONTH,
     *   Calendar.YEAR,
     *
     * @param source
     * @param field
     * @return
     */
    public static Calendar roundTo(Calendar source, int field) {
        Calendar ret = GregorianCalendar.getInstance();
        ret.setTimeInMillis(source.getTimeInMillis());
        for (int i = 0 ; i < calendarFields.length; i++) {
            CalendarClearer cl = calendarFields[i];
            if (cl.field == field) {
                ret = ret;
            }
            cl.clearer.accept(ret);
            if (cl.field == field)
                return ret;
        }
        throw new RuntimeException("Field is not supported for rounding");
        // return ret;
    }

    @Deprecated
    public static void setMonday(Calendar cal) {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                break;
            case Calendar.TUESDAY:
                cal.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case Calendar.WEDNESDAY:
                cal.add(Calendar.DAY_OF_MONTH, -2);
                break;
            case Calendar.THURSDAY:
                cal.add(Calendar.DAY_OF_MONTH, -3);
                break;
            case Calendar.FRIDAY:
                cal.add(Calendar.DAY_OF_MONTH, -4);
                break;
            case Calendar.SATURDAY:
                cal.add(Calendar.DAY_OF_MONTH, -5);
                break;
            case Calendar.SUNDAY:
                cal.add(Calendar.DAY_OF_MONTH, -7);
                break;
        }
    }

    /**
     * Returns a new calendar built from source calendar plus offset.
     *
     * @param source
     * @param field
     * @param value
     * @return
     */
    public static Calendar thisPlus(Calendar source, int field, int value) {
        Calendar ret = GregorianCalendar.getInstance();
        ret.setTimeInMillis(source.getTimeInMillis());
        ret.add(field, value);
        return ret;
    }

    public static Calendar fromDate(Date date) {
        Calendar ret = GregorianCalendar.getInstance();
        if (date != null)
            ret.setTime(date);
        return ret;
    }

    /**
     * Returns true if the date refers to the same day
     *
     * @param one
     * @param two
     * @return
     */
    public static boolean isSameDay(Date one, Date two) {
        return dfDate.format(one).equals(dfDate.format(two));
    }
}
