package de.slava.schoolaccounting.model.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

import de.slava.schoolaccounting.model.BasicEntity;
import de.slava.schoolaccounting.model.Image;

/**
 * @author by V.Sysoltsev
 */
public class ImageDao extends BaseJPADao<Image> {
    public final static String TABLE_NAME = "IMAGE";
    public final static String COLUMN_SID = "SID";
    public final static String COLUMN_USAGE_CATEGORY = "USAGE_CATEGORY";
    public final static String COLUMN_USAGE_PERSON = "USAGE_PERSON";

    public ImageDao(EntityManager entityManager, EntityManager.DBDaoKey key) {
        super(entityManager, key);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected ContentValues asCV(Image entity) {
        ContentValues ret = new ContentValues();
        ret.put(COLUMN_ID, entity.getId());
        ret.put(COLUMN_SID, entity.getSid().name());
        ret.put(COLUMN_USAGE_CATEGORY, entity.isUsageCategory());
        ret.put(COLUMN_USAGE_PERSON, entity.isUsagePerson());
        return ret;
    }

    private final static String[] columns = {
            COLUMN_ID, COLUMN_SID, COLUMN_USAGE_CATEGORY, COLUMN_USAGE_PERSON
    };

    @Override
    protected String[] getColumns() {
        return columns;
    }

    @Override
    protected Image fromCursor(Cursor cursor) {
        Integer id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        String sidName = cursor.getString(cursor.getColumnIndex(COLUMN_SID));
        Image.SID sid = Image.SID.valueOf(sidName);
        Integer tmp = cursor.getInt(cursor.getColumnIndex(COLUMN_USAGE_CATEGORY));
        boolean useForCategory = BasicEntity.i2b(tmp);
        tmp = cursor.getInt(cursor.getColumnIndex(COLUMN_USAGE_PERSON));
        boolean useForPerson = BasicEntity.i2b(tmp);
        return new Image(id, sid, useForCategory, useForPerson);
    }

    @Override
    void mergeInto(Image target, Image source) {
        assert target.getId() == source.getId();
        target.setSid(source.getSid());
        target.setUsageCategory(source.isUsageCategory());
        target.setUsagePerson(source.isUsagePerson());
    }

    /**
     * Finds the image by SID
     * @param sid
     * @return
     */
    public Image getBySid(Image.SID sid) {
        List<Image> allWithSid = getAll(COLUMN_SID + "=?", new String[] {sid.name()}, null);
        return allWithSid == null || allWithSid.isEmpty() ? null : allWithSid.get(0);
    }

    /**
     * Returns all images usable for persons
     * @return
     */
    public List<Image> findAllForPersons() {
        return getAll(COLUMN_USAGE_PERSON + "=?", new String[] {"1"}, null);
    }

}
