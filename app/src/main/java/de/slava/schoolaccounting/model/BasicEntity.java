package de.slava.schoolaccounting.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import de.slava.schoolaccounting.model.db.BaseJPADao;

/**
 * @author by V.Sysoltsev
 */
public abstract class BasicEntity extends BaseObservable implements Serializable {

    public static enum State {
        TRANSIENT, // entity is not bound with entity manager
        PERSISTENT // entity is bound with entity manager
    }
    private State entityState;
    private BaseJPADao persistingDao;

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

    public State getEntityState() {
        return entityState;
    }

    public void setEntityState(State entityState) {
        this.entityState = entityState;
    }

    public BaseJPADao getPersistingDao() {
        return persistingDao;
    }

    public void setPersistingDao(BaseJPADao persistingDao) {
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

    /**
     * Converts integer to boolean. Null is treated as false.
     * @param i
     * @return
     */
    public static boolean i2b(Integer i) {
        return i != null && i != 0;
    }

}
