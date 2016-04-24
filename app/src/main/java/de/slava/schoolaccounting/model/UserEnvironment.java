package de.slava.schoolaccounting.model;

import de.slava.schoolaccounting.AccessRight;

/**
 * Environment of the user.
 * Contains: current access rights, etc
 *
 * @author by V.Sysoltsev
 */
public class UserEnvironment extends BaseObservable {
    public final static String PROPERTY_ACCESS_RIGHT = "accessRight";

    /**
     * Current access rights of the user
     */
    private AccessRight accessRight = AccessRight.USER;

    private static UserEnvironment instance = new UserEnvironment();

    private UserEnvironment() {
    }

    public static UserEnvironment instance() {
        return instance;
    }

    public AccessRight getAccessRight() {
        return accessRight;
    }

    public void setAccessRight(AccessRight accessRight) {
        AccessRight oldValue = this.accessRight;
        this.accessRight = accessRight;
        super.firePropertyChange(PROPERTY_ACCESS_RIGHT, oldValue, accessRight);
    }
}
