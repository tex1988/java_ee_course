package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class BaseCqrsIndexedCrudRepository<T extends BaseEntity<ID>, ID>
        extends BaseCqrsCrudRepository<T, ID> implements CqrsIndexedCrudRepository<T, ID> {
    protected BaseCqrsIndexedCrudRepository(
            CqrsIndexedReadRepository<T, ID> readRepository,
            CqrsWriteRepository<T, ID> writeRepository) {
        super(readRepository, writeRepository);
    }

    @Override
    public boolean hasIndex(String key) {
        return getReadRepository().hasIndex(key);
    }

    @Override
    public void addIndex(String key) {
        getReadRepository().addIndex(key);
    }

    @Override
    public void removeIndex(String key) {
        getReadRepository().removeIndex(key);
    }

    @Override
    public void addIndexes(Set<String> keySet) {
        getReadRepository().addIndexes(keySet);
    }

    @Override
    public void removeIndexes(Set<String> keySet) {
        getReadRepository().removeIndexes(keySet);
    }

    @Override
    public Optional<List<T>> findByIndex(String key, Object value) {
        return getReadRepository().findByIndex(key, value);
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        final List<T> result = new ArrayList<>();
        ids.forEach(id -> findById(id).ifPresent(result::add));
        return result;
    }

    private CqrsIndexedReadRepository<T, ID> getReadRepository() {
        return ((CqrsIndexedReadRepository<T, ID>) readRepository);
    }
}
