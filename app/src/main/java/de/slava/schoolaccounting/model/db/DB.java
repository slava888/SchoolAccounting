package de.slava.schoolaccounting.model.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.SchoolModel;
import lombok.Getter;

/**
 * @author by V.Sysoltsev
 */
public class DB extends SQLiteOpenHelper {

    /** a private key class to be able to construct dao **/
    public static class DBDaoKey {
        private DBDaoKey() {
        }
    };
    private final static DBDaoKey daoKey = new DBDaoKey();
    private Map<Class<?>, Object> daoCache = new HashMap<>();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SchoolAccounting";
    public static final String DATABASE_TABLE_ROOM = "ROOM";
    public static final String DATABASE_TABLE_CHILD = "CHILD";

    private static final String SQL_CREATE_ROOMS = "create table " + DATABASE_TABLE_ROOM + " ( " +
            "   ID INTEGER NOT NULL PRIMARY KEY ASC AUTOINCREMENT" +
            " , NAME TEXT NOT NULL UNIQUE" +
            " , INITIAL INTEGER NOT NULL DEFAULT 0" +
            " )";

    private static final String SQL_CREATE_CHILDREN = "create table " + DATABASE_TABLE_CHILD + " ( " +
            "   ID INTEGER NOT NULL PRIMARY KEY ASC AUTOINCREMENT" +
            " , NAME TEXT NOT NULL UNIQUE" +
            " , ROOM_FK INTEGER NOT NULL REFERENCES " + DATABASE_TABLE_ROOM + " (ID)" +
            " , IMAGE_FK INTEGER" + // TODO FK here
            " )";

    private DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    private static DB instance;
    public static synchronized DB instance(Context context) {
        if (instance == null) {
            // context.deleteDatabase(DB.DATABASE_NAME);
            instance = new DB(context);
        }
        return instance;
    }

    SQLiteDatabase db;
    SQLiteDatabase getDb() {
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL(SQL_CREATE_ROOMS);
        db.execSQL(SQL_CREATE_CHILDREN);
        populateRooms();
        populateChildren();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.db = db;
        // no versions yet
    }

    public <T> T getDao(Class<T> daoClass) {
        Object dao = daoCache.get(daoClass);
        if (dao == null) {
            if (daoClass == ChildDao.class)
                dao = new ChildDao(this, daoKey);
            else if (daoClass == RoomDao.class)
                dao = new RoomDao(this, daoKey);
            else {
                assert false : "Unknown DAO requested";
                return null;
            }
            daoCache.put(daoClass, dao);
        }
        return (T)dao;
    }

    private void populateRooms() {
        RoomDao dao = getDao(RoomDao.class);
        for (String roomName : new String[]{"Home", "?", "011", "017", "018", "TH", "Hof"}) {
            Room room = dao.add(new Room(null, roomName, roomName.equals("?")));
            // Log.d(Main.getTag(), String.format("Created room %s", room));
        }
        Log.d(Main.getTag(), String.format("All rooms: %s", dao.getAll(null, null)));
    }

    private void populateChildren() {
        Room initial = ensureInitialRoomExists();
        ChildDao dao = getDao(ChildDao.class);
        dao.add(new Child(null, "Slava", initial, 1));
        dao.add(new Child(null, "Marina", initial, 4));
        dao.add(new Child(null, "Stefan", initial, 2));
        dao.add(new Child(null, "Sebastian", initial, 2));
        dao.add(new Child(null, "Maja", initial, 3));
        Log.d(Main.getTag(), String.format("All children: %s", dao.getAll(null, null)));
    }

    private Room ensureInitialRoomExists() {
        RoomDao dao = getDao(RoomDao.class);
        List<Room> defauls = dao.getAll("INITIAL = 1", null);
        if (defauls.isEmpty()) {
            Log.w(Main.getTag(), "No initial room defined, mark first as one");
            defauls = dao.getAll(null, null);
            assert !defauls.isEmpty() : "No rooms?!?";
            Room def = defauls.iterator().next();
            def.setInitial(true);
            def = dao.update(def);
            return def;
        }
        return defauls.iterator().next();
    }

    public void saveModel(SchoolModel model) {
        // TODO
    }
}
