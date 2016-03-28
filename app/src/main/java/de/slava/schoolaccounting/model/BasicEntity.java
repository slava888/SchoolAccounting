package de.slava.schoolaccounting.model;

import android.util.Log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.model.db.BaseDao;

/**
 * @author by V.Sysoltsev
 */
public abstract class BasicEntity implements Serializable {

    public static enum State {
        TRANSIENT, // entity is not bound with entity manager
        PERSISTENT // entity is bound with entity manager
    }
    private State entityState;
    private BaseDao persistingDao;

    transient private final PropertyChangeSupport mPcs = new PropertyChangeSupport(this);
    private Integer id;

    public BasicEntity() {
        this(null);
    }

    public BasicEntity(Integer id) {
        this.id = id;
        entityState = State.TRANSIENT;
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

    public State getEntityState() {
        return entityState;
    }

    public void setEntityState(State entityState) {
        this.entityState = entityState;
    }

    public BaseDao getPersistingDao() {
        return persistingDao;
    }

    public void setPersistingDao(BaseDao persistingDao) {
        this.persistingDao = persistingDao;
    }

    /**
     * Returns true if the entity is not yet associate with persistence context
     * @return
     */
    public boolean isTransient() {
        return getEntityState() == State.TRANSIENT;
    }

    /**
     * Returns true if the entity is associated with persistence context
     * @return
     */
    public boolean isPersistent() {
        return getEntityState() == State.PERSISTENT;
    }

}
