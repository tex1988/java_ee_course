package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.repository;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.Country;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountrySpringDataCrud extends CrudRepository<Country, Integer> {
}
