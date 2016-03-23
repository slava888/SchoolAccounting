package de.slava.schoolaccounting.model.db;

import android.content.ContentValues;
import android.database.Cursor;

import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Room;

/**
 * @author by V.Sysoltsev
 */
public class ChildDao extends BaseDao<Child> {

    protected ChildDao(DB db, DB.DBDaoKey key) {
        super(db, key);
    }

    @Override
    protected String getTableName() {
        return "CHILD";
    }

    @Override
    protected ContentValues asCV(Child room) {
        ContentValues ret = new ContentValues();
        ret.put("ID", room.getId());
        ret.put("NAME", room.getNameFull());
        ret.put("ROOM_FK", room.getRoom() != null ? room.getRoom().getId() : null);
        ret.put("IMAGE_FK", room.getImageId());
        return ret;
    }

    private final static String[] columns = {
        "ID", "NAME", "ROOM_FK", "IMAGE_FK"
    };

    @Override
    protected String[] getColumns() {
        return columns;
    }

    @Override
    protected Child fromCursor(Cursor cursor) {
        Integer id = cursor.getInt(cursor.getColumnIndex("ID"));
        String name = cursor.getString(cursor.getColumnIndex("NAME"));
        Integer roomFk = cursor.getInt(cursor.getColumnIndex("ROOM_FK")); // TODO: lazy loading? Maybe I should have used hibernate after all...
        Room room = null;
        if (roomFk != null) {
            RoomDao roomDao = getDb().getDao(RoomDao.class);
            room = roomDao.getById(roomFk);
        }
        Integer imageFk = cursor.getInt(cursor.getColumnIndex("IMAGE_FK"));
        return new Child(id, name, room, imageFk);

    }

    @Override
    void mergeInto(Child target, Child source) {
        assert target.getId() == source.getId();
        target.setNameFull(source.getNameFull());
        target.setRoom(source.getRoom());
        target.setImageId(source.getImageId());
    }
}
