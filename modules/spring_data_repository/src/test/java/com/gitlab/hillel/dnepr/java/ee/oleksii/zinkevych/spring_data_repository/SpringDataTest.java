package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.config.PersistenceConfig;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.config.UserServiceConfig;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.service.UserService;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.TestUtils;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpringDataTest {
    private static final User user1 = new User("Ivan", "Ivanov", 30); // ID: 71c61354-e77d-3645-b696-ed70d6bc0a5b
    private static final User user2 = new User("Petr", "Petrov", 40); // ID: d454dba7-997a-38bc-bc48-dc3c642d86b2
    private static final User user3 = new User("Ivan", "Alexandrov", 50); // ID: d0c9962d-59bb-3c6e-a38e-92f6ea1237fa
    private static final User user4 = new User("Petr", "Ivanov", 60); // ID: 06f25d6a-3ec1-379b-9658-bee7477435d8
    private static final User user5 = new User("Alexandr", "Alexandrov", 60); // ID: 85db8c9b-349b-37c3-a217-c4904a1e5b77

    private UserService userService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, LiquibaseException {
        connection = DriverManager.getConnection("jdbc:h2:mem:dbTest");
        runLiquibase();

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(UserServiceConfig.class, PersistenceConfig.class);
        userService = applicationContext.getBean(UserService.class);
    }

    @AfterEach
    void tearDown() throws LiquibaseException, SQLException {
        dropAllLiquibase();
        connection.close();
    }

    @Test
    void getUsersByCityNameTest() {
        String cityName = "Гусь Хрустальный";
        User user = userService.getUsersByCityName(cityName).get(0);
        assertEquals(user4, user);
    }

    @Test
    void getCitiesByCountryIdTest() {
        assertEquals(746, userService.getCitiesByCountryId(9908).size());
    }

    @Test
    void getFirstTenRegionsSortedByFieldInDescendingOrderTest() {
       userService.getFirstTenRegionsSortedByFieldInDescendingOrder("countryCountryId", "name").
               forEach(region -> System.out.println(region.toString()));
    }

    public void runLiquibase() throws LiquibaseException {
        TestUtils.runLiquibase(connection, "/liquibase/data/master.xml");
    }

    public void dropAllLiquibase() throws LiquibaseException {
        TestUtils.dropAllLiquibase(connection, "/liquibase/data/master.xml");
    }

}
