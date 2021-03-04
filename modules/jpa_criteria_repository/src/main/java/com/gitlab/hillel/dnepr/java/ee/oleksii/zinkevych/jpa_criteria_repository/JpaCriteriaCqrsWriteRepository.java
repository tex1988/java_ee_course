package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_criteria_repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.BaseCqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.CriteriaUtils;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class JpaCriteriaCqrsWriteRepository<T extends BaseEntity<ID>, ID>
        extends BaseCqrsWriteRepository<T, ID> {
    private final EntityManager entityManager;
    private final CriteriaUtils<T, ID> criteriaUtils;

    public JpaCriteriaCqrsWriteRepository(EntityManager entityManager, Class<T> entityClass, Class<ID> idClass) {
        this.entityManager = entityManager;
        this.criteriaUtils = new CriteriaUtils<>(entityClass, idClass, entityManager);
    }

    @Override
    public void delete(T entity) {
        Objects.requireNonNull(entity, "Entity is undefined");
        criteriaUtils.deleteEntity(entity);
        List<T> entities = Collections.singletonList(entity);
        onChange(Observer.Action.DELETE, entities);
    }

    @Override
    public void deleteAll() {
        List<T> entities = criteriaUtils.getAllEntities();
        entities.forEach(criteriaUtils::deleteEntity);
        onChange(Observer.Action.DELETE_ALL, entities);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        List<T> entitiesList = new ArrayList<>();
        Objects.requireNonNull(entities, "Entities is undefined");
        entities.forEach(criteriaUtils::deleteEntity);
        entities.forEach(entitiesList::add);
        onChange(Observer.Action.DELETE, entitiesList);
    }

    @Override
    public void deleteById(ID id) {
        Objects.requireNonNull(id, "ID is undefined");
        T entity = criteriaUtils.getEntitiesById(id).get(0);
        if (entity != null) {
            criteriaUtils.deleteEntity(entity);
            List<T> entities = Collections.singletonList(entity);
            onChange(Observer.Action.DELETE, entities);
        }
    }

    @Override
    public <S extends T> S save(S entity) {
        Objects.requireNonNull(entity, "Entity is undefined");
        EntityTransaction transaction = entityManager.getTransaction();
        List<T> entities = new ArrayList<>();
        entities.add(entity);
        transaction.begin();
        try {
            entityManager.persist(entity);
            entityManager.flush();
            entityManager.detach(entity);
            onChange(Observer.Action.CREATE, entities);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        Objects.requireNonNull(entities, "Entities is undefined");
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entities.forEach(entity -> {
                entityManager.persist(entity);
                entityManager.flush();
                entityManager.detach(entity);
            });
            List<T> entityList = new ArrayList<>();
            entities.forEach(entityList::add);
            onChange(Observer.Action.CREATE, entityList);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return entities;
    }

    @Override
    public void close() {
        entityManager.close();
    }
}