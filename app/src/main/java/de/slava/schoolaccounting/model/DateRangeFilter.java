package de.slava.schoolaccounting.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.slava.schoolaccounting.util.DateUtils;

/**
 * @author by V.Sysoltsev
 */
public class DateRangeFilter extends BaseObservable {
    private Calendar from;
    private Calendar to;

    public DateRangeFilter() {
    }

    public DateRangeFilter(Calendar from, Calendar to) {
        this.from = from;
        this.to = to;
    }

    public Calendar getFrom() {
        return from;
    }

    public void setFrom(Calendar from) {
        Calendar oldValue = this.from;
        this.from = from;
        super.firePropertyChange("from", oldValue, from);
    }

    public Calendar getTo() {
        return to;
    }

    public void setTo(Calendar to) {
        Calendar oldValue = this.to;
        this.to = to;
        super.firePropertyChange("to", oldValue, to);
    }

    @Override
    public String toString() {
        return String.format("%s{%s - %s}", DateRangeFilter.class.getSimpleName(), DateUtils.dateToString(from), DateUtils.dateToString(to));
    }

}
