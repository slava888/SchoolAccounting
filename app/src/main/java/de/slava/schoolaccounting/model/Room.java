package de.slava.schoolaccounting.model;

import android.util.Log;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.slava.schoolaccounting.Main;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author by V.Sysoltsev
 */
@Accessors(chain=true)
public class Room extends BasicEntity {
    private String name;
    private boolean initial;
    private final Set<Child> children = new HashSet<>();

    public Room() {
        super();
    }

    public Room(Integer id, String name, boolean initial) {
        super(id);
        setName(name);
        setInitial(initial);
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

    public void addChild(Child child) {
        Log.d(Main.getTag(), String.format("Adding child %s to room %s", child, this));
        children.add(child);
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
