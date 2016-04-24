package de.slava.schoolaccounting.model.db;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Room;

/**
 * @author by V.Sysoltsev
 */
public class ChildDao extends BaseJPADao<Child> {
    public final static String COLUMN_NAME = "NAME";
    public final static String COLUMN_ROOM_FK = "ROOM_FK";
    public final static String COLUMN_IMAGE_FK = "IMAGE_FK";

    protected ChildDao(EntityManager entityManager, EntityManager.DBDaoKey key) {
        super(entityManager, key);
    }

    @Override
    protected String getTableName() {
        return "CHILD";
    }

    @Override
    protected ContentValues asCV(Child room) {
        ContentValues ret = new ContentValues();
        ret.put(COLUMN_ID, room.getId());
        ret.put(COLUMN_NAME, room.getNameFull());
        ret.put(COLUMN_ROOM_FK, room.getRoom() != null ? room.getRoom().getId() : null);
        ret.put(COLUMN_IMAGE_FK, room.getImageId());
        return ret;
    }

    private final static String[] columns = {
        COLUMN_ID, COLUMN_NAME, COLUMN_ROOM_FK, COLUMN_IMAGE_FK
    };

    @Override
    protected String[] getColumns() {
        return columns;
    }

    @Override
    protected Child fromCursor(Cursor cursor) {
        Integer id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        Integer roomFk = cursor.getInt(cursor.getColumnIndex(COLUMN_ROOM_FK)); // TODO: lazy loading? Maybe I should have used hibernate after all...
        Room room = null;
        if (roomFk != null) {
            RoomDao roomDao = getEntityManager().getDao(RoomDao.class);
            room = roomDao.getById(roomFk);
        }
        Integer imageFk = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_FK));
        return new Child(id, name, room, imageFk);

    }

    @Override
    void mergeInto(Child target, Child source) {
        assert target.getId() == source.getId();
        target.setNameFull(source.getNameFull());
        target.setRoom(source.getRoom());
        target.setImageId(source.getImageId());
    }

    public void moveEveryoneToInitialRoom() {
        List<Room> initialRooms = getDao(RoomDao.class).getAll("INITIAL = 1", null, null);
        if (initialRooms == null || initialRooms.isEmpty()) {
            Main.toast(R.string.childdao_error_noinitialroom);
            return;
        }
        Room initialRoom = initialRooms.get(0);
        List<Child> all = getAll(String.format("%s != ?", COLUMN_ROOM_FK), new String[] {initialRoom.getId().toString()}, null);
        for (Child child : all) {
            child.moveToToom(initialRoom);
        }
        Main.toast(R.string.childdao_all_children_moved_to_initial, initialRoom.getName());
    }
}
