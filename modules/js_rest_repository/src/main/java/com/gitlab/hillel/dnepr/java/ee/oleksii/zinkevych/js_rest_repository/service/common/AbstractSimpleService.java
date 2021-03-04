package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.hillel.dnepr.java.ee.common.repository.IndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.exception.ServiceEntityException;

public abstract class AbstractSimpleService<T extends BaseEntity<ID>, ID> implements EntityService<ID> {
    protected final IndexedCrudRepository<T, ID> repository;
    protected final ObjectMapper objectMapper;
    protected final Class<T> entityClass;

    public AbstractSimpleService(IndexedCrudRepository<T, ID> repository, ObjectMapper objectMapper, Class<T> entityClass) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.entityClass = entityClass;
    }

    @Override
    public void saveEntity(String requestBody) throws Exception {
        T entity = objectMapper.readValue(requestBody, entityClass);
        if (repository.existsById(entity.getId())) {
            throw new ServiceEntityException(entity.getClass().getSimpleName() + " with id: " + entity.getId() + " already exists");
        }
        repository.save(entity);
    }

    @Override
    public void updateEntity(String requestBody) throws Exception {
        T entity = objectMapper.readValue(requestBody, entityClass);
        if (!repository.existsById(entity.getId())) {
            throw new ServiceEntityException(entity.getClass().getSimpleName() + " with id: " + entity.getId() + " not found");
        }
        repository.deleteById(entity.getId());
        repository.save(entity);
    }

    @Override
    public String getEntity(ID id) throws Exception {
        String result;
        T entity = repository.findById(id).orElseThrow(() -> new ServiceEntityException("Entity with id: " + id + " not found"));
        result = objectMapper.writeValueAsString(entity);
        return result;
    }

    @Override
    public void deleteEntity(ID id) throws Exception {
        repository.findById(id).orElseThrow(() -> new ServiceEntityException("Entity with id: " + id + " not found"));
        repository.deleteById(id);
    }
}
