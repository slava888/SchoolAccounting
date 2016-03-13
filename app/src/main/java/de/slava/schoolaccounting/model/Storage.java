package de.slava.schoolaccounting.model;

/**
 * @author by V.Sysoltsev
 */
public class Storage {
    public SchoolModel loadModel() {
        SchoolModel ret = new SchoolModel();
        int roomId = 1;
        Room roomUnknown = null;
        for (String roomName : new String[]{"Home", "Unknown", "011", "017", "018", "TH", "Hof"}) {
            Room room = new Room(roomId++, roomName);
            if (roomName.equals("Unknown")) {
                roomUnknown = room;
            }
            ret.getRooms().add(room);
        }
        int schId = 1;
        ret.getScholars().add(new Scholar(schId++, "Slava", roomUnknown));
        ret.getScholars().add(new Scholar(schId++, "Marina", roomUnknown));
        ret.getScholars().add(new Scholar(schId++, "Stefan", roomUnknown));
        ret.getScholars().add(new Scholar(schId++, "Sebastian", roomUnknown));
        return ret;
    }

    public void saveModel(SchoolModel model) {
        // TODO
    }
}
