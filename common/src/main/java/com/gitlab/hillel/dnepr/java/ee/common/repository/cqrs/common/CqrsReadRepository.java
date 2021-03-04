package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common;

import com.gitlab.hillel.dnepr.java.ee.common.repository.ReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

public interface CqrsReadRepository<T extends BaseEntity<ID>, ID>
        extends ReadRepository<T, ID>, Observer<T, ID> {
}
