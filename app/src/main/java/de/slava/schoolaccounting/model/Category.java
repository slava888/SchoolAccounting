package de.slava.schoolaccounting.model;

/**
 * @author by V.Sysoltsev
 */
public class Category extends BasicEntity {
    private String name;
    private Image image;

    public Category(String name, Image image) {
        this.name = name;
        this.image = image;
    }

    public Category(Integer id, String name, Image image) {
        super(id);
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        super.firePropertyChange("name", oldValue, name);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        Image oldValue = this.image;
        this.image = image;
        super.firePropertyChange("image", oldValue, image);
    }

    @Override
    public String toString() {
        return String.format("%s{%d, %s, %s}", getClass().getSimpleName(), getId(), getName(), getImage());
    }

}
