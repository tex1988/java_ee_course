package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "fname", "lname", "age", "address"})
public class UserDto {
    private String id;
    private String fName;
    private String lName;
    private int age;
    private String address;
}