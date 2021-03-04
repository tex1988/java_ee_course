package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.db_migration;

;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.impl.AbstractEntity;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class User extends AbstractEntity<User> {
    private final String fName;
    private final String lName;
    private final int age;
    private final Date birthDate;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public User() throws ParseException {
        super();
        this.fName = "";
        this.lName = "";
        this.age = 0;
        this.birthDate = dateFormat.parse("1990-01-01");
        String aggregatedName = fName + lName + age;
        super.setId(String.valueOf(UUID.nameUUIDFromBytes(aggregatedName.getBytes())));
    }

    public User(String fName, String lName, int age) throws ParseException {
        super();
        this.fName = fName;
        this.lName = lName;
        this.age = age;
        this.birthDate = dateFormat.parse("1980-12-31");
        String aggregatedName = fName + lName + age;
        super.setId(String.valueOf(UUID.nameUUIDFromBytes(aggregatedName.getBytes())));
    }

    public User(String fName, String lName, int age, String date) throws ParseException {
        super();
        this.fName = fName;
        this.lName = lName;
        this.age = age;
        this.birthDate = dateFormat.parse(date);
        String aggregatedName = fName + lName + age;
        super.setId(String.valueOf(UUID.nameUUIDFromBytes(aggregatedName.getBytes())));
    }

    @Override
    public String toString() {
        return "User{" +
                "fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", age='" + age + '\'' +
                ", birthDate='" + dateFormat.format(birthDate) + '\'' +
                ", id='" + this.getId() +
                '}';
    }
}
