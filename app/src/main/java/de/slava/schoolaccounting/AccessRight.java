package de.slava.schoolaccounting;

/**
 * User current access right
 *
 * @author by V.Sysoltsev
 */
public enum AccessRight {
    USER,
    ADMIN,
    DEV;

    public boolean higherThan(AccessRight other) {
        return ordinal() > other.ordinal();
    }

    public boolean higherOrEqualsThan(AccessRight other) {
        return ordinal() >= other.ordinal();
    }

    /**
     * Return highest right from the list
     * @param values
     * @return
     */
    public static AccessRight highestOf(AccessRight... values) {
        AccessRight ret = USER;
        if (values != null) {
            for (AccessRight right : values) {
                if (right.ordinal() > ret.ordinal())
                    ret = right;
            }
        }
        return ret;
    }
}
