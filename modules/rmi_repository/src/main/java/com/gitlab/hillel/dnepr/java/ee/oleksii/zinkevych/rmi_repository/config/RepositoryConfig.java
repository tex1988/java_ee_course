package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.config;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.remote_wrapper.RemoteCqrsIndexedReadRepositoryWrapper;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.remote_wrapper.RemoteCqrsWriteRepositoryWrapper;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.SpringRmiCqrsIndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsWriteRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {
    ApplicationContext context = new AnnotationConfigApplicationContext(SpringRmiClientConfig.class);

    @Bean(name = "remoteCqrsIndexedReadRepository")
    public RemoteCqrsIndexedReadRepository<User, String> remoteCqrsIndexedReadRepository() {
        return context.getBean(RemoteCqrsIndexedReadRepository.class);
    }

    @Bean(name = "remoteCqrsWriteRepository")
    public RemoteCqrsWriteRepository<User, String> remoteCqrsWriteRepository() {
        return context.getBean(RemoteCqrsWriteRepository.class);
    }

    @Bean(name = "remoteCqrsIndexedReadRepositoryWrapper")
    public CqrsIndexedReadRepository<User, String> remoteCqrsIndexedReadRepositoryWrapper() {
        return new RemoteCqrsIndexedReadRepositoryWrapper<>(remoteCqrsIndexedReadRepository());
    }

    @Bean(name = "remoteCqrsWriteRepositoryWrapper")
    public CqrsWriteRepository<User, String> remoteCqrsWriteRepositoryWrapper() {
        return new RemoteCqrsWriteRepositoryWrapper<>(remoteCqrsWriteRepository());
    }

    @Bean(name = "springRmiCqrsIndexedCrudRepository")
    public CqrsIndexedCrudRepository<User, String> springRmiCqrsIndexedCrudRepository() {
        return new SpringRmiCqrsIndexedCrudRepository<>(remoteCqrsIndexedReadRepositoryWrapper(), remoteCqrsWriteRepositoryWrapper());
    }
}
