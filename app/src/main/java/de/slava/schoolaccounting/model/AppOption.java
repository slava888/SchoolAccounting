package de.slava.schoolaccounting.model;

/**
 * @author by V.Sysoltsev
 */
public enum AppOption {
    /**
     * stores the room, which was opened last time
     */
    LAST_VIEWED_ROOM,

    /**
     * The last day, when the children were reset to default room
     */
    LAST_DAY_RESET;

    private final String specificOptionNameInDb;

    AppOption() {
        this(null);
    }

    AppOption(String optionNameInDb) {
        this.specificOptionNameInDb = optionNameInDb;
    }

    public String getOptionNameInDb() {
        return specificOptionNameInDb != null ? specificOptionNameInDb : name();
    }
}
