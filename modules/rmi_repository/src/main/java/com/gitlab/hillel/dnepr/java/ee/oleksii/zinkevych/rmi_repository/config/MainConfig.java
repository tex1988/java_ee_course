package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.config;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository.JdbcCqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository.JdbcCqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.RemoteCqrsIndexedReadRepositoryImpl;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.RemoteCqrsWriteRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class MainConfig {
    Connection readConnection = DriverManager.getConnection("jdbc:h2:mem:readTest");
    Connection writeConnection = DriverManager.getConnection("jdbc:h2:mem:writeTest");

    public MainConfig() throws SQLException {
    }

    @Bean(name = "jdbcCqrsIndexedReadRepository")
    public CqrsIndexedReadRepository<User, String> jdbcCqrsIndexedReadRepository() {
        return new JdbcCqrsIndexedReadRepository<>(readConnection, User.class);
    }

    @Bean(name = "jdbcCqrsWriteRepository")
    public CqrsWriteRepository<User, String> jdbcCqrsWriteRepository() {
        return new JdbcCqrsWriteRepository<>(writeConnection, User.class);
    }

    @Bean(name = "remoteJdbcCqrsIndexedReadRepository")
    public RemoteCqrsIndexedReadRepository<User, String> remoteJdbcCqrsIndexedReadRepository() {
        return new RemoteCqrsIndexedReadRepositoryImpl<>(jdbcCqrsIndexedReadRepository());
    }

    @Bean(name = "remoteJdbcCqrsWriteRepository")
    public RemoteCqrsWriteRepository<User, String> remoteJdbcCqrsWriteRepository() {
        return new RemoteCqrsWriteRepositoryImpl<>(jdbcCqrsWriteRepository());
    }
}
