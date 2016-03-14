package de.slava.schoolaccounting.model;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author by V.Sysoltsev
 */
@AllArgsConstructor(suppressConstructorProperties=true)
@Accessors(chain=true)
public class Room extends BasicEntity {
    private Integer id;
    private String name;
    private final Set<Scholar> scholars = new HashSet<>();

    public Room() {
        PropertyChangeListener l = (event) -> {};
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldValue = name;
        this.name = name;
        super.firePropertyChange("name", oldValue, name);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        Integer oldValue = id;
        this.id = id;
        super.firePropertyChange("id", oldValue, id);
    }

    public void addScholar(Scholar scholar) {
        scholars.add(scholar);
        super.firePropertyChange("scholars", scholars, scholars);
    }

    public void removeScholar(Scholar scholar) {
        scholars.remove(scholar);
        super.firePropertyChange("scholars", scholars, scholars);
    }

    public Set<Scholar> getScholarsReadOnly() {
        return Collections.unmodifiableSet(scholars);
    }

    @Override
    public String toString() {
        return String.format("%s{name=%s, scholars=%d}", getClass().getSimpleName(), getName(), scholars.size());
    }
}
