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
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

@Configuration
public class SpringRmiClientConfig {
    @Bean
    public RmiProxyFactoryBean writeRepositoryExporter() {
        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
        rmiProxyFactoryBean.setServiceInterface(RemoteCqrsWriteRepository.class);
        rmiProxyFactoryBean.setServiceUrl("rmi://localhost:1099/writeRepository");
        return rmiProxyFactoryBean;
    }

    @Bean
    public RmiProxyFactoryBean readRepositoryExporter() {
        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
        rmiProxyFactoryBean.setServiceInterface(RemoteCqrsIndexedReadRepository.class);
        rmiProxyFactoryBean.setServiceUrl("rmi://localhost:1099/readRepository");
        return rmiProxyFactoryBean;
    }
}
