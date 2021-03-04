package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.repository.common;

import com.gitlab.hillel.dnepr.java.ee.common.repository.IndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.IndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.WriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractSimpleCrud<T extends BaseEntity<ID>, ID> implements IndexedCrudRepository<T, ID>, CrudRepository<T, ID> {
    protected final IndexedReadRepository<T, ID> readRepository;
    protected final WriteRepository<T, ID> writeRepository;

    public AbstractSimpleCrud(IndexedReadRepository<T, ID> readRepository, WriteRepository<T, ID> writeRepository) {
        this.readRepository = readRepository;
        this.writeRepository = writeRepository;
    }

    @Override
    public long count() {
        return readRepository.count();
    }

    @Override
    public boolean existsById(ID id) {
        return readRepository.existsById(id);
    }

    @Override
    public Iterable<T> findAll() {
        return readRepository.findAll();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        return readRepository.findAllById(ids);
    }

    @Override
    public Optional<T> findById(ID id) {
        return readRepository.findById(id);
    }

    @Override
    public void delete(T entity) {
        writeRepository.delete(entity);
    }

    @Override
    public void deleteAll() {
        writeRepository.deleteAll();
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        writeRepository.deleteAll(entities);
    }

    @Override
    public void deleteById(ID id) {
        writeRepository.deleteById(id);
    }

    @Override
    public <S extends T> S save(S entity) {
        return writeRepository.save(entity);
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return writeRepository.saveAll(entities);
    }

    @Override
    public boolean hasIndex(String key) {
        return readRepository.hasIndex(key);
    }

    @Override
    public void addIndex(String key) {
        readRepository.addIndex(key);
    }

    @Override
    public void removeIndex(String key) {
        readRepository.removeIndex(key);
    }

    @Override
    public void addIndexes(Set<String> keySet) {
        readRepository.addIndexes(keySet);
    }

    @Override
    public void removeIndexes(Set<String> keySet) {
        readRepository.removeIndexes(keySet);
    }

    @Override
    public Optional<List<T>> findByIndex(String key, Object value) {
        return readRepository.findByIndex(key, value);
    }

    @Override
    public void close() throws Exception {
        writeRepository.close();
        readRepository.close();
    }
}
