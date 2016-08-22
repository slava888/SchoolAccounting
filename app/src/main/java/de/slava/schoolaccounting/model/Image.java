package de.slava.schoolaccounting.model;

import de.slava.schoolaccounting.R;

/**
 * @author by V.Sysoltsev
 */
public class Image extends BasicEntity {
    public static enum SID {
          CATEGORY_CLASS_1(R.drawable.cat_class_1, true, false)
        , CATEGORY_CLASS_2(R.drawable.cat_class_2, true, false)
        , CATEGORY_CLASS_3(R.drawable.cat_class_3, true, false)
        , CATEGORY_CLASS_4(R.drawable.cat_class_4, true, false)
        , PERSON_1(R.drawable.person_1, false, true)
        , PERSON_2(R.drawable.person_2, false, true)
        , PERSON_3(R.drawable.person_3, false, true)
        , PERSON_4(R.drawable.person_4, false, true)
        , PERSON_5(R.drawable.person_5, false, true)
        , PERSON_6(R.drawable.person_6, false, true)
        , PERSON_7(R.drawable.flower, false, true)
        ;
        private final int resourceId;
        private final boolean usageCategory;
        private final boolean usagePerson;

        SID(int resourceId, boolean usageCategory, boolean usagePerson) {
            this.resourceId = resourceId;
            this.usageCategory = usageCategory;
            this.usagePerson = usagePerson;
        }

        public int getResourceId() {
            return resourceId;
        }

        public boolean isUsageCategory() {
            return usageCategory;
        }

        public boolean isUsagePerson() {
            return usagePerson;
        }
    };

    private final String PROPERTY_SID = "sid";
    private final String PROPERTY_USAGE_CATEGORY = "usageCategory";
    private final String PROPERTY_USAGE_PERSON = "usagePerson";

    private SID sid;
    private boolean usageCategory;
    private boolean usagePerson;

    public Image(SID sid) {
        super();
        this.sid = sid;
        this.usageCategory = sid.isUsageCategory();
        this.usagePerson = sid.isUsagePerson();
    }

    public Image(Integer id, SID sid, boolean usageCategory, boolean usagePerson) {
        super(id);
        this.sid = sid;
        this.usageCategory = usageCategory;
        this.usagePerson = usagePerson;
    }

    public SID getSid() {
        return sid;
    }

    public void setSid(SID sid) {
        SID oldValue = this.sid;
        this.sid = sid;
        super.firePropertyChange(PROPERTY_SID, oldValue, sid);
    }

    public boolean isUsageCategory() {
        return usageCategory;
    }

    public void setUsageCategory(boolean usageCategory) {
        boolean oldValue = this.usageCategory;
        this.usageCategory = usageCategory;
        super.firePropertyChange(PROPERTY_USAGE_CATEGORY, oldValue, usageCategory);
    }

    public boolean isUsagePerson() {
        return usagePerson;
    }

    public void setUsagePerson(boolean usagePerson) {
        boolean oldValue = this.usagePerson;
        this.usagePerson = usagePerson;
        super.firePropertyChange(PROPERTY_USAGE_PERSON, oldValue, usagePerson);
    }
}
