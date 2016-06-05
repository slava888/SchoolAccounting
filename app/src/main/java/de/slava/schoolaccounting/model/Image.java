package de.slava.schoolaccounting.model;

import de.slava.schoolaccounting.R;

/**
 * @author by V.Sysoltsev
 */
public class Image extends BasicEntity {
    public static enum SID {
          CATEGORY_CLASS_1(R.drawable.cat_class_1)
        , CATEGORY_CLASS_2(R.drawable.cat_class_2)
        , CATEGORY_CLASS_3(R.drawable.cat_class_3)
        , CATEGORY_CLASS_4(R.drawable.cat_class_4)
        , PERSON_1(R.drawable.person_1)
        , PERSON_2(R.drawable.person_2)
        , PERSON_3(R.drawable.person_3)
        , PERSON_4(R.drawable.person_4)
        , PERSON_5(R.drawable.person_5)
        , PERSON_6(R.drawable.person_6)
        , PERSON_7(R.drawable.flower)
        ;
        private final int resourceId;

        SID(int resourceId) {
            this.resourceId = resourceId;
        }

        public int getResourceId() {
            return resourceId;
        }
    };

    private final String PROPERTY_SID = "sid";

    private SID sid;

    public Image(SID sid) {
        this.sid = sid;
    }

    public Image(Integer id, SID sid) {
        super(id);
        this.sid = sid;
    }

    public SID getSid() {
        return sid;
    }

    public void setSid(SID sid) {
        SID oldValue = this.sid;
        this.sid = sid;
        super.firePropertyChange(PROPERTY_SID, oldValue, sid);
    }
}
