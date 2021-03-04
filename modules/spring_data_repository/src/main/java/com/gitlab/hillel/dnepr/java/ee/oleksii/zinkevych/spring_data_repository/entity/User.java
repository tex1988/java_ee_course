package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity;


import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.impl.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity(name = "user_entity")
@Table(name = "userentity")
@Getter
@Setter
public class User extends AbstractEntity<User> {
    private String fName;
    private String lName;
    private int age;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

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
                ", id=" + getId() + '\'' +
                ", address=" + getAddress().toString() +
                '}';
    }
}