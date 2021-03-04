package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common;

import com.gitlab.hillel.dnepr.java.ee.common.repository.CrudRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

public interface CqrsCrudRepository<T extends BaseEntity<ID>, ID>
        extends CqrsReadRepository<T, ID>, CqrsWriteRepository<T, ID>, CrudRepository<T, ID> {
}
