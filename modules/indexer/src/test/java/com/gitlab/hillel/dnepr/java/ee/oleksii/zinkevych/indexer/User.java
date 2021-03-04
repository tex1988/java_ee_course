package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.indexer;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.impl.AbstractEntity;

import java.util.UUID;

public class User extends AbstractEntity<User> {
    private final String fName;
    private final String lName;
    private final int age;

    public User() {
        super();
        this.fName = "";
        this.lName = "";
        this.age = 0;
        String aggregatedName = fName + lName + age;
        super.setId(String.valueOf(UUID.nameUUIDFromBytes(aggregatedName.getBytes())));
    }

    public User(String fName, String lName, int age) {
        super();
        this.fName = fName;
        this.lName = lName;
        this.age = age;
        String aggregatedName = fName + lName + age;
        super.setId(String.valueOf(UUID.nameUUIDFromBytes(aggregatedName.getBytes())));
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public int getAge() {
        return age;
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
