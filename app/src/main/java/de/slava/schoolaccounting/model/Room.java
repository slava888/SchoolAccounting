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
import de.slava.schoolaccounting.model.db.JournalDao;
import de.slava.schoolaccounting.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author by V.Sysoltsev
 */
@Accessors(chain=true)
public class Room extends BasicEntity {
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
        String oldValue = name;
        this.name = name;
        super.firePropertyChange("name", oldValue, name);
    }

    public boolean isInitial() {
        return initial;
    }

    public void setInitial(boolean initial) {
        boolean oldValue = initial;
        this.initial = initial;
        super.firePropertyChange("initial", oldValue, initial);
    }

    public boolean isProtocolOnEntry() {
        return protocolOnEntry;
    }

    public void setProtocolOnEntry(boolean protocolOnEntry) {
        boolean oldValue = protocolOnEntry;
        this.protocolOnEntry = protocolOnEntry;
        super.firePropertyChange("protocolOnEntry", oldValue, protocolOnEntry);
    }

    public void addChild(Child child) {
        Log.d(Main.getTag(), String.format("Adding child %s to room %s", child, this));
        children.add(child);
        if (isProtocolOnEntry()) {
            Calendar now = GregorianCalendar.getInstance();
            Log.d(Main.getTag(), String.format("Protocol entrance of child %s to room %s on %s", child, this, DateUtils.dateTimeToString(now)));
            JournalDao jdao = getPersistingDao().getDao(JournalDao.class);
            jdao.upsert(child, this, now);
        }
        super.firePropertyChange("children", null, children);
    }

    public void removeChild(Child child) {
        Log.d(Main.getTag(), String.format("Removing child %s from room %s", child, this));
        children.remove(child);
        super.firePropertyChange("children", null, children);
    }

    public Set<Child> getChildrenReadOnly() {
        return Collections.unmodifiableSet(children);
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
