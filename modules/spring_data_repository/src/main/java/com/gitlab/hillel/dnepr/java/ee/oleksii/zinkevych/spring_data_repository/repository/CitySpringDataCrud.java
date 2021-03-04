package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.repository;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.City;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitySpringDataCrud extends CrudRepository<City, Integer> {
    @Query("select city from city_entity city where city.country.countryId = ?1")
    List<City> findCitiesByCountryId(int countryId);
}
