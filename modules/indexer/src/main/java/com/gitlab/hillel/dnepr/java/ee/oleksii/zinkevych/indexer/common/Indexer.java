package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.indexer.common;

import java.util.List;
import java.util.Set;

public interface Indexer<T, ID> {
    boolean hasIndex(String key);

    void addIndex(String key, Iterable<T> entities);

    void removeIndex(String key);

    void addIndexes(Set<String> keySet, Iterable<T> entities);

    void removeIndexes(Set<String> keySet);

    List<String> getEntityIdsByIndex(String key, String value);

    void addEntityToIndexes(T entity);

    void removeEntityFromIndexes(T entity);

    void removeAllIndexes();
}
