package de.slava.schoolaccounting.model.db;

import android.content.ContentValues;
import android.database.Cursor;

import de.slava.schoolaccounting.model.Category;
import de.slava.schoolaccounting.model.Image;
import de.slava.schoolaccounting.model.Room;

/**
 * @author by V.Sysoltsev
 */
public class CategoryDao extends BaseJPADao<Category> {
    public final static String TABLE_NAME = "CATEGORY";
    public final static String COLUMN_NAME = "NAME";
    public final static String COLUMN_IMAGE_FK = "IMAGE_FK";

    public CategoryDao(EntityManager entityManager, EntityManager.DBDaoKey key) {
        super(entityManager, key);
    }

    @Override
    protected ContentValues asCV(Category entity) {
        ContentValues ret = new ContentValues();
        ret.put(COLUMN_ID, entity.getId());
        ret.put(COLUMN_NAME, entity.getName());
        ret.put(COLUMN_IMAGE_FK, entity.getImage() != null ? entity.getImage().getId() : null);
        return ret;
    }

    private final static String[] columns = {
        COLUMN_ID, COLUMN_NAME, COLUMN_IMAGE_FK
    };

    @Override
    protected String[] getColumns() {
        return columns;
    }

    @Override
    protected Category fromCursor(Cursor cursor) {
        Integer id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        Integer imageFk = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_FK));
        Image image = null;
        if (imageFk != null) {
            image = getDao(ImageDao.class).getById(imageFk);
        }
        return new Category(id, name, image);
    }

    @Override
    void mergeInto(Category target, Category source) {
        assert target.getId() == source.getId();
        target.setName(source.getName());
        target.setImage(source.getImage());
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }
}
