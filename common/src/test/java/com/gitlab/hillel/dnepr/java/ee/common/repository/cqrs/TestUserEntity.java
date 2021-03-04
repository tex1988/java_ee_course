package com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestUserEntity implements BaseEntity<String> {
    private String id;

    private String fName;
    private String mName;
    private String lName;
    private int age;
}
