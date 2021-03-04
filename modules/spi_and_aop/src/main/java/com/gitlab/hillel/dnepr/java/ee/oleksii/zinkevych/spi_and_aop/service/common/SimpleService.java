package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.service.common;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

public interface SimpleService<T extends BaseEntity<ID>, ID> {
    void printMessage(T entity);
    void throwException() throws IllegalStateException;
}
