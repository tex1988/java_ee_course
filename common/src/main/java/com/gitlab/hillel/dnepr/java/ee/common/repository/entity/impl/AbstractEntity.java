package com.gitlab.hillel.dnepr.java.ee.common.repository.entity.impl;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class AbstractEntity<T extends BaseEntity<String>> implements BaseEntity<String> {
    @Id
    private String id;

    public static BaseEntity<String> of(String id) {
        return new AbstractEntity<>() {
            {
                this.setId(id);
            }
        };
    }
}
