package de.slava.schoolaccounting.model;

import android.util.Log;

import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.filter.FilterModel;
import de.slava.schoolaccounting.model.db.JournalDao;
import de.slava.schoolaccounting.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author by V.Sysoltsev
 */
@Accessors(chain=true)
public class Room extends BasicEntity {
    public static enum Name {
        ROOM_HOME(R.string.room_home),
        ROOM_MITTAGSBETREUUNG(R.string.room_mb),
        ROOM_111(R.string.room_111),
        ROOM_017(R.string.room_017),
        ROOM_018(R.string.room_018),
        ROOM_TURNHALLE(R.string.room_TH),
        ROOM_HOF(R.string.room_Hof);

        private final int roomResourceKey;

        Name(int roomResourceKey) {
            this.roomResourceKey = roomResourceKey;
        }

        public int getRoomResourceKey() {
            return roomResourceKey;
        }
    }

    public final static String PROPERTY_NAME = "name";
    public final static String PROPERTY_INITIAL = "initial";
    public final static String PROPERTY_PROTOCOL_ON_ENTRY = "protocolOnEntry";
    public final static String PROPERTY_CHILDREN = "children";

    private String name;
    private boolean initial;
    private boolean protocolOnEntry;
    private final Set<Child> children = new HashSet<>();

    public Room() {
        super();
    }

    public Room(Integer id, String name, boolean initial, boolean protocolOnEntry) {
        super(id);
        setName(name);
        setInitial(initial);
        setProtocolOnEntry(protocolOnEntry);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        super.firePropertyChange(PROPERTY_NAME, oldValue, name);
    }

    public boolean isInitial() {
        return initial;
    }

    public void setInitial(boolean initial) {
        boolean oldValue = this.initial;
        this.initial = initial;
        super.firePropertyChange(PROPERTY_INITIAL, oldValue, initial);
    }

    public boolean isProtocolOnEntry() {
        return protocolOnEntry;
    }

    public void setProtocolOnEntry(boolean protocolOnEntry) {
        boolean oldValue = this.protocolOnEntry;
        this.protocolOnEntry = protocolOnEntry;
        super.firePropertyChange(PROPERTY_PROTOCOL_ON_ENTRY, oldValue, protocolOnEntry);
    }

    public void addChild(Child child) {
        Log.d(Main.getTag(), String.format("Adding child %s to room %s", child, this));
        if (children.add(child)) {
            if (isProtocolOnEntry()) {
                Calendar now = GregorianCalendar.getInstance();
                Log.d(Main.getTag(), String.format("Protocol entrance of child %s to room %s on %s", child, this, DateUtils.dateTimeToString(now)));
                JournalDao jdao = getPersistingDao().getDao(JournalDao.class);
                jdao.upsert(child, this, now);
            }
            super.firePropertyChange(PROPERTY_CHILDREN, null, children);
        }
    }

    public void removeChild(Child child) {
        Log.d(Main.getTag(), String.format("Removing child %s from room %s", child, this));
        if (children.remove(child))
            super.firePropertyChange(PROPERTY_CHILDREN, null, children);
    }

    /**
     * Returns the number of children in the room, without filter
     * @return
     */
    public int getTotalChildrenCount() {
        return children.size();
    }

    public Set<Child> getChildrenFiltered(FilterModel filter) {
        Set<Child> ret = new HashSet<>();
        for (Child child : children) {
            if (child.isMatch(filter))
                ret.add(child);
        }
        return ret;
    }

    public void setChildren(Collection<Child> children) {
        this.children.clear();
        this.children.addAll(children);
    }

    @Override
    public String toString() {
        return String.format("%s{%d, %s, %b, children=%d}", getClass().getSimpleName(), getId(), getName(), isInitial(), children.size());
    }
}
