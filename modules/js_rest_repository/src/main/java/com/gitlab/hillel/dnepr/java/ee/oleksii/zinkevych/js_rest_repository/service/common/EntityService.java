package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.common;

public interface EntityService<ID> {
    void saveEntity(String requestBody) throws Exception;

    void updateEntity(String requestBody) throws Exception;

    String getEntity(ID id) throws Exception;

    void deleteEntity(ID id) throws Exception;
}
