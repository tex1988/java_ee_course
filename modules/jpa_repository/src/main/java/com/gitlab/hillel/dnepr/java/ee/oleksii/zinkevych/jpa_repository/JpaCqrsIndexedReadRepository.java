package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository;


import com.gitlab.hillel.dnepr.java.ee.common.repository.SyncFuture;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.function.Function;

@Slf4j
public class JpaCqrsIndexedReadRepository<T extends BaseEntity<ID>, ID>
        implements CqrsIndexedReadRepository<T, ID> {
    private final Map<Action, Function<Iterable<T>, Future<?>>> actionMap = new HashMap<>();
    private final List<String> indexKeys = new ArrayList<>();
    private final EntityManager entityManager;
    private final Class<T> entityClass;

    public JpaCqrsIndexedReadRepository(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        addActions();
    }

    private void addActions() {
        this.actionMap.put(Action.CREATE, this::createEntity);
        this.actionMap.put(Action.CREATE_OR_UPDATE, this::createEntity);
        this.actionMap.put(Action.UPDATE, this::createEntity);
        this.actionMap.put(Action.DELETE, this::deleteEntity);
        this.actionMap.put(Action.DELETE_ALL, this::deleteAll);
    }

    @Override
    public boolean hasIndex(String key) {
        return indexKeys.contains(key);
    }

    @Override
    public void addIndex(String key) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            String query = String.format("CREATE INDEX IF NOT EXISTS index_%s ON %s (%s)",
                    key, entityClass.getSimpleName(), key);
            entityManager.createNativeQuery(query).executeUpdate();
            if (!indexKeys.contains(key)) {
                indexKeys.add(key);
            }
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
    }

    @Override
    public void removeIndex(String key) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            String query = String.format("DROP INDEX IF EXISTS index_%s", key);
            entityManager.createNativeQuery(query).executeUpdate();
            indexKeys.remove(key);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
    }

    @Override
    public void addIndexes(Set<String> keySet) {
        Objects.requireNonNull(keySet, "Keys is undefined");
        keySet.forEach(this::addIndex);
    }

    @Override
    public void removeIndexes(Set<String> keySet) {
        Objects.requireNonNull(keySet, "Keys is undefined");
        keySet.forEach(this::removeIndex);
    }

    @Override
    public Optional<List<T>> findByIndex(String key, Object value) {
        Objects.requireNonNull(key, "Key is undefined");
        Objects.requireNonNull(value, "Value is undefined");
        String query = String.format("FROM %s WHERE %s='%s'", entityClass.getSimpleName(), key, value);
        EntityTransaction transaction = entityManager.getTransaction();
        List<T> entities = new ArrayList<>();
        transaction.begin();
        try {
            entities = entityManager.createQuery(query, entityClass).getResultList();
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(entities);
        }
    }

    @Override
    public long count() {
        long result = 0;
        String query = String.format("SELECT COUNT(*) FROM %s %s",
                entityClass.getSimpleName(),
                entityClass.getSimpleName().toLowerCase());
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            result = (Long) entityManager.createQuery(query).getSingleResult();
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return result;
    }

    @Override
    public boolean existsById(ID id) {
        Objects.requireNonNull(id, "ID is undefined");
        boolean result = false;
        T entity = getEntity(id);
        if (entity != null) {
            result = true;
        }
        return result;
    }

    @Override
    public Iterable<T> findAll() {
        List<T> result = new ArrayList<>();
        String query = String.format("from %s as entity", entityClass.getSimpleName());
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            result = entityManager.createQuery(query, entityClass).getResultList();
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return result;
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        Objects.requireNonNull(ids, "IDs is undefined");
        List<T> result = new ArrayList<>();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            ids.forEach(id -> {
                T entity = entityManager.find(entityClass, id);
                if (entity != null) {
                    result.add(entity);
                } else {
                    LOGGER.debug("Entity with id {}, is not found", id);
                }
            });
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return result;
    }

    @Override
    public Optional<T> findById(ID id) {
        Objects.requireNonNull(id, "ID is undefined");
        T entity = getEntity(id);
        if (entity == null) {
            return Optional.empty();
        } else {
            return Optional.of(entity);
        }
    }

    private T getEntity(ID id) {
        T result = null;
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            result = entityManager.find(entityClass, id);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return result;
    }

    @Override
    public Future<?> apply(Action action, Iterable<T> entityList) {
        return actionMap.get(action).apply(entityList);
    }

    private Future<?> createEntity(Iterable<T> entities) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entities.forEach(entityManager::persist);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return SyncFuture.empty();
    }

    private Future<?> deleteEntity(Iterable<T> entities) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entities.forEach(entity -> {
                T persistenceEntity = entityManager.merge(entity);
                entityManager.remove(persistenceEntity);
            });
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return SyncFuture.empty();
    }

    private Future<?> deleteAll(Iterable<T> entities) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            if (entities == null) {
                String query = String.format("FROM %s AS entity", entityClass.getSimpleName());
                entities = entityManager.createQuery(query, entityClass).getResultList();
            }
            entities.forEach(entity -> {
                T persistenceEntity = entityManager.merge(entity);
                entityManager.remove(persistenceEntity);
            });
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return SyncFuture.empty();
    }

    public void close() {
        entityManager.close();
    }
}