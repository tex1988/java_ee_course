package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.mapper.common;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.mapper.MapperException;

public interface Mapper<ID> {
    ID map(String id) throws MapperException;
}