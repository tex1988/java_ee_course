package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "countryName", "name"})
public class RegionDto {
    private int id;
    private String name;
    private String countryName;
}