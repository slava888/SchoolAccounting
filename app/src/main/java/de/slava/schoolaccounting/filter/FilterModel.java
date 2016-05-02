package de.slava.schoolaccounting.filter;

import java.util.HashSet;
import java.util.Set;

import de.slava.schoolaccounting.model.BaseObservable;
import de.slava.schoolaccounting.util.StringUtils;

/**
 * Model for filter
 * @author by V.Sysoltsev
 */
public class FilterModel extends BaseObservable {
    public static final String PROPERTY_TEXT = "text";
    public static final String PROPERTY_TEXT_ACTIVE = "textActive";
    public static final String PROPERTY_CATEGORIES = "categories";

    private Set<Integer> categories = new HashSet<>();
    private String text;
    private boolean textActive = false;

    public Set<Integer> getCategories() {
        return categories;
    }

    public void setCategories(Set<Integer> categories) {
        Set<Integer> oldValue = this.categories;
        this.categories = categories;
        super.firePropertyChange(PROPERTY_CATEGORIES, oldValue, categories);
    }

    public void addCategory(Integer category) {
        if (categories.add(category)) {
            super.firePropertyChange(PROPERTY_CATEGORIES, null, categories);
        }
    }

    public void removeCategory(Integer category) {
        if (categories.remove(category)) {
            super.firePropertyChange(PROPERTY_CATEGORIES, null, categories);
        }
    }

    public boolean isCategoryActivated(Integer id) {
        return categories.contains(id);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String oldValue = this.text;
        this.text = text;
        if (StringUtils.isBlank(text))
            setTextActive(false);
        else
            setTextActive(true);
        super.firePropertyChange(PROPERTY_TEXT, oldValue, text);
    }

    public boolean isTextActive() {
        return textActive;
    }

    public void setTextActive(boolean textActive) {
        boolean oldValue = this.textActive;
        this.textActive = textActive;
        super.firePropertyChange(PROPERTY_TEXT_ACTIVE, oldValue, textActive);
    }

    @Override
    public String toString() {
        return String.format("%s{%s, %s%s}", getClass().getSimpleName(), getCategories(), getText(), isTextActive() ? "" : "(-)");
    }
}