package de.slava.schoolaccounting.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Basic observable
 *
 * @author by V.Sysoltsev
 */
public class BaseObservable {
    transient private final PropertyChangeSupport mPcs = new PropertyChangeSupport(this);

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
