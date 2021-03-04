package com.gitlab.hillel.dnepr.java.ee.common.repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

public interface WriteRepository<T extends BaseEntity<ID>, ID>
        extends AutoCloseable {
    /**
     * Deletes a given entity.
     *
     * @param entity Entity to delete
     */
    void delete(T entity);

    /**
     * Deletes all entities managed by the repository.
     */
    void deleteAll();

    /**
     * Deletes the given entities.
     *
     * @param entities Entities to delete
     */
    void deleteAll(Iterable<? extends T> entities);

    /**
     * Deletes the entity with the given id.
     *
     * @param id Entity ID to delete
     */
    void deleteById(ID id);

    /**
     * Saves a given entity.
     *
     * @param entity Entity to save
     * @param <S>    Saved entity type
     * @return Old entity if not null OR saved entity
     */
    <S extends T> S save(S entity);

    /**
     * Saves all given entities.
     *
     * @param entities Entities to save
     * @param <S>      Saved entity type
     * @return Old entities if not null OR saved entities
     */
    <S extends T> Iterable<S> saveAll(Iterable<S> entities);
}
