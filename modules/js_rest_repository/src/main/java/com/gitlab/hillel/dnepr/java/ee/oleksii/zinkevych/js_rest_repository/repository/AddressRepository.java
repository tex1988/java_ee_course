package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.repository;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<Address, Integer> {

    List<Address> findAddressByCityNameAndStreet(String city_name, String street);
}
