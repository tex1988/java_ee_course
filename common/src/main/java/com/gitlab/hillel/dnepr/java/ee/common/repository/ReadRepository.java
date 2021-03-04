package com.gitlab.hillel.dnepr.java.ee.common.repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

import java.util.Optional;

public interface ReadRepository<T extends BaseEntity<ID>, ID>
        extends AutoCloseable {
    /**
     * @return Returns the number of entities available.
     */
    long count();

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id Entity ID to check existing
     * @return True if exists
     */
    boolean existsById(ID id);

    /**
     * Returns all instances of the type.
     *
     * @return Found entity
     */
    Iterable<T> findAll();

    /**
     * Returns all instances of the type T with the given IDs.
     *
     * @param ids Entity IDs to find
     * @return Found entities
     */
    Iterable<T> findAllById(Iterable<ID> ids);

    /**
     * Retrieves an entity by its id.
     *
     * @param id Entity ID to find
     * @return Found entity
     */
    Optional<T> findById(ID id);
}
