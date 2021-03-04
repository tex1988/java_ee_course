package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.repository;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSpringDataCrud extends CrudRepository<User, String> {
    List<User> findUsersByAddressCityName(String cityName);
}
