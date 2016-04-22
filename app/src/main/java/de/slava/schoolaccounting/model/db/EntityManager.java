package de.slava.schoolaccounting.model.db;

import android.content.Context;
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
    private Map<Class<?>, Object> daoCache = new HashMap<>();

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "SchoolAccounting";
    public static final String DATABASE_TABLE_ROOM = "ROOM";
    public static final String DATABASE_TABLE_CHILD = "CHILD";
    public static final String DATABASE_TABLE_JOURNAL = "JOURNAL";

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
            " , " + ChildDao.COLUMN_IMAGE_FK + " INTEGER" + // TODO FK here
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL(SQL_CREATE_ROOMS);
        db.execSQL(SQL_CREATE_CHILDREN);
        db.execSQL(SQL_CREATE_JOURNAL);
        db.execSQL(SQL_CREATE_OPTIONS);
        populateRooms();
        populateChildren();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.db = db;
        switch(oldVersion) {
            case 1:
                db.execSQL(SQL_CREATE_OPTIONS);
        }
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
            Room room = dao.add(new Room(null, roomName, roomName.equals("?"), roomName.equals("Home")));
            // Log.d(Main.getTag(), String.format("Created room %s", room));
        }
        Log.d(Main.getTag(), String.format("All rooms: %s", dao.getAll(null, null, null)));
    }

    private void populateChildren() {
        Room initial = ensureInitialRoomExists();
        ChildDao dao = getDao(ChildDao.class);
        dao.add(new Child(null, "Slava", initial, 1));
        dao.add(new Child(null, "Marina", initial, 4));
        dao.add(new Child(null, "Stefan", initial, 2));
        dao.add(new Child(null, "Sebastian", initial, 2));
        dao.add(new Child(null, "Maja", initial, 3));
        dao.add(new Child(null, "Rocco", initial, 2));
        dao.add(new Child(null, "Julian", initial, 2));
        dao.add(new Child(null, "Carlos", initial, 2));
        dao.add(new Child(null, "Benedikt", initial, 2));
        dao.add(new Child(null, "Marko", initial, 2));
        dao.add(new Child(null, "Ira", initial, 3));
        dao.add(new Child(null, "Valentin", initial, 2));
        dao.add(new Child(null, "Iva", initial, 3));
        dao.add(new Child(null, "Miku", initial, 3));
        dao.add(new Child(null, "Misu", initial, 3));
        dao.add(new Child(null, "Milad", initial, 2));
        dao.add(new Child(null, "Aikan", initial, 2));
        dao.add(new Child(null, "Akan", initial, 2));
        dao.add(new Child(null, "Ilaidanur", initial, 3));
        dao.add(new Child(null, "Mila", initial, 3));
        dao.add(new Child(null, "Maria", initial, 3));
        dao.add(new Child(null, "Aikan", initial, 2));

        String players[] = {
                "Timo Achenbach",
                "Benjamin Auer",
                "Roland Benschneider",
                "Daniel Bierofka",
                "Philipp Bönig",
                "Pascal Borel",
                "Tim Borowski",
                "Thomas Broich",
                "Daniyel Cimen",
                "Simon Cziommer",
                "Christoph Dabrowski",
                "Markus Daun",
                "Mustafa Doğan",
                "Marco Engelhardt",
                "Robert Enke",
                "Fabian Ernst",
                "Frank Fahrenhorst",
                "Maik Franz",
                "Arne Friedrich",
                "Manuel Friedrich",
                "Clemens Fritz",
                "Nico Frommer",
                "Christian Gentner",
                "Fabian Gerber",
                "Mario Gómez",
                "Mike Hanke",
                "Patrick Helmes",
                "Ingo Hertzsch",
                "Timo Hildebrand",
                "Andreas Hinkel",
                "Steffen Hofmann",
                "Alexander Huber",
                "Simon Jentzsch",
                "Jermaine Jones",
                "Enrico Kern",
                "Stefan Kießling",
                "Thomas Kleine",
                "Stephan Kling",
                "Peer Kluge",
                "Bernd Korzynietz",
                "Markus Kreuz",
                "Florian Kringe",
                "Emmanuel Krontiris",
                "Kevin Kurányi",
                "Matthias Langkamp",
                "Benjamin Lense",
                "Alexander Madlung",
                "Marcel Maltritz",
                "Thorben Marx",
                "Martin Meichelbeck",
                "Alexander Meier",
                "Alexander Meyer",
                "Uwe Möhrle",
                "Sven Müller",
                "Andreas Ottl",
                "Christoph Preuß",
                "Tobias Rau",
                "Simon Rolfes",
                "Sascha Rösler",
                "Marcel Schäfer",
                "Sebastian Schindzielorz",
                "Björn Schlicke",
                "Silvio Schröter",
                "Markus Schroth",
                "Martin Stoll",
                "Albert Streit",
                "Christian Timm",
                "Alexander Voigt",
                "Andreas Voss",
                "Roman Weidenfeller",
                "Benjamin Weigelt",
                "Timo Wenzel",
                "Stefan Wessels"
        };
        for (String player : players) {
            dao.add(new Child(null, player, initial, 5));
        }

        Log.d(Main.getTag(), String.format("All children: %s", dao.getAll(null, null, null)));
    }

    private Room ensureInitialRoomExists() {
        RoomDao dao = getDao(RoomDao.class);
        List<Room> defauls = dao.getAll("INITIAL = 1", null, null);
        if (defauls.isEmpty()) {
            Log.w(Main.getTag(), "No initial room defined, mark first as one");
            defauls = dao.getAll(null, null, null);
            assert !defauls.isEmpty() : "No rooms?!?";
            Room def = defauls.iterator().next();
            def.setInitial(true);
            def = dao.update(def);
            return def;
        }
        return defauls.iterator().next();
    }

}
