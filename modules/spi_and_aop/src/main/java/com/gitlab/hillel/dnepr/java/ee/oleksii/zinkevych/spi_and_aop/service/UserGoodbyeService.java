package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.service;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.service.common.SimpleService;
import org.springframework.stereotype.Service;

@Service("goodbyeSimpleService")
public class UserGoodbyeService implements SimpleService<User, String> {
    @Override
    public void printMessage(User entity) {
        String message = String.format("Goodbye, %s!", entity.getFName());
        System.out.println(message);
    }

    @Override
    public void throwException() throws IllegalStateException {
        throw new IllegalStateException("bla bla");
    }
}
