package de.slava.schoolaccounting.model;

import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author by V.Sysoltsev
 */
@Accessors(chain=true)
@ToString
public class Child extends BasicEntity {
    private String nameFull;
    private Room room;
    private Integer imageId;

    public Child() { this(null, null); }

    public Child(Integer id, String name) { this(id, name, null, null); }

    public Child(Integer id, String name, Room room, Integer imageId) {
        super(id);
        setNameFull(name);
        setRoom(room);
        setImageId(imageId);
    }

    /**
     * allows to move a scholar to a new room
     * @param newRoom
     */
    public void moveToToom(Room newRoom) {
        final Room oldRoom = room;
        if (room != null)
            room.removeChild(this);
        this.room = newRoom;
        if (room != null)
            room.addChild(this);
        super.firePropertyChange("room", oldRoom, newRoom);
    }

    public String getNameFull() {
        return nameFull;
    }

    public void setNameFull(String nameFull) {
        String oldValue = nameFull;
        this.nameFull = nameFull;
        super.firePropertyChange("nameFull", oldValue, nameFull);
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        Room oldValue = room;
        this.room = room;
        super.firePropertyChange("room", oldValue, room);
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        Integer oldValue = imageId;
        this.imageId = imageId;
        super.firePropertyChange("imageId", oldValue, imageId);
    }

    @Override
    public String toString() {
        return String.format("%s{%d, %s, in %s}", getClass().getSimpleName(), getId(), getNameFull(), getRoom());
    }

}
