package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common;


import com.gitlab.hillel.dnepr.java.ee.common.repository.WriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

public interface CqrsWriteRepository<T extends BaseEntity<ID>, ID>
        extends WriteRepository<T, ID>, Observable<T, ID> {
}
