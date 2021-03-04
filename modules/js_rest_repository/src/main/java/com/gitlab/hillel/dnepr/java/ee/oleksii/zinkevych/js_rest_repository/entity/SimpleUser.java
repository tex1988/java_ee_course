package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.impl.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "simpleuser")
@Getter
@Setter
public class SimpleUser extends AbstractEntity<SimpleUser> {
    private String fName;
    private String lName;
    private int age;

    public SimpleUser() {
    }

    public SimpleUser(String fName, String lName, int age) {
        super();
        this.fName = fName;
        this.lName = lName;
        this.age = age;
        String aggregatedName = fName + lName + age;
        super.setId(String.valueOf(UUID.nameUUIDFromBytes(aggregatedName.getBytes())));
    }

    @Override
    public String toString() {
        return "User{" +
                "fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", age=" + age + '\'' +
                ", id=" + getId() +
                '}';
    }
}
