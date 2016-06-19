package de.slava.schoolaccounting.model;

import de.slava.schoolaccounting.filter.FilterModel;
import de.slava.schoolaccounting.util.TriBoolean;
import lombok.experimental.Accessors;

/**
 * @author by V.Sysoltsev
 */
@Accessors(chain=true)
public class Child extends BasicEntity {
    public final static String PROPERTY_NAME_FULL = "nameFull";
    public final static String PROPERTY_ACTIVE = "active";
    public final static String PROPERTY_ROOM = "room";
    public final static String PROPERTY_IMAGE = "image";
    public final static String PROPERTY_CATEGORY = "category";

    private String nameFull;
    private boolean active;
    private Room room;
    private Image image;
    private Category category;

    public Child() { this(null, null); }

    public Child(Integer id, String name) { this(id, true, name, null, null, null); }

    public Child(Child other) {
        setActive(other.isActive());
        setNameFull(other.getNameFull());
        setRoom(other.getRoom());
        setImage(other.getImage());
        setCategory(other.getCategory());
    }

    public Child(Integer id, boolean active, String name, Room room, Image image, Category category) {
        super(id);
        setActive(active);
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        boolean oldValue = this.active;
        this.active = active;
        super.firePropertyChange(PROPERTY_ACTIVE, oldValue, active);
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
        return String.format("%s{%s%d, %s, in %s}", getClass().getSimpleName(), isActive() ? "" : "X, ", getId(), getNameFull(), getRoom());
    }

    /**
     * Returns true if the child matches the filter and should be returned.
     * @param filter
     * @return
     */
    public boolean isMatch(FilterModel filter) {
        if (filter == null)
            return true;
        if (!isMatchFilterDeleted(filter.getShowDeleted()))
            return false;
        if (getCategory() != null && !filter.isCategoryActivated(getCategory().getId()))
            return false;
        if (getNameFull() != null && filter.isTextActive() && !getNameFull().toLowerCase().contains(filter.getText().toLowerCase()))
            return false;
        return true;
    }

    private boolean isMatchFilterDeleted(TriBoolean showDeleted) {
        // UNKNOWN - show all, TRUE - show deleted only, FALSE - show active only
        if (showDeleted == null || showDeleted == TriBoolean.UNKNOWN)
            return true;
        return showDeleted.toBoolean() != isActive();
    }
}
