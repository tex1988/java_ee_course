package com.gitlab.hillel.dnepr.java.ee.common.repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

/**
 * https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
 *
 * @param <T>
 * @param <ID>
 */
public interface CrudRepository<T extends BaseEntity<ID>, ID>
        extends ReadRepository<T, ID>, WriteRepository<T, ID> {
}
