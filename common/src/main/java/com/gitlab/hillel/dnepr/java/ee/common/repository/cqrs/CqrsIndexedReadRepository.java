package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs;

import com.gitlab.hillel.dnepr.java.ee.common.repository.IndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

public interface CqrsIndexedReadRepository<T extends BaseEntity<ID>, ID>
        extends IndexedReadRepository<T, ID>, CqrsReadRepository<T, ID> {
}
