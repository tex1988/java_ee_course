package com.gitlab.hillel.dnepr.java.ee.common.repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IndexedReadRepository<T extends BaseEntity<ID>, ID>
        extends ReadRepository<T, ID> {
    boolean hasIndex(String key);

    void addIndex(String key);

    void removeIndex(String key);

    void addIndexes(Set<String> keySet);

    void removeIndexes(Set<String> keySet);

    Optional<List<T>> findByIndex(String key, Object value);
}
