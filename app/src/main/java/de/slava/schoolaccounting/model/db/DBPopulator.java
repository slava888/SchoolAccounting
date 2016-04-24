package de.slava.schoolaccounting.model.db;

import android.util.Log;

import java.util.List;

import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.model.Category;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Image;
import de.slava.schoolaccounting.model.Room;

/**
 * Populates the database with data
 *
 * @author by V.Sysoltsev
 */
public class DBPopulator {

    private final EntityManager em;
    private Category cat1;
    private Category cat2;
    private Category cat3;
    private Category cat4;

    public DBPopulator(EntityManager em, int oldDbVersion, int newDbVersion) {
        this.em = em;
        switch (oldDbVersion) {
            case 0:
                populateImages();
                populateRooms();
                populateCategories();
                populateChildren();
                // fallthrough
        }
    }

    public EntityManager getEntityManager() {
        return em;
    }

    private void populateImages() {
        ImageDao dao = getDao(ImageDao.class);
        for (Image.SID sid : Image.SID.values()) {
            Image img = new Image(sid);
            dao.add(img);
        }
    }

    private void populateRooms() {
        RoomDao dao = getDao(RoomDao.class);
        for (Room.Name roomName : Room.Name.values()) {
            Room room = dao.add(new Room(null, getEntityManager().getContext().getString(roomName.getRoomResourceKey()), roomName == Room.Name.ROOM_MITTAGSBETREUUNG, roomName == Room.Name.ROOM_HOME));
            // Log.d(Main.getTag(), String.format("Created room %s", room));
        }
        Log.d(Main.getTag(), String.format("All rooms: %s", dao.getAll(null, null, null)));
    }

    private void populateCategories() {
        ImageDao idao = getDao(ImageDao.class);
        Image ci1 = idao.getBySid(Image.SID.CATEGORY_CLASS_1);
        Image ci2 = idao.getBySid(Image.SID.CATEGORY_CLASS_2);
        Image ci3 = idao.getBySid(Image.SID.CATEGORY_CLASS_3);
        Image ci4 = idao.getBySid(Image.SID.CATEGORY_CLASS_4);
        CategoryDao dao = getDao(CategoryDao.class);
        this.cat1 = dao.add(new Category("Klasse 1", ci1));
        this.cat2 = dao.add(new Category("Klasse 2", ci2));
        this.cat3 = dao.add(new Category("Klasse 3", ci3));
        this.cat4 = dao.add(new Category("Klasse 4", ci4));
    }

    private void populateChildren() {
        Room initial = ensureInitialRoomExists();
        ImageDao idao = getDao(ImageDao.class);
        Image p1 = idao.getBySid(Image.SID.PERSON_1);
        Image p2 = idao.getBySid(Image.SID.PERSON_2);
        Image p3 = idao.getBySid(Image.SID.PERSON_3);
        Image p4 = idao.getBySid(Image.SID.PERSON_4);
        Image p5 = idao.getBySid(Image.SID.PERSON_5);
        Image p6 = idao.getBySid(Image.SID.PERSON_6);
        ChildDao dao = getEntityManager().getDao(ChildDao.class);
        dao.add(new Child(null, "Slava", initial, p1, cat4));
        dao.add(new Child(null, "Marina", initial, p4, cat4));
        dao.add(new Child(null, "Stefan", initial, p2, cat3));
        dao.add(new Child(null, "Sebastian", initial, p2, cat3));
        dao.add(new Child(null, "Maja", initial, p3, cat3));
        dao.add(new Child(null, "Rocco", initial, p2, cat3));
        dao.add(new Child(null, "Julian", initial, p2, cat3));
        dao.add(new Child(null, "Carlos", initial, p2, cat3));
        dao.add(new Child(null, "Benedikt", initial, p2, cat3));
        dao.add(new Child(null, "Marko", initial, p2, cat3));
        dao.add(new Child(null, "Ira", initial, p3, cat3));
        dao.add(new Child(null, "Valentin", initial, p2, cat3));
        dao.add(new Child(null, "Iva", initial, p3, cat3));
        dao.add(new Child(null, "Miku", initial, p3, cat3));
        dao.add(new Child(null, "Misu", initial, p3, cat3));
        dao.add(new Child(null, "Milad", initial, p2, cat3));
        dao.add(new Child(null, "Aikan", initial, p2, cat3));
        dao.add(new Child(null, "Akan", initial, p2, cat3));
        dao.add(new Child(null, "Ilaidanur", initial, p3, cat3));
        dao.add(new Child(null, "Mila", initial, p3, cat3));
        dao.add(new Child(null, "Maria", initial, p3, cat3));
        dao.add(new Child(null, "Aikan", initial, p2, cat3));

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
        boolean v = true;
        for (String player : players) {
            dao.add(new Child(null, player, initial, v ? p5 : p6, v ? cat1 : cat2));
            v = !v;
        }
        // Log.d(Main.getTag(), String.format("All children: %s", dao.getAll(null, null, null)));
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

    public <T> T getDao(Class<T> daoClass) {
        return getEntityManager().getDao(daoClass);
    }
}
