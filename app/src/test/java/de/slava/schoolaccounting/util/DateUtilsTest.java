package de.slava.schoolaccounting.de.slava.schoolaccounting.util;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.slava.schoolaccounting.util.DateUtils;

import static junit.framework.TestCase.*;

/**
 * @author by V.Sysoltsev
 */
public class DateUtilsTest {
    @BeforeClass
    public static void setup() {
        Locale.setDefault(Locale.GERMAN);
    }

    @Test
    public void test_round_to_this_week() {
        Calendar c = new GregorianCalendar(2016, Calendar.APRIL, 17);
        Calendar rounded = DateUtils.roundTo(c, Calendar.DAY_OF_WEEK);
        assertEqualsCal(new GregorianCalendar(2016, Calendar.APRIL, 11), rounded);
        c.add(Calendar.DAY_OF_WEEK, 1);
        assertEqualsCal(new GregorianCalendar(2016, Calendar.APRIL, 18), DateUtils.roundTo(c, Calendar.DAY_OF_WEEK));
    }

    private void assertEqualsCal(Calendar expected, Calendar actual) {
        assertEquals(DateUtils.dateTimeToString(expected), DateUtils.dateTimeToString(actual));
    }
}
