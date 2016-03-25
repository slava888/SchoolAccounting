package de.slava.schoolaccounting.model.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Room;

/**
 * @author by V.Sysoltsev
 */
public class RoomDao extends BaseDao<Room> {

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
        ret.put("ID", room.getId());
        ret.put("NAME", room.getName());
        ret.put("INITIAL", room.isInitial());
        return ret;
    }

    private final static String[] columns = {
        "ID", "NAME", "INITIAL"
    };

    @Override
    protected String[] getColumns() {
        return columns;
    }

    @Override
    protected Room fromCursor(Cursor cursor) {
        Integer id = cursor.getInt(cursor.getColumnIndex("ID"));
        String name = cursor.getString(cursor.getColumnIndex("NAME"));
        Integer initial = cursor.getInt(cursor.getColumnIndex("INITIAL"));
        return new Room(id, name, initial != null && initial != 0);
    }

    @Override
    protected Room fetchRelations(Room entity) {
        entity = super.fetchRelations(entity);
        List<Child> children = childDao.getAll(String.format("ROOM_FK=%d", entity.getId()), "NAME ASC");
        entity.setChildren(children);
        return entity;
    }

    @Override
    void mergeInto(Room target, Room source) {
        assert target.getId() == source.getId();
        target.setName(source.getName());
        target.setInitial(source.isInitial());
    }
}
