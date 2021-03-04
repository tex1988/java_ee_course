package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.repository;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionSpringDataCrud extends JpaRepository<Region, Integer> {
}
