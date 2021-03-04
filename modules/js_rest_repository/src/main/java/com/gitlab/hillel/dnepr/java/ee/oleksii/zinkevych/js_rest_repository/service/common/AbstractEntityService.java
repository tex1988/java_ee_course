package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.exception.ServiceEntityException;
import org.springframework.data.repository.CrudRepository;

public abstract class AbstractEntityService<T extends BaseEntity<ID>, ID, DTO> implements EntityService<ID> {
    protected final CrudRepository<T, ID> repository;
    protected final ObjectMapper objectMapper;
    protected final Class<T> entityClass;
    protected final Class<DTO> dtoClass;

    public AbstractEntityService(CrudRepository<T, ID> repository, ObjectMapper objectMapper, Class<T> entityClass, Class<DTO> dtoClass) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    @Override
    public void saveEntity(String requestBody) throws Exception {
        DTO dto = objectMapper.readValue(requestBody, dtoClass);
        T entity = convertDtoToEntity(dto);
        if (repository.existsById(entity.getId())) {
            throw new ServiceEntityException("Entity with id: " + entity.getId() + " already exists");
        }
        repository.save(entity);
    }

    @Override
    public void updateEntity(String requestBody) throws Exception {
        DTO dto = objectMapper.readValue(requestBody, dtoClass);
        T entity = convertDtoToEntity(dto);
        if (!repository.existsById(entity.getId())) {
            throw new IllegalArgumentException("Entity with id: " + entity.getId() + " not found");
        }
        repository.deleteById(entity.getId());
        repository.save(entity);
    }

    @Override
    public String getEntity(ID id) throws Exception {
        String result;
        T entity = repository.findById(id).orElseThrow(() -> new ServiceEntityException("Entity with id: " + id + " not found"));
        DTO dto = convertEntityToDto(entity);
        result = objectMapper.writeValueAsString(dto);
        return result;
    }

    @Override
    public void deleteEntity(ID id) throws Exception {
        repository.findById(id).orElseThrow(() -> new ServiceEntityException("User with id: " + id + " not found"));
        repository.deleteById(id);
    }

    protected abstract T convertDtoToEntity(DTO dto);

    protected abstract DTO convertEntityToDto(T entity);
}
