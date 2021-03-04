package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.hillel.dnepr.java.ee.common.repository.IndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.SimpleUser;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.service.common.AbstractSimpleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SimpleUserService extends AbstractSimpleService<SimpleUser, String> {
    @Autowired
    public SimpleUserService(IndexedCrudRepository<SimpleUser, String> repository, ObjectMapper objectMapper) {
        super(repository, objectMapper, SimpleUser.class);
    }

    public Iterable<SimpleUser> findAll() {
        return repository.findAll();
    }
}
