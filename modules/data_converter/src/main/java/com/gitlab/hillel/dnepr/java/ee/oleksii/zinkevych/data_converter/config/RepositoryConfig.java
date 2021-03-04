package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.config;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.BaseCqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.cqrs_indexed_repository.PrefixFileCqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository.JdbcCqrsIndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository.JdbcCqrsIndexedReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@Import(ConverterConfig.class)
@PropertySource("classpath:config.properties")
public class RepositoryConfig {
    @Autowired
    ConverterConfig converterConfig;

    @Value("${jdbc.url:jdbc:h2:mem:readTest}")
    String url;

    @Value("${repository.root.path}")
    String repositoryRootPath;

    @Value("${repository.file.extension:.bin}")
    String extension;

    public RepositoryConfig() {
    }

    @Bean(name = "prefixFileCqrsWriteRepository")
    public BaseCqrsWriteRepository<User, String> prefixFileCqrsWriteRepository() {
        return new PrefixFileCqrsWriteRepository<>(repositoryRootPath, User.class, converterConfig.javaObjectSerializer(), extension);
    }

    @Bean(name = "jdbcCqrsIndexedReadRepository")
    public CqrsIndexedReadRepository<User, String> jdbcCqrsIndexedReadRepository() throws SQLException {
        Connection connection = DriverManager.getConnection(url);
        return new JdbcCqrsIndexedReadRepository<>(connection, User.class);
    }

    @Bean(name = "crud")
    public CqrsIndexedCrudRepository<User, String> jdbcCqrsIndexedCrudRepository() throws SQLException {
        return new JdbcCqrsIndexedCrudRepository<>(jdbcCqrsIndexedReadRepository(), prefixFileCqrsWriteRepository());
    }
}