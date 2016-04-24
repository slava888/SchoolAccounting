package de.slava.schoolaccounting.model.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Image;
import de.slava.schoolaccounting.model.Room;

/**
 * @author by V.Sysoltsev
 */
public class ImageDao extends BaseJPADao<Image> {
    public final static String TABLE_NAME = "IMAGE";
    public final static String COLUMN_SID = "SID";

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
        return ret;
    }

    private final static String[] columns = {
            COLUMN_ID, COLUMN_SID
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
        return new Image(id, sid);
    }

    @Override
    void mergeInto(Image target, Image source) {
        assert target.getId() == source.getId();
        target.setSid(source.getSid());
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

}
