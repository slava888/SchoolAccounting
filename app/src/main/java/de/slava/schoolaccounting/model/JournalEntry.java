package de.slava.schoolaccounting.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.slava.schoolaccounting.util.DateUtils;

/**
 * @author by V.Sysoltsev
 */
public class JournalEntry extends BasicEntity {
    private Child child;
    private Room room;
    private Calendar timestamp;

    public JournalEntry() {
        super();
    }

    public JournalEntry(Integer id, Child child, Room room, Calendar timestamp) {
        super(id);
        this.child = child;
        this.room = room;
        this.timestamp = timestamp;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        Child oldValue = this.child;
        this.child = child;
        super.firePropertyChange("child", oldValue, child);
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        Room oldValue = this.room;
        this.room = room;
        super.firePropertyChange("room", oldValue, room);
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    private final static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public String getTimestampString() {
        return getTimestamp() != null ? DateUtils.dateToString(getTimestamp(), df) : null;
    }

    public void setTimestamp(Calendar timestamp) {
        Calendar oldValue = this.timestamp;
        this.timestamp = timestamp;
        super.firePropertyChange("timestamp", oldValue, timestamp);
    }

    @Override
    public String toString() {
        return String.format("%s{%s, %s}", getClass().getSimpleName(), getChild(), getTimestampString());
    }
}
