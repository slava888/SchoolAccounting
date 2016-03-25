package de.slava.schoolaccounting.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.slava.schoolaccounting.model.BasicEntity;

/**
 * @author by V.Sysoltsev
 */
public abstract class BaseDao<Entity extends BasicEntity> {
    private final EntityManager entityManager;

    // only the entities from cache are allowed to be outside (due to FK handling).
    private final Map<Integer, Entity> cache = new HashMap<>();

    protected BaseDao(EntityManager entityManager, EntityManager.DBDaoKey key) {
        this.entityManager = entityManager;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected SQLiteDatabase getDatabase() {
        return entityManager.getDb();
    }

    /**
     * Descendants to declare table name here.
     *
     * @return
     */
    abstract protected String getTableName();

    public int getNextId() {
        try (Cursor c = getDatabase().rawQuery("select max(ID) from " + getTableName(), null)) {
            c.moveToFirst();
            int v = c.getInt(0);
            return v + 1;
        }
    }

    /**
     * Converts an entity to ContentValues.
     * Must be impleented by descendants.
     *
     * @param room
     * @return
     */
    abstract protected ContentValues asCV(Entity room);

    /**
     * Add new entity to a database
     */
    public Entity add(Entity entity) {
        assert entity.isTransient();
        if (entity.getId() == null)
            entity.setId(getNextId());
        getDatabase().insert(getTableName(), null, asCV(entity));
        return persist(entity);
    }

    /**
     * Get-all query. Filter/order may be null.
     *
     * @param where
     * @param orderBy
     * @return
     */
    public List<Entity> getAll(String where, String orderBy) {
        List<Entity> ret = new ArrayList<>();
        try (Cursor c = getDatabase().query(getTableName(), getColumns(), where, null, null, null, orderBy)) {
            while (c.moveToNext()) {
                ret.add(persist(fromCursor(c)));
            }
        }
        return ret;
    }

    public Entity getById(Integer id) {
        assert id != null;
        Entity fromCache = cache.get(id);
        if (fromCache != null)
            return fromCache;
        List<Entity> ret = getAll(String.format("ID = %d", id), null);
        return ret.isEmpty() ? null : persist(ret.iterator().next());
    }

    /**
     * Descendants should list all columns needed to be fetched from the database to construct an entity
     *
     * @return
     */
    abstract protected String[] getColumns();

    /**
     * Descendants should construct an entity from result set. The fetched columns are those enumerated in {@link #getColumns() getColumns} method.
     *
     * @param cursor
     * @return
     */
    abstract protected Entity fromCursor(Cursor cursor);


    /**
     * Update entity in DB.
     *
     * @param entity
     * @return
     */
    public Entity update(Entity entity) {
        assert entity.isPersistent() && entity.getId() != null : "Use add() for new entities";
        getDatabase().update(getTableName(), asCV(entity), String.format("ID = %d", entity.getId()), null);
        return persist(entity);
    }

    /**
     * Either add or update an entity in DB
     *
     * @param entity
     * @return
     */
    public Entity addUpdate(Entity entity) {
       if (entity.getId() == null)
           return add(entity);
        else
           return update(entity);
    }

    /**
     * Descendants have to implement merge from source to destination
     * @param target
     * @param source
     */
    abstract void mergeInto(Entity target, Entity source);

    /**
     * Descendants may implement to fetch relations of the entity
     * @param entity
     */
    protected Entity fetchRelations(Entity entity) {
        return entity;
    }

    private Entity persist(Entity persisted) {
        assert persisted.getId() != null;
        Entity fromCache = cache.get(persisted.getId());
        if (fromCache == null) {
            fromCache = persisted;
            persisted.setEntityState(BasicEntity.State.PERSISTENT);
            cache.put(persisted.getId(), persisted);
        } else {
            mergeInto(fromCache, persisted);
        }
        return fetchRelations(fromCache);
    }
}

