package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "countryName", "regionName", "name"})
public class CityDto {
    private int id;
    private String name;
    private String regionName;
    private String countryName;
}