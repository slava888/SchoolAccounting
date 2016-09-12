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
        , PERSON_ALLIGATOR(R.drawable.alligator, false, true)
        , PERSON_ANIMATION(R.drawable.animation, false, true)
        , PERSON_BAT(R.drawable.bat, false, true)
        , PERSON_BEAR_FOOTPRINT(R.drawable.bear_footprint, false, true)
        , PERSON_BICYCLE(R.drawable.bicycle, false, true)
        , PERSON_BIRTHDAY(R.drawable.birthday, false, true)
        , PERSON_BRUTUS(R.drawable.brutus, false, true)
        , PERSON_BULL(R.drawable.bull, false, true)
        , PERSON_BUTTERFLY(R.drawable.butterfly, false, true)
        , PERSON_CANDY_CANE(R.drawable.candy_cane, false, true)
        , PERSON_CAT(R.drawable.cat, false, true)
        , PERSON_CHESS_BISHOP(R.drawable.chess_bishop, false, true)
        , PERSON_CHESS_KING(R.drawable.chess_king, false, true)
        , PERSON_CHESS_KNIGHT(R.drawable.chess_knight, false, true)
        , PERSON_CHESS_PAWN(R.drawable.chess_pawn, false, true)
        , PERSON_CHESS_QUEEN(R.drawable.chess_queen, false, true)
        , PERSON_CHESS_ROOK(R.drawable.chess_rook, false, true)
        , PERSON_CLOWN_FISH(R.drawable.clown_fish, false, true)
        , PERSON_COCONUT_COCKTAIL(R.drawable.coconut_cocktail, false, true)
        , PERSON_COMEDY_2(R.drawable.comedy_2, false, true)
        , PERSON_CORGI(R.drawable.corgi, false, true)
        , PERSON_DEER(R.drawable.deer, false, true)
        , PERSON_DIAMOND(R.drawable.diamond, false, true)
        , PERSON_DIZZY_PERSON(R.drawable.dizzy_person, false, true)
        , PERSON_DNA_HELIX(R.drawable.dna_helix, false, true)
        , PERSON_DOG(R.drawable.dog, false, true)
        , PERSON_FIRE_TRUCK(R.drawable.fire_truck, false, true)
        , PERSON_FOREST(R.drawable.forest, false, true)
        , PERSON_FRANKENSTEIN_MONSTER(R.drawable.frankenstein_monster, false, true)
        , PERSON_GHOST(R.drawable.ghost, false, true)
        , PERSON_GORILLA(R.drawable.gorilla, false, true)
        , PERSON_HAPPY(R.drawable.happy, false, true)
        , PERSON_HEART_WITH_ARROW(R.drawable.heart_with_arrow, false, true)
        , PERSON_HORSEBACK_RIDING(R.drawable.horseback_riding, false, true)
        , PERSON_ICE_CREAM_CONE(R.drawable.ice_cream_cone, false, true)
        , PERSON_KNITTED_FROG(R.drawable.knitted_frog, false, true)
        , PERSON_LION_STATUE(R.drawable.lion_statue, false, true)
        , PERSON_MILITARY_HELICOPTER(R.drawable.military_helicopter, false, true)
        , PERSON_MINECRAFT_DIAMOND(R.drawable.minecrat_diamond, false, true)
        , PERSON_MODERATOR_FEMALE(R.drawable.moderator_female, false, true)
        , PERSON_MODERATOR_MALE(R.drawable.moderator_male, false, true)
        , PERSON_NINJA_TURTLE(R.drawable.ninja_turtle, false, true)
        , PERSON_OCTOPUS(R.drawable.octopus, false, true)
        , PERSON_PHYSICS(R.drawable.physics, false, true)
        , PERSON_PIG(R.drawable.pig, false, true)
        , PERSON_PIZZA(R.drawable.pizza, false, true)
        , PERSON_PLUSH(R.drawable.plush, false, true)
        , PERSON_POKEMON(R.drawable.pokemon, false, true)
        , PERSON_QUESTION(R.drawable.question, false, true)
        , PERSON_RICE_BOWL(R.drawable.rice_bowl, false, true)
        , PERSON_ROCKET(R.drawable.rocket, false, true)
        , PERSON_SEESAW(R.drawable.seesaw, false, true)
        , PERSON_SHEEP_2(R.drawable.sheep_2, false, true)
        , PERSON_SNOWFLAKE(R.drawable.snowflake, false, true)
        , PERSON_SWEARING_FEMALE(R.drawable.swearing_female, false, true)
        , PERSON_SWEARING_MALE(R.drawable.swearing_male, false, true)
        , PERSON_TANK(R.drawable.tank, false, true)
        , PERSON_THEATRE_MASK(R.drawable.theatre_mask, false, true)
        , PERSON_THREE_LEAF_CLOVER(R.drawable.three_leaf_clover, false, true)
        , PERSON_WITCH(R.drawable.witch, false, true)
        , PERSON_WIZARD(R.drawable.wizard, false, true)
        , PERSON_WOODY_WOODPECKER(R.drawable.woody_woodpecker, false, true)
        , PERSON_YEAR_OF_DRAGON(R.drawable.year_of_dragon, false, true)
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
