package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.entity;


import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.impl.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "userentity")
public class User extends AbstractEntity<User> {
    @Getter
    @Setter
    private String fName;

    @Getter
    @Setter
    private String lName;

    @Getter
    @Setter
    private int age;

    public User() {
    }

    public User(String fName, String lName, int age) {
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
