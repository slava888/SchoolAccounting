package de.slava.schoolaccounting.model.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;

import de.slava.schoolaccounting.model.AppOption;
import de.slava.schoolaccounting.util.StringUtils;

/**
 * @author by V.Sysoltsev
 */
public class OptionsDao extends BaseRawDao {
    public final static String TABLE_NAME = "OPTIONS";
    public final static String COLUMN_OPTION = "OPTION";
    public final static String COLUMN_VALUE = "VALUE";

    private Gson gson;

    protected OptionsDao(EntityManager entityManager, EntityManager.DBDaoKey key) {
        super(entityManager, key);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    /**
     * Retrieves an option from db
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getOption(AppOption name, Class<T> clazz) {
        String columns[] = {
                COLUMN_VALUE
        };
        String args[] = {
                name.getOptionNameInDb()
        };
        try (Cursor c = getDatabase().query(getTableName(), columns, COLUMN_OPTION + " = ?", args, null, null, null)) {
            while (c.moveToNext()) {
                String optionValueStr = c.getString(c.getColumnIndex(COLUMN_VALUE));
                if (StringUtils.isBlank(optionValueStr))
                    return null;
                return getGson().fromJson(optionValueStr, clazz);
            }
        }
        return null;
    }

    /**
     * Stores option in DB
     * @param option
     * @param value
     * @param <T>
     */
    public <T> void setOption(AppOption option, T value) {
        // we need to find out the ID if it is in DB
        Integer id = findOptionId(option);
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_OPTION, option.getOptionNameInDb());
        String jsonValue = getGson().toJson(value);
        cv.put(COLUMN_VALUE, jsonValue);
        if (id != null) {
            // update
            getDatabase().update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[] {id.toString()});
        } else {
            // insert
            getDatabase().insert(TABLE_NAME, null, cv);
        }
    }

    private Integer findOptionId(AppOption option) {
        try (Cursor c = getDatabase().query(getTableName(), new String[] {COLUMN_ID}, COLUMN_OPTION + "=?", new String[] {option.getOptionNameInDb()}, null, null, null)) {
            while (c.moveToNext()) {
                return c.getInt(c.getColumnIndex(COLUMN_ID));
            }
        }
        return null;
    }

    private Gson getGson() {
        if (gson == null)
            gson = new Gson();
        return gson;
    }
}
