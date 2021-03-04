package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.config;


import com.gitlab.hillel.dnepr.java.ee.common.repository.CrudRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.crud_impl.SpringDataCrudImpl;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.Country;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.repository"
})
public class CrudConfig {
    @Bean(name = "countryCrud")
    public CrudRepository<Country, Integer> countrySpringDataCrud() {
        return new SpringDataCrudImpl();
    }
}
