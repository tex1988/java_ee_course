package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_criteria_repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.SyncFuture;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.CriteriaUtils;
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
public class JpaCriteriaCqrsIndexedReadRepository<T extends BaseEntity<ID>, ID>
        implements CqrsIndexedReadRepository<T, ID> {
    private final Map<Action, Function<Iterable<T>, Future<?>>> actionMap = new HashMap<>();
    private final List<String> indexKeys = new ArrayList<>();
    private final EntityManager entityManager;
    private final Class<T> entityClass;
    private final CriteriaUtils<T, ID> criteriaUtils;

    public JpaCriteriaCqrsIndexedReadRepository(EntityManager entityManager, Class<T> entityClass, Class<ID> idClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.criteriaUtils = new CriteriaUtils<>(entityClass, idClass, entityManager);
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
        List<T> entities = criteriaUtils.getEntityByIndex(key, value);
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(entities);
        }
    }

    @Override
    public long count() {
        return criteriaUtils.getEntityCount();
    }

    @Override
    public boolean existsById(ID id) {
        Objects.requireNonNull(id, "ID is undefined");
        boolean result = false;
        List<T> entities = criteriaUtils.getEntitiesById(id);
        if (!entities.isEmpty()) {
            result = true;
        }
        return result;
    }

    @Override
    public Iterable<T> findAll() {
        return criteriaUtils.getAllEntities();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        Objects.requireNonNull(ids, "IDs is undefined");
        List<T> result = new ArrayList<>();
        ids.forEach(id -> {
            List<T> entities = criteriaUtils.getEntitiesById(id);
            result.addAll(entities);
        });
        return result;
    }

    @Override
    public Optional<T> findById(ID id) {
        Objects.requireNonNull(id, "ID is undefined");
        List<T> entities = criteriaUtils.getEntitiesById(id);
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(entities.get(0));
        }
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
        entities.forEach(criteriaUtils::deleteEntity);
        return SyncFuture.empty();
    }

    private Future<?> deleteAll(Iterable<T> entities) {
        if (entities == null) {
            entities = criteriaUtils.getAllEntities();
        }
        try {
            entities.forEach(criteriaUtils::deleteEntity);
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
        }
        return SyncFuture.empty();
    }

    public void close() {
        entityManager.close();
    }
}