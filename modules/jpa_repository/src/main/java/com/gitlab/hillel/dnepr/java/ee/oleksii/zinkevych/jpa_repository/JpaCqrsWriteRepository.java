package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.BaseCqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class JpaCqrsWriteRepository<T extends BaseEntity<ID>, ID>
        extends BaseCqrsWriteRepository<T, ID> {
    private final EntityManager entityManager;
    private final Class<T> entityClass;

    public JpaCqrsWriteRepository(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    @Override
    public void delete(T entity) {
        Objects.requireNonNull(entity, "Entity is undefined");
        EntityTransaction transaction = entityManager.getTransaction();
        List<T> entities = new ArrayList<>();
        entities.add(entity);
        transaction.begin();
        try {
            T persistenceEntity = entityManager.merge(entity);
            entityManager.remove(persistenceEntity);
            transaction.commit();
            onChange(Observer.Action.DELETE, entities);
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
    }

    @Override
    public void deleteAll() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            String query = String.format("FROM %s AS entity", entityClass.getSimpleName());
            List<T> entities = entityManager.createQuery(query, entityClass).getResultList();
            entities.forEach(entity -> {
                T persistenceEntity = entityManager.merge(entity);
                entityManager.remove(persistenceEntity);
            });
            onChange(Observer.Action.DELETE_ALL, entities);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        Objects.requireNonNull(entities, "Entities is undefined");
        EntityTransaction transaction = entityManager.getTransaction();
        List<T> entityList = new ArrayList<>();
        entities.forEach(entityList::add);
        transaction.begin();
        try {
            entities.forEach(entity -> {
                T persistenceEntity = entityManager.merge(entity);
                entityManager.remove(persistenceEntity);
            });
            transaction.commit();
            onChange(Observer.Action.DELETE, entityList);
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
    }

    @Override
    public void deleteById(ID id) {
        Objects.requireNonNull(id, "ID is undefined");
        T entity = entityManager.find(entityClass, id);
        List<T> entities = new ArrayList<>();
        entities.add(entity);
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.remove(entity);
            transaction.commit();
            onChange(Observer.Action.DELETE, entities);
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
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
            entities.forEach(entity->{
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