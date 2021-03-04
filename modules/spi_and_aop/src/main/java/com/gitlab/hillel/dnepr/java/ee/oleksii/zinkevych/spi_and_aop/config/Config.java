package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.config;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository.JpaCqrsIndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository.JpaCqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository.JpaCqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan("com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository," +
                "com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_repository")
public class Config {
    private final EntityManager readEntityManager;
    private final EntityManager writeEntityManager;

    public Config() {
        EntityManagerFactory readEntityManagerFactory =
                Persistence.createEntityManagerFactory("persistence-unit-oz-spi-repository-read");
        EntityManagerFactory writeEntityManagerFactory =
                Persistence.createEntityManagerFactory("persistence-unit-oz-spi-repository-write");
        readEntityManager = readEntityManagerFactory.createEntityManager();
        writeEntityManager = writeEntityManagerFactory.createEntityManager();
    }

    @Bean(name = "jpaCqrsIndexedReadRepository")
    public CqrsIndexedReadRepository<User, String> jpaCqrsIndexedReadRepository() {
        return new JpaCqrsIndexedReadRepository<>(readEntityManager, User.class);
    }

    @Bean(name = "jpaCqrsWriteRepository")
    public CqrsWriteRepository<User, String> jpaCqrsWriteRepository() {
        return new JpaCqrsWriteRepository<>(writeEntityManager, User.class);
    }

    @Bean(name = "jpaCqrsIndexedCrudRepository")
    public CqrsIndexedCrudRepository<User, String> jpaCqrsIndexedCrudRepository() {
        return new JpaCqrsIndexedCrudRepository<>(jpaCqrsIndexedReadRepository(), jpaCqrsWriteRepository());
    }
}
