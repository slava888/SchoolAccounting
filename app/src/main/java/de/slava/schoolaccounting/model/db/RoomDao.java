package de.slava.schoolaccounting.model.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

import de.slava.schoolaccounting.model.BasicEntity;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Room;

/**
 * @author by V.Sysoltsev
 */
public class RoomDao extends BaseJPADao<Room> {
    public final static String COLUMN_NAME = "NAME";
    public final static String COLUMN_INITIAL = "INITIAL";
    public final static String COLUMN_PROTOCOL_ON_ENTRY = "PROTOCOL_ON_ENTRY";

    private ChildDao childDao = getEntityManager().getDao(ChildDao.class);

    protected RoomDao(EntityManager entityManager, EntityManager.DBDaoKey key) {
        super(entityManager, key);
    }

    @Override
    protected String getTableName() {
        return EntityManager.DATABASE_TABLE_ROOM;
    }

    @Override
    protected ContentValues asCV(Room room) {
        ContentValues ret = new ContentValues();
        ret.put(COLUMN_ID, room.getId());
        ret.put(COLUMN_NAME, room.getName());
        ret.put(COLUMN_INITIAL, room.isInitial());
        ret.put(COLUMN_PROTOCOL_ON_ENTRY, room.isProtocolOnEntry());
        return ret;
    }

    private final static String[] columns = {
        COLUMN_ID, COLUMN_NAME, COLUMN_INITIAL, COLUMN_PROTOCOL_ON_ENTRY
    };

    @Override
    protected String[] getColumns() {
        return columns;
    }

    @Override
    protected Room fromCursor(Cursor cursor) {
        Integer id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        Integer initial = cursor.getInt(cursor.getColumnIndex(COLUMN_INITIAL));
        Integer protocolOnEntry = cursor.getInt(cursor.getColumnIndex(COLUMN_PROTOCOL_ON_ENTRY));
        return new Room(id, name, BasicEntity.i2b(initial), BasicEntity.i2b(protocolOnEntry));
    }

    @Override
    protected Room fetchRelations(Room entity) {
        entity = super.fetchRelations(entity);
        List<Child> children = childDao.getAll(String.format("%s=%d", ChildDao.COLUMN_ROOM_FK, entity.getId()), String.format("%s ASC", ChildDao.COLUMN_NAME));
        entity.setChildren(children);
        return entity;
    }

    @Override
    void mergeInto(Room target, Room source) {
        assert target.getId() == source.getId();
        target.setName(source.getName());
        target.setInitial(source.isInitial());
        target.setProtocolOnEntry(source.isProtocolOnEntry());
    }
}
