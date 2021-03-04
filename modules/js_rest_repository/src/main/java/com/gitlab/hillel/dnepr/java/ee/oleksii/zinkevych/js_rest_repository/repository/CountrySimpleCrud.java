package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.IndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.WriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.Country;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.repository.common.AbstractSimpleCrud;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class CountrySimpleCrud extends AbstractSimpleCrud<Country, Integer> {

    @Autowired
    public CountrySimpleCrud(IndexedReadRepository<Country, Integer> readRepository,
                             WriteRepository<Country, Integer> writeRepository) {
        super(readRepository, writeRepository);
    }
}
