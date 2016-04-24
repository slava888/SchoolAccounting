package de.slava.schoolaccounting.model.db;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.model.Category;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.SchoolModel;

/**
 * @author by V.Sysoltsev
 */
public class EntityManager extends SQLiteOpenHelper {

    /** a private key class to be able to construct dao **/
    public static class DBDaoKey {
        private DBDaoKey() {
        }
    };
    private final static DBDaoKey daoKey = new DBDaoKey();
    private final Context context;
    private Map<Class<?>, Object> daoCache = new HashMap<>();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SchoolAccounting";
    public static final String DATABASE_TABLE_ROOM = "ROOM";
    public static final String DATABASE_TABLE_CHILD = "CHILD";
    public static final String DATABASE_TABLE_JOURNAL = "JOURNAL";

    private static final String SQL_CREATE_IMAGES = "create table " + ImageDao.TABLE_NAME + " (" +
            "   " + BaseRawDao.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY ASC AUTOINCREMENT" +
            " , " + ImageDao.COLUMN_SID + " TEXT" +
            " )";

    private static final String SQL_CREATE_CATEGORIES = "create table " + CategoryDao.TABLE_NAME + " (" +
            "   " + BaseRawDao.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY ASC AUTOINCREMENT" +
            " , " + CategoryDao.COLUMN_NAME + " TEXT NOT NULL UNIQUE" +
            " , " + CategoryDao.COLUMN_IMAGE_FK + " INTEGER NOT NULL REFERENCES " + ImageDao.TABLE_NAME + " (ID)" +
            " )";

    private static final String SQL_CREATE_ROOMS = "create table " + DATABASE_TABLE_ROOM + " ( " +
            "   " + BaseRawDao.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY ASC AUTOINCREMENT" +
            " , " + RoomDao.COLUMN_NAME + " TEXT NOT NULL UNIQUE" +
            " , " + RoomDao.COLUMN_INITIAL + " INTEGER NOT NULL DEFAULT 0" +
            " , " + RoomDao.COLUMN_PROTOCOL_ON_ENTRY + " INTEGER NOT NULL DEFAULT 0" +
            " )";

    private static final String SQL_CREATE_CHILDREN = "create table " + DATABASE_TABLE_CHILD + " ( " +
            "   " + BaseRawDao.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY ASC AUTOINCREMENT" +
            " , " + ChildDao.COLUMN_NAME + " TEXT NOT NULL UNIQUE" +
            " , " + ChildDao.COLUMN_ROOM_FK + " INTEGER NOT NULL REFERENCES " + DATABASE_TABLE_ROOM + " (ID)" +
            " , " + ChildDao.COLUMN_IMAGE_FK + " INTEGER NOT NULL REFERENCES " + ImageDao.TABLE_NAME + " (ID)" +
            " , " + ChildDao.COLUMN_CATEGORY_FK + " INTEGER NOT NULL REFERENCES " + CategoryDao.TABLE_NAME + " (ID)" +
            " )";

    private static final String SQL_CREATE_JOURNAL = "create table " + DATABASE_TABLE_JOURNAL + " ( " +
            "   " + BaseRawDao.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY ASC AUTOINCREMENT" +
            " , " + JournalDao.COLUMN_CHILD_FK + " INTEGER NOT NULL REFERENCES " + DATABASE_TABLE_CHILD + " (ID)" +
            " , " + JournalDao.COLUMN_ROOM_FK + " INTEGER NOT NULL REFERENCES " + DATABASE_TABLE_ROOM + " (ID)" +
            " , " + JournalDao.COLUMN_TIME + " DATETIME NOT NULL" +
            " )";

    private static final String SQL_CREATE_OPTIONS = "create table " + OptionsDao.TABLE_NAME + " ( " +
            "   " + BaseRawDao.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY ASC AUTOINCREMENT" +
            " , " + OptionsDao.COLUMN_OPTION + " TEXT NOT NULL UNIQUE" +
            " , " + OptionsDao.COLUMN_VALUE + " TEXT " +
            " )";

    private EntityManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        db = getWritableDatabase();
    }

    private static EntityManager instance;
    public static synchronized EntityManager instance(Context context) {
        if (instance == null) {
            // context.deleteDatabase(EntityManager.DATABASE_NAME);
            instance = new EntityManager(context);
        }
        return instance;
    }

    private SQLiteDatabase db;
    SQLiteDatabase getDb() {
        return db;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL(SQL_CREATE_IMAGES);
        db.execSQL(SQL_CREATE_ROOMS);
        db.execSQL(SQL_CREATE_CATEGORIES);
        db.execSQL(SQL_CREATE_CHILDREN);
        db.execSQL(SQL_CREATE_JOURNAL);
        db.execSQL(SQL_CREATE_OPTIONS);
        new DBPopulator(this, 0, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // should not yet be used
        this.db = db;
        switch(oldVersion) {
        }
        new DBPopulator(this, oldVersion, newVersion);
    }

    public <T> T getDao(Class<T> daoClass) {
        Object dao = daoCache.get(daoClass);
        if (dao == null) {
            if (daoClass == ChildDao.class)
                dao = new ChildDao(this, daoKey);
            else if (daoClass == RoomDao.class)
                dao = new RoomDao(this, daoKey);
            else if (daoClass == JournalDao.class)
                dao = new JournalDao(this, daoKey);
            else if (daoClass == OptionsDao.class)
                dao = new OptionsDao(this, daoKey);
            else if (daoClass == ImageDao.class)
                dao = new ImageDao(this, daoKey);
            else if (daoClass == CategoryDao.class)
                dao = new CategoryDao(this, daoKey);
            else {
                assert false : "Unknown DAO requested";
                return null;
            }
            daoCache.put(daoClass, dao);
        }
        return (T)dao;
    }

    public ArrayList<Cursor> getDataForADBM(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {
                alc.set(0, c);
                c.moveToFirst();
                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d(Main.getTag(), sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {
            Log.d(Main.getTag(), ex.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }

}
