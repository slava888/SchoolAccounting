package de.slava.schoolaccounting.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by V.Sysoltsev
 */
public enum TriBoolean {
    UNKNOWN(-1, null),
    TRUE(1, true),
    FALSE(0, false);

    private final int asInt;
    private final Boolean asBoolean;

    TriBoolean(int asInt, Boolean asBoolean) {
        this.asInt = asInt;
        this.asBoolean = asBoolean;
    }

    /**
     * Cycles tri-boolean state
     * @return
     */
    public TriBoolean cycle() {
        switch (this) {
            case UNKNOWN: return TRUE;
            case TRUE: return FALSE;
            case FALSE: return UNKNOWN;
        }
        assert false : "Unhandled tri-boolean state";
        return UNKNOWN;
    }

    public int toInt() {
        return asInt;
    }

    public Boolean toBoolean() {
        return asBoolean;
    }

    private final static Map<Integer, TriBoolean> int2b = new HashMap<>();
    private final static Map<Boolean, TriBoolean> bool2b = new HashMap<>();
    static {
        for (TriBoolean tb : TriBoolean.values()) {
            if (tb != UNKNOWN) {
                int2b.put(tb.toInt(), tb);
                bool2b.put(tb.toBoolean(), tb);
            }
        }
    }

    public static TriBoolean fromInt(int value) {
        TriBoolean ret = int2b.get(value);
        return ret != null ? ret : UNKNOWN;
    }

    public static TriBoolean fromBoolean(int value) {
        TriBoolean ret = bool2b.get(value);
        return ret != null ? ret : UNKNOWN;
    }

}
