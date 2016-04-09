package de.slava.schoolaccounting.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.model.BasicEntity;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.JournalEntry;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.util.StringUtils;

import static de.slava.schoolaccounting.util.DateUtils.*;

/**
 * @author by V.Sysoltsev
 */
public class JournalDao extends BaseJPADao<JournalEntry> {
    public final static String COLUMN_CHILD_FK = "CHILD_FK";
    public final static String COLUMN_ROOM_FK = "ROOM_FK";
    public final static String COLUMN_TIME = "TIME";
    private final static String[] columns = {
        COLUMN_ID,
        COLUMN_CHILD_FK,
        COLUMN_ROOM_FK,
        COLUMN_TIME
    };

    public JournalDao(EntityManager entityManager, EntityManager.DBDaoKey key) {
        super(entityManager, key);
    }

    @Override
    protected String getTableName() {
        return EntityManager.DATABASE_TABLE_JOURNAL;
    }

    @Override
    protected ContentValues asCV(JournalEntry entry) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, entry.getId());
        cv.put(COLUMN_CHILD_FK, entry.getChild() != null ? entry.getChild().getId() : null);
        cv.put(COLUMN_ROOM_FK, entry.getRoom() != null ? entry.getRoom().getId() : null);
        cv.put(COLUMN_TIME, dateTimeToString(entry.getTimestamp()));
        return cv;
    }

    @Override
    protected String[] getColumns() {
        return columns;
    }

    @Override
    protected JournalEntry fromCursor(Cursor cursor) {
        Integer id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        Integer childFk = cursor.getInt(cursor.getColumnIndex(COLUMN_CHILD_FK));
        Child child = null;
        if (childFk != null) {
            ChildDao dao = getEntityManager().getDao(ChildDao.class);
            child = dao.getById(childFk);
        }
        Integer roomFk = cursor.getInt(cursor.getColumnIndex(COLUMN_ROOM_FK));
        Room room = null;
        if (roomFk != null) {
            RoomDao dao = getEntityManager().getDao(RoomDao.class);
            room = dao.getById(roomFk);
        }
        String dateString = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
        Calendar date = Calendar.getInstance();
        if (StringUtils.isBlank(dateString)) {
            try {
                Date d = dfDateTime.parse(dateString);
                date.setTime(d);
            } catch (ParseException e) {
                Log.w(Main.getTag(), String.format("%s with ID %d has unparseable %s (%s): %s", JournalEntry.class.getSimpleName(), id, COLUMN_TIME, dateString, e.getMessage()));
                date = null;
            }
        }
        return new JournalEntry(id, child, room, date);
    }

    @Override
    void mergeInto(JournalEntry target, JournalEntry source) {
        // should no be used actually
        assert target.getId() == source.getId();
        target.setChild(source.getChild());
        target.setRoom(source.getRoom());
        target.setTimestamp(source.getTimestamp());
    }

    @Override
    protected JournalEntry persist(JournalEntry persisted) {
        // We do not want to cache these.
        persisted.setEntityState(BasicEntity.State.PERSISTENT);
        persisted.setPersistingDao(this);
        return persisted;
    }

    /**
     * Returns the ID of existing entity (virtual, there is no entity for this) or null if not found
     * @param child
     * @param day
     * @return
     */
    public Integer findBySameDay(Child child, Room room, Calendar day) {
        final String columns [] = { COLUMN_ID };
        final Calendar from = new GregorianCalendar(day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH));
        final Calendar to = new GregorianCalendar(day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH) + 1); // relies on lenient = true by default
        final String args [] = {
                  Integer.toString(child.getId())
                , Integer.toString(room.getId())
                , dateToString(from)
                , dateToString(to)
        };
        try (Cursor c = getDatabase().query(getTableName(), columns, String.format("%s = ? and %s = ? and %s between ? and ?", COLUMN_CHILD_FK, COLUMN_ROOM_FK, COLUMN_TIME), args, null, null, null)) {
            if (c.moveToNext()) {
                return c.getInt(0);
            }
        }
        return null;
    }

    /**
     * Updates the table (upsert = insert/update)
     * @param child
     * @param timestamp
     */
    public JournalEntry upsert(Child child, Room room, Calendar timestamp) {
        Objects.requireNonNull(child);
        Objects.requireNonNull(timestamp);
        Integer exisitingId = findBySameDay(child, room, timestamp);
        JournalEntry entry = new JournalEntry(exisitingId, child, room, timestamp);
        return super.upsert(entry);
    }
}
