package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.config;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository.JdbcCqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository.JdbcCqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.RemoteCqrsIndexedReadRepositoryImpl;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.RemoteCqrsWriteRepositoryImpl;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsWriteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.remoting.support.RemoteExporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class SpringRmiServerConfig {
    Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");

    public SpringRmiServerConfig() throws SQLException {
    }

    @Bean(name = "jdbcCqrsIndexedReadRepository")
    public CqrsIndexedReadRepository<User, String> jdbcCqrsIndexedReadRepository() {
        return new JdbcCqrsIndexedReadRepository<>(connection, User.class);
    }

    @Bean(name = "jdbcCqrsWriteRepository")
    public CqrsWriteRepository<User, String> jdbcCqrsWriteRepository() {
        return new JdbcCqrsWriteRepository<>(connection, User.class);
    }

    @Bean(name = "remoteJdbcCqrsIndexedReadRepository")
    public RemoteCqrsIndexedReadRepository<User, String> remoteJdbcCqrsIndexedReadRepository() {
        return new RemoteCqrsIndexedReadRepositoryImpl<>(jdbcCqrsIndexedReadRepository());
    }

    @Bean(name = "remoteJdbcCqrsWriteRepository")
    public RemoteCqrsWriteRepository<User, String> remoteJdbcCqrsWriteRepository() {
        return new RemoteCqrsWriteRepositoryImpl<>(jdbcCqrsWriteRepository());
    }

    @Bean
    RemoteExporter readRepositoryRegisterRMIExporter() {
        RmiServiceExporter exporter = new RmiServiceExporter();
        exporter.setServiceName("readRepository");
        exporter.setServiceInterface(RemoteCqrsIndexedReadRepository.class);
        exporter.setService(remoteJdbcCqrsIndexedReadRepository());
        return exporter;
    }

    @Bean
    RemoteExporter writeRepositoryRegisterRMIExporter() {
        RmiServiceExporter exporter = new RmiServiceExporter();
        exporter.setServiceName("writeRepository");
        exporter.setServiceInterface(RemoteCqrsWriteRepository.class);
        exporter.setService(remoteJdbcCqrsWriteRepository());
        return exporter;
    }
}