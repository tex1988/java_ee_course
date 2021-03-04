package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.service.common.SimpleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

public class SpiTest {
    private static final User user1 = new User("Ivan", "Ivanov", 30); // ID: 71c61354-e77d-3645-b696-ed70d6bc0a5b
    private static final User user2 = new User("Petr", "Petrov", 40); // ID: d454dba7-997a-38bc-bc48-dc3c642d86b2

    private static ServiceLoader<SimpleService> simpleServiceServiceLoader = ServiceLoader.load(SimpleService.class);

    private SimpleService<User, String> helloUserService;
    private SimpleService<User, String> goodbyeUserService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        simpleServiceServiceLoader.reload();

        helloUserService = simpleServiceServiceLoader.stream()
                .map(ServiceLoader.Provider::get)
                .filter(service -> service.getClass().getSimpleName().toLowerCase().contains("hello"))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
        goodbyeUserService = simpleServiceServiceLoader.stream()
                .map(ServiceLoader.Provider::get)
                .filter(service -> service.getClass().getSimpleName().toLowerCase().contains("goodbye"))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    @Test
    void helloUserServiceTest() {
        helloUserService.printMessage(user1);
    }

    @Test
    void goodbyeUserServiceTest() {
        goodbyeUserService.printMessage(user1);
    }
}
