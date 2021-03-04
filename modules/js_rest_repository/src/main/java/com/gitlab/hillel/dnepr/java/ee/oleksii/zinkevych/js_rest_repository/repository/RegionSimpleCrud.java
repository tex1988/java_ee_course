package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.IndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.WriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.Region;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.repository.common.AbstractSimpleCrud;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class RegionSimpleCrud extends AbstractSimpleCrud<Region, Integer> {

    @Autowired
    public RegionSimpleCrud(IndexedReadRepository<Region, Integer> readRepository,
                            WriteRepository<Region, Integer> writeRepository) {
        super(readRepository, writeRepository);
    }
}
