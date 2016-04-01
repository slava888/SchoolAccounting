package de.slava.schoolaccounting.dnd;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DND Object wrapping the business object with drag listeners being able to follow drag states.
 *
 * @author by V.Sysoltsev
 */
public class DNDObject <T> {
    private final Class<T> objectClass;
    private final T object;
    private int dragState = 0;
    private Set<DNDStateListener<T>> listeners = new HashSet<>();

    public DNDObject(Class<T> objectClass) {
        this.objectClass = objectClass;
        this.object = null;
    }

    public DNDObject(T object) {
        Objects.requireNonNull(object);
        this.objectClass = (Class<T>)object.getClass();
        this.object = object;
    }

    /**
     * Returns the wrapped object, may be null
     * @return
     */
    public T getObject() {
        return object;
    }


    /**
     * Returns the class of wrapped object
     * @return
     */
    public Class<T> getObjectClass() {
        return objectClass;
    }

    /***
     * Allows to register an listener to DND state
     * @param listener
     */
    public void addDragStateListener(DNDStateListener<T> listener) {
        listeners.add(listener);
    }

    /**
     * Unregister the listener
     * @param listener
     */
    public void removeDragStateListener(DNDStateListener<T> listener) {
        listeners.remove(listener);
    }

    /*
    Called when DND state is changed and the listeners are to be notified
     */
    public void updateState(int newState) {
        if (this.dragState != newState) {
            int oldState = this.dragState;
            this.dragState = newState;
            notifyListeners(oldState, newState);
        }
    }

    protected void notifyListeners(int oldState, int newState) {
        for (DNDStateListener<T> listener : listeners) {
            listener.onStateChanges(this, oldState, newState);
        }
    }

    public static boolean isInstanceOf(Object dndObject, Class<?> clazz) {
        return dndObject instanceof DNDObject && clazz.isAssignableFrom(((DNDObject<?>)dndObject).getObjectClass());
    }

    public int getDragState() {
        return dragState;
    }
}
