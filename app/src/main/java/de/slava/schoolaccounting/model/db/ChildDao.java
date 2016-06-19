package de.slava.schoolaccounting.model.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.BasicEntity;
import de.slava.schoolaccounting.model.Category;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Image;
import de.slava.schoolaccounting.model.Room;

/**
 * @author by V.Sysoltsev
 */
public class ChildDao extends BaseJPADao<Child> {
    public final static String COLUMN_ACTIVE = "ACTIVE";
    public final static String COLUMN_NAME = "NAME";
    public final static String COLUMN_ROOM_FK = "ROOM_FK";
    public final static String COLUMN_IMAGE_FK = "IMAGE_FK";
    public final static String COLUMN_CATEGORY_FK = "CATEGORY_FK";

    protected ChildDao(EntityManager entityManager, EntityManager.DBDaoKey key) {
        super(entityManager, key);
    }

    @Override
    protected String getTableName() {
        return EntityManager.DATABASE_TABLE_CHILD;
    }

    @Override
    protected ContentValues asCV(Child entity) {
        ContentValues ret = new ContentValues();
        ret.put(COLUMN_ID, entity.getId());
        ret.put(COLUMN_ACTIVE, entity.isActive());
        ret.put(COLUMN_NAME, entity.getNameFull());
        ret.put(COLUMN_ROOM_FK, entity.getRoom() != null ? entity.getRoom().getId() : null);
        ret.put(COLUMN_IMAGE_FK, entity.getImage() != null ? entity.getImage().getId() : null);
        ret.put(COLUMN_CATEGORY_FK, entity.getCategory() != null ? entity.getCategory().getId() : null);
        return ret;
    }

    private final static String[] columns = {
        COLUMN_ID, COLUMN_ACTIVE, COLUMN_NAME, COLUMN_ROOM_FK, COLUMN_IMAGE_FK, COLUMN_CATEGORY_FK
    };

    @Override
    protected String[] getColumns() {
        return columns;
    }

    @Override
    protected Child fromCursor(Cursor cursor) {
        Integer id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        Integer activeI = cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVE));
        boolean active = BasicEntity.i2b(activeI);
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        Integer roomFk = cursor.getInt(cursor.getColumnIndex(COLUMN_ROOM_FK)); // TODO: lazy loading? Maybe I should have used hibernate after all...
        Room room = null;
        if (roomFk != null) {
            room = getDao(RoomDao.class).getById(roomFk);
        }
        Integer imageFk = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_FK));
        Image image = null;
        if (imageFk != null) {
            image = getDao(ImageDao.class).getById(imageFk);
        }
        Integer categoryFk = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_FK));
        Category category = null;
        if (categoryFk != null) {
            category = getDao(CategoryDao.class).getById(categoryFk);
        }
        return new Child(id, active, name, room, image, category);

    }

    @Override
    void mergeInto(Child target, Child source) {
        assert target.getId() == source.getId();
        target.setActive(source.isActive());
        target.setNameFull(source.getNameFull());
        target.setRoom(source.getRoom());
        target.setImage(source.getImage());
        target.setCategory(source.getCategory());
    }

    public void moveEveryoneToInitialRoom() {
        Room initialRoom = getDao(RoomDao.class).findInitial();
        if (initialRoom == null) {
            Main.toast(R.string.childdao_error_noinitialroom);
            return;
        }
        List<Child> all = getAll(String.format("%s != ?", COLUMN_ROOM_FK), new String[] {initialRoom.getId().toString()}, null);
        for (Child child : all) {
            child.moveToToom(initialRoom);
        }
        Main.toast(R.string.childdao_all_children_moved_to_initial, initialRoom.getName());
    }
}
