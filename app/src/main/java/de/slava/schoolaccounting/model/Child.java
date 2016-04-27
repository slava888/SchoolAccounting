package de.slava.schoolaccounting.model;

import de.slava.schoolaccounting.filter.FilterModel;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author by V.Sysoltsev
 */
@Accessors(chain=true)
public class Child extends BasicEntity {
    public final static String PROPERTY_NAME_FULL = "nameFull";
    public final static String PROPERTY_ROOM = "room";
    public final static String PROPERTY_IMAGE = "image";
    public final static String PROPERTY_CATEGORY = "category";

    private String nameFull;
    private Room room;
    private Image image;
    private Category category;

    public Child() { this(null, null); }

    public Child(Integer id, String name) { this(id, name, null, null, null); }

    public Child(Integer id, String name, Room room, Image image, Category category) {
        super(id);
        setNameFull(name);
        setRoom(room);
        setImage(image);
        setCategory(category);
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
        super.firePropertyChange(PROPERTY_ROOM, oldRoom, newRoom);
        if (getPersistingDao() != null)
            getPersistingDao().update(this);
    }

    public String getNameFull() {
        return nameFull;
    }

    public void setNameFull(String nameFull) {
        String oldValue = this.nameFull;
        this.nameFull = nameFull;
        super.firePropertyChange(PROPERTY_NAME_FULL, oldValue, nameFull);
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        Room oldValue = this.room;
        this.room = room;
        super.firePropertyChange(PROPERTY_ROOM, oldValue, room);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        Image oldValue = this.image;
        this.image = image;
        super.firePropertyChange(PROPERTY_IMAGE, oldValue, image);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        Category oldValue = this.category;
        this.category = category;
        super.firePropertyChange(PROPERTY_CATEGORY, oldValue, category);
    }

    @Override
    public String toString() {
        return String.format("%s{%d, %s, in %s}", getClass().getSimpleName(), getId(), getNameFull(), getRoom());
    }

    /**
     * Returns true if the child matches the filter and should be returned.
     * @param filter
     * @return
     */
    public boolean isMatch(FilterModel filter) {
        if (filter == null)
            return true;
        if (getCategory() != null && !filter.isCategoryActivated(getCategory().getId()))
            return false;
        if (getNameFull() != null && filter.isTextActive() && !getNameFull().toLowerCase().contains(filter.getText().toLowerCase()))
            return false;
        return true;
    }
}
