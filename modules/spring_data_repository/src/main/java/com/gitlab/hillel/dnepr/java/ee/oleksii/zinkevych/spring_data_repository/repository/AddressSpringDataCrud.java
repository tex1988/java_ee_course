package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.repository;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressSpringDataCrud extends CrudRepository<Address, Integer> {
}
