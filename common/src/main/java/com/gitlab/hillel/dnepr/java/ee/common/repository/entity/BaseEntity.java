package com.gitlab.hillel.dnepr.java.ee.common.repository.entity;

import java.io.Serializable;

public interface BaseEntity<ID> extends Serializable {
    ID getId();
}
