package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.converter;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.config.RepositoryConfig;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.TestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class RepositoryConfigTest {
    private static final User user1 = new User("Ivan", "Ivanov", 30); // ID: 71c61354-e77d-3645-b696-ed70d6bc0a5b
    private static final User user2 = new User("Petr", "Petrov", 40); // ID: d454dba7-997a-38bc-bc48-dc3c642d86b2
    private static final User user3 = new User("Ivan", "Alexandrov", 50); // ID: d0c9962d-59bb-3c6e-a38e-92f6ea1237fa
    private static final User user4 = new User("Petr", "Ivanov", 60); // ID: 06f25d6a-3ec1-379b-9658-bee7477435d8
    private static final User user5 = new User("Alexandr", "Alexandrov", 60); // ID: 85db8c9b-349b-37c3-a217-c4904a1e5b77

    private static final ApplicationContext xmlApplicationContext = new ClassPathXmlApplicationContext("RepositoryApplicationContext.xml");
    private static final ApplicationContext annotationApplicationContext = new AnnotationConfigApplicationContext(RepositoryConfig.class);

    private static final CqrsIndexedCrudRepository<User, String> annContextCrud = annotationApplicationContext.getBean(CqrsIndexedCrudRepository.class);
    private static final CqrsIndexedCrudRepository<User, String> xmlContextCrud = xmlApplicationContext.getBean(CqrsIndexedCrudRepository.class);

    String REPO_ROOT_PATH;

    Connection connection;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/config.properties"));
        REPO_ROOT_PATH = properties.getProperty("repository.root.path");
        connection = DriverManager.getConnection(properties.getProperty("jdbc.url"));
    }

    @AfterEach
    void tearDown() throws IOException, SQLException {
        FileUtils.deleteDirectory(Path.of(REPO_ROOT_PATH).toFile());
        connection.close();
    }

    @ParameterizedTest
    @MethodSource("converterProvider")
    void saveThenDeleteTest(CqrsIndexedCrudRepository<User, String> crud) throws SQLException, InterruptedException {
        File writeRepoEntity = Path
                .of(REPO_ROOT_PATH,
                        "User",
                        "71",
                        "c6",
                        "13",
                        "71c61354-e77d-3645-b696-ed70d6bc0a5b.bin")
                .toFile();
        crud.save(user1);
        Thread.sleep(500);
        assertTrue(writeRepoEntity.exists());
        assertTrue(TestUtils.isEntityPresentInDb(user1, connection));
        crud.delete(user1);
        Thread.sleep(500);
        assertFalse(writeRepoEntity.exists());
        assertFalse(TestUtils.isEntityPresentInDb(user1, connection));
    }

    @ParameterizedTest
    @MethodSource("converterProvider")
    void saveAllThenDeleteAllTest(CqrsIndexedCrudRepository<User, String> crud) throws SQLException, InterruptedException {
        List<User> users = Arrays.asList(user2, user3, user4);
        crud.saveAll(users);
        Thread.sleep(500);
        assertTrue(TestUtils.isEntityPresentInDb(user2, connection));
        assertTrue(TestUtils.isEntityPresentInDb(user3, connection));
        assertTrue(TestUtils.isEntityPresentInDb(user4, connection));
        crud.deleteAll(users);
        Thread.sleep(500);
        assertFalse(TestUtils.isEntityPresentInDb(user2, connection));
        assertFalse(TestUtils.isEntityPresentInDb(user3, connection));
        assertFalse(TestUtils.isEntityPresentInDb(user4, connection));
    }

    private static Stream<Arguments> converterProvider() {
        return Stream.of(
                Arguments.of(annContextCrud),
                Arguments.of(xmlContextCrud));
    }
}