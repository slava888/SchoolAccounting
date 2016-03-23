package de.slava.schoolaccounting.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import lombok.EqualsAndHashCode;

/**
 * @author by V.Sysoltsev
 */
public class BasicEntity implements Serializable {
    transient private final PropertyChangeSupport mPcs = new PropertyChangeSupport(this);
    private Integer id;

    public BasicEntity() {
        this(null);
    }

    public BasicEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void addChangeListener(PropertyChangeListener listener) {
        mPcs.addPropertyChangeListener(listener);
    }

    public void removeChangeListener(PropertyChangeListener listener) {
        mPcs.removePropertyChangeListener(listener);
    }

    public <T> void firePropertyChange(String propertyName, T oldValue, T newValue) {
        mPcs.firePropertyChange(propertyName, oldValue, newValue);
    }
}
