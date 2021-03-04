package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Id;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CriteriaUtils<T extends BaseEntity<ID>, ID> {
    private final Class<T> entityClass;
    private final Class<ID> idClass;
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaUtils(Class<T> entityClass, Class<ID> idClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        this.idClass = idClass;
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public List<T> getEntitiesById(ID id) {
        List<T> result = new ArrayList<>();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            Field idField = ReflectionUtils.getAnnotatedField(entityClass, Id.class);
            String idFieldName = idField.getName();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
            Root<T> root = criteriaQuery.from(entityClass);
            ParameterExpression<ID> parameter = criteriaBuilder.parameter(idClass);
            criteriaQuery.select(root).where(criteriaBuilder.equal(root.get(idFieldName), parameter));
            TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
            query.setParameter(parameter, id);
            result = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return result;
    }

    public List<T> getEntityByIndex(String key, Object value) {
        List<T> entities = new ArrayList<>();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
            Root<T> root = criteriaQuery.from(entityClass);
            ParameterExpression<Object> parameter = criteriaBuilder.parameter(Object.class);
            try {
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get(key), parameter));
            } catch (IllegalArgumentException e) {
                Field keyField = ReflectionUtils.getAnnotatedField(entityClass, Column.class, "name", key);
                String keyFieldName = keyField.getName();
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get(keyFieldName), parameter));
            }
            TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
            query.setParameter(parameter, value);
            entities = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return entities;
    }

    public List<T> getAllEntities() {
        List<T> result = new ArrayList<>();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
            Root<T> root = criteriaQuery.from(entityClass);
            criteriaQuery.select(root);
            TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
            result = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return result;
    }

    public void deleteEntity(T entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            T persistenceEntity = entityManager.merge(entity);
            entityManager.remove(persistenceEntity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
    }

    public long getEntityCount() {
        long result = 0;
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(entityClass)));
            result = entityManager.createQuery(criteriaQuery).getSingleResult();
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            transaction.rollback();
        }
        return result;
    }
}
