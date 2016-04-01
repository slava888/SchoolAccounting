package de.slava.schoolaccounting.dnd;

/**
 * Interface to be notified when DND state of an DND object changes.
 *
 * @author by V.Sysoltsev
 */
// @FunctionalInterface
public interface DNDStateListener<T> {

    public void onStateChanges(DNDObject<T> object, int oldState, int newState);
}
