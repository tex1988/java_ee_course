package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.config;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.repository"
})
public class UserServiceConfig {
    @Bean(name = "userService")
    public UserService userService() {
        return new UserService();
    }
}
