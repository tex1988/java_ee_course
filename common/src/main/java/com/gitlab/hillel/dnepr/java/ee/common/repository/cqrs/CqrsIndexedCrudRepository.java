package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

public interface CqrsIndexedCrudRepository<T extends BaseEntity<ID>, ID>
        extends CqrsIndexedReadRepository<T, ID>, CqrsCrudRepository<T, ID> {
}
