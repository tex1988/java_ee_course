package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.repository;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
}
