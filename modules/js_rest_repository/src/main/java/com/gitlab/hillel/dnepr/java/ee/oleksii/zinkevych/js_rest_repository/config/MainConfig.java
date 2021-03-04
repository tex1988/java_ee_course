package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.hillel.dnepr.java.ee.common.repository.IndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.WriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository.JpaCqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository.JpaCqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.City;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.Country;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.Region;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.entity.SimpleUser;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.mapper.IdMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Slf4j
@Configuration
@ComponentScan({"com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository"})
@PropertySource("classpath:config.properties")
@EnableJpaRepositories(basePackages = {"com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.js_rest_repository.repository"})
public class MainConfig {
    private final EntityManager entityManager;

    public MainConfig(@Value("${jdbc.driver:org.h2.Driver}")String jdbcDriverClass) {
        try {
            Class.forName(jdbcDriverClass);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Exception:", e);
        }
        EntityManagerFactory entityManagerFactory =
                Persistence.createEntityManagerFactory("persistence-unit-oz-js-rest-repository");
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Bean("objectMapper")
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean("stringIdMapper")
    public IdMapper<String> stringIdMapper() {
        return new IdMapper<>(String.class);
    }

    @Bean("intIdMapper")
    public IdMapper<Integer> intIdMapper() {
        return new IdMapper<>(Integer.class);
    }

    @Bean
    public LocalEntityManagerFactoryBean entityManagerFactory() {
        LocalEntityManagerFactoryBean factoryBean = new LocalEntityManagerFactoryBean();
        factoryBean.setPersistenceUnitName("persistence-unit-oz-js-rest-repository");
        return factoryBean;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean("cityReadRepository")
    public IndexedReadRepository<City, Integer> cityReadRepository() {
        return new JpaCqrsIndexedReadRepository<>(entityManager, City.class);
    }

    @Bean("cityWriteRepository")
    public WriteRepository<City, Integer> cityWriteRepository() {
        return new JpaCqrsWriteRepository<>(entityManager, City.class);
    }

    @Bean("regionReadRepository")
    public IndexedReadRepository<Region, Integer> regionReadRepository() {
        return new JpaCqrsIndexedReadRepository<>(entityManager, Region.class);
    }

    @Bean("regionWriteRepository")
    public WriteRepository<Region, Integer> regionWriteRepository() {
        return new JpaCqrsWriteRepository<>(entityManager, Region.class);
    }

    @Bean("countryReadRepository")
    public IndexedReadRepository<Country, Integer> countryReadRepository() {
        return new JpaCqrsIndexedReadRepository<>(entityManager, Country.class);
    }

    @Bean("countryWriteRepository")
    public WriteRepository<Country, Integer> countryWriteRepository() {
        return new JpaCqrsWriteRepository<>(entityManager, Country.class);
    }

    @Bean("simpleUserReadRepository")
    public IndexedReadRepository<SimpleUser, String> simpleUserReadRepository() {
        return new JpaCqrsIndexedReadRepository<>(entityManager, SimpleUser.class);
    }

    @Bean("simpleUserWriteRepository")
    public WriteRepository<SimpleUser, String> simpleUserWriteRepository() {
        return new JpaCqrsWriteRepository<>(entityManager, SimpleUser.class);
    }
}