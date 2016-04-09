package de.slava.schoolaccounting.model.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains minimal functionality of every DAO
 *
 * @author by V.Sysoltsev
 */
public abstract class BaseRawDao {
    public static final String COLUMN_ID = "ID";

    private final EntityManager entityManager;

    protected BaseRawDao(EntityManager entityManager, EntityManager.DBDaoKey key) {
        this.entityManager = entityManager;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected SQLiteDatabase getDatabase() {
        return entityManager.getDb();
    }

    public <T> T getDao(Class<T> daoClass) {
        return getEntityManager().getDao(daoClass);
    }

    /**
     * Descendants to declare table name here.
     *
     * @return
     */
    abstract protected String getTableName();

    public int getNextId() {
        try (Cursor c = getDatabase().rawQuery("select max(" + COLUMN_ID + ") from " + getTableName(), null)) {
            c.moveToFirst();
            int v = c.getInt(0);
            return v + 1;
        }
    }

}
