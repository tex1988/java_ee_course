package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.config.Config;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spi_and_aop.service.common.SimpleService;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.*;

public class AopTest {
    private static final User user1 = new User("Ivan", "Ivanov", 30); // ID: 71c61354-e77d-3645-b696-ed70d6bc0a5b
    private static final User user2 = new User("Petr", "Petrov", 40); // ID: d454dba7-997a-38bc-bc48-dc3c642d86b2
    private static final User user3 = new User("Ivan", "Alexandrov", 50); // ID: d0c9962d-59bb-3c6e-a38e-92f6ea1237fa
    private static final User user4 = new User("Petr", "Ivanov", 60); // ID: 06f25d6a-3ec1-379b-9658-bee7477435d8
    private static final User user5 = new User("Alexandr", "Alexandrov", 60); // ID: 85db8c9b-349b-37c3-a217-c4904a1e5b77

    private CqrsIndexedCrudRepository<User, String> crud;

    private Connection readConnection;
    private Connection writeConnection;

    private SimpleService<User, String> helloUserService;
    private SimpleService<User, String> goodbyeUserService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() throws SQLException, InterruptedException {
        readConnection = DriverManager.getConnection("jdbc:h2:mem:readTest");
        writeConnection = DriverManager.getConnection("jdbc:h2:mem:writeTest");
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        helloUserService = (SimpleService<User, String>) context.getBean("helloSimpleService");
        goodbyeUserService = (SimpleService<User, String>) context.getBean("goodbyeSimpleService");
        crud = context.getBean(CqrsIndexedCrudRepository.class);
        crud.saveAll(new HashSet<>(Arrays.asList(user1, user2, user4)));
        Thread.sleep(500);
    }

    @AfterEach
    void tearDown() throws SQLException {
        readConnection.createStatement().executeUpdate("TRUNCATE TABLE userentity");
        writeConnection.createStatement().executeUpdate("TRUNCATE TABLE userentity");
        readConnection.close();
        writeConnection.close();
    }

    @Test
    void hasIndex() {
        String key = "lName";
        assertFalse(crud.hasIndex(key));
        crud.addIndex(key);
        assertTrue(crud.hasIndex(key));
        crud.removeIndex(key);
        assertFalse(crud.hasIndex(key));
    }

    @Test
    void deleteAll() throws SQLException, InterruptedException {
        assertTrue(isEntityPresentInAllDbs(user1));
        assertTrue(isEntityPresentInAllDbs(user4));
        crud.deleteAll();
        TimeUnit.SECONDS.sleep(1);
        assertFalse(isEntityPresentInAllDbs(user1));
        assertFalse(isEntityPresentInAllDbs(user4));
    }

    @Test
    void delete() throws SQLException, InterruptedException {
        assertTrue(isEntityPresentInAllDbs(user1));
        crud.delete(user1);
        TimeUnit.SECONDS.sleep(1);
        assertFalse(isEntityPresentInAllDbs(user1));
    }

    @Test
    void save() throws SQLException, InterruptedException {
        assertFalse(isEntityPresentInAllDbs(user5));
        crud.save(user5);
        TimeUnit.SECONDS.sleep(1);
        assertTrue(isEntityPresentInAllDbs(user5));
    }

    @Test
    void saveAll() throws SQLException, InterruptedException {
        assertFalse(isEntityPresentInAllDbs(user3));
        assertFalse(isEntityPresentInAllDbs(user5));
        List<User> userList = Arrays.asList(user3, user5);
        crud.saveAll(userList);
        TimeUnit.SECONDS.sleep(1);
        assertTrue(isEntityPresentInAllDbs(user3));
        assertTrue(isEntityPresentInAllDbs(user5));
    }

    @Test
    void userServiceAopTest() {
        helloUserService.printMessage(user1);
        goodbyeUserService.printMessage(user1);
    }

    @Test
    void throwExceptionTest() {
        assertThrows(IllegalStateException.class, () -> helloUserService.throwException());
    }

    private boolean isEntityPresentInAllDbs(BaseEntity<?> entity) throws SQLException {
        boolean result = false;
        boolean writeConnectionResult = TestUtils.isEntityPresentInDb(entity, writeConnection);
        boolean readConnectionResult = TestUtils.isEntityPresentInDb(entity, readConnection);
        if (writeConnectionResult && readConnectionResult) {
            result = true;
        }
        return result;
    }
}
