package de.slava.schoolaccounting.model;

/**
 * @author by V.Sysoltsev
 */
public class Storage {
    public SchoolModel loadModel() {
        SchoolModel ret = new SchoolModel();
        int roomId = 1;
        for (String roomName : new String[]{"Home", "?", "011", "017", "018", "TH", "Hof"}) {
            Room room = new Room(roomId++, roomName);
            if (roomName.equals("?")) {
                ret.setDefaultRoom(room);
            }
            ret.getRooms().add(room);
        }
        int schId = 1;
        ret.getScholars().add(new Scholar(schId++, "Slava", ret.getDefaultRoom(), 1));
        ret.getScholars().add(new Scholar(schId++, "Marina", ret.getDefaultRoom(), 4));
        ret.getScholars().add(new Scholar(schId++, "Stefan", ret.getDefaultRoom(), 2));
        ret.getScholars().add(new Scholar(schId++, "Sebastian", ret.getDefaultRoom(), 2));
        ret.getScholars().add(new Scholar(schId++, "Maja", ret.getDefaultRoom(), 3));
        return ret;
    }

    public void saveModel(SchoolModel model) {
        // TODO
    }
}
