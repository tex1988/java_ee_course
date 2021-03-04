package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository;


import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

public class JpaCqrsIndexedCrudRepository<T extends BaseEntity<ID>, ID>
        implements CqrsIndexedCrudRepository<T, ID> {
    private final CqrsIndexedReadRepository<T, ID> readRepository;
    private final CqrsWriteRepository<T, ID> writeRepository;

    public JpaCqrsIndexedCrudRepository(
            CqrsIndexedReadRepository<T, ID> readRepository,
            CqrsWriteRepository<T, ID> writeRepository) {
        this.readRepository = readRepository;
        this.writeRepository = writeRepository;
        this.writeRepository.addObserver(this.readRepository);
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
    public void close() throws Exception {
        readRepository.close();
        writeRepository.close();
    }

    @Override
    public boolean addObserver(Observer<T, ID> observer) {
        return writeRepository.addObserver(observer);
    }

    @Override
    public boolean addObservers(List<Observer<T, ID>> observers) {
        return writeRepository.addObservers(observers);
    }

    @Override
    public List<Observer<T, ID>> getObservers() {
        return writeRepository.getObservers();
    }

    @Override
    public boolean removeObserver(Observer<T, ID> observer) {
        return writeRepository.removeObserver(observer);
    }

    @Override
    public boolean removeObservers(List<Observer<T, ID>> observers) {
        return writeRepository.removeObservers(observers);
    }

    @Override
    public Future<?> apply(Action action, Iterable<T> entities) {
        return readRepository.apply(action, entities);
    }
}
