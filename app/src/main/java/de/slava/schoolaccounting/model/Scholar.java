package de.slava.schoolaccounting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author by V.Sysoltsev
 */
@Accessors(chain=true)
@ToString
public class Scholar extends BasicEntity {
    private Integer id;
    private String nameFull;
    private Room room;

    public Scholar() {
        this(null, null);
    }

    public Scholar(Integer id, String name) {
        this(id, name, null);
    }

    public Scholar(Integer id, String name, Room room) {
        setId(id);
        setNameFull(name);
        setRoom(room);
    }

    /**
     * allows to move a scholar to a new room
     * @param newRoom
     */
    public void setRoom(Room newRoom) {
        if (room != null)
            room.removeScholar(this);
        this.room = newRoom;
        if (room != null)
            room.addScholar(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        Integer oldValue = id;
        this.id = id;
        super.firePropertyChange("id", oldValue, id);
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
}
