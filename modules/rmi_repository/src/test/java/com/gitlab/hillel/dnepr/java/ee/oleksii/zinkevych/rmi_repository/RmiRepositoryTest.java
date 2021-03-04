package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.CqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository.JdbcCqrsIndexedCrudRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.entity.User;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.remote_wrapper.RemoteCqrsIndexedReadRepositoryWrapper;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.remote_wrapper.RemoteCqrsWriteRepositoryWrapper;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.reomote_repository.common.RemoteCqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.server.RmiReadRepositoryServer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.server.RmiRegistryServer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.rmi_repository.server.RmiWriteRepositoryServer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class RmiRepositoryTest {
    private final static List<Runnable> servers = Arrays.asList(new RmiRegistryServer(), new RmiReadRepositoryServer(), new RmiWriteRepositoryServer());

    private static final User user1 = new User("Ivan", "Ivanov", 30); // ID: 71c61354-e77d-3645-b696-ed70d6bc0a5b
    private static final User user2 = new User("Petr", "Petrov", 40); // ID: d454dba7-997a-38bc-bc48-dc3c642d86b2
    private static final User user3 = new User("Ivan", "Alexandrov", 50); // ID: d0c9962d-59bb-3c6e-a38e-92f6ea1237fa
    private static final User user4 = new User("Petr", "Ivanov", 60); // ID: 06f25d6a-3ec1-379b-9658-bee7477435d8
    private static final User user5 = new User("Alexandr", "Alexandrov", 60); // ID: 85db8c9b-349b-37c3-a217-c4904a1e5b77

    private static Connection readConnection;
    private static Connection writeConnection;

    private static CqrsIndexedCrudRepository<User, String> crud;

    @BeforeAll
    static void beforeAllSetup() throws IOException, InterruptedException, SQLException, NotBoundException {
        for (Runnable server : servers) {
            Thread thread = new Thread(server);
            thread.setDaemon(true);
            thread.setName("Thread-" + server.getClass().getSimpleName());
            thread.start();
            Thread.sleep(500);
        }

        readConnection = DriverManager.getConnection("jdbc:h2:mem:readTest");
        writeConnection = DriverManager.getConnection("jdbc:h2:mem:writeTest");

        Registry registry = LocateRegistry.getRegistry(63920);
        RemoteCqrsIndexedReadRepository<User, String> readRepositoryStub = (RemoteCqrsIndexedReadRepository<User, String>) registry.lookup("readRepository");
        RemoteCqrsWriteRepository<User, String> writeRepositoryStub = (RemoteCqrsWriteRepository<User, String>) registry.lookup("writeRepository");
        CqrsIndexedReadRepository<User, String> readRepository = new RemoteCqrsIndexedReadRepositoryWrapper<>(readRepositoryStub);
        CqrsWriteRepository<User, String> writeRepository = new RemoteCqrsWriteRepositoryWrapper<>(writeRepositoryStub);
        crud = new JdbcCqrsIndexedCrudRepository<>(readRepository, writeRepository);
    }

    @AfterAll
    static void tearDownAfterAll() throws SQLException {
        readConnection.close();
        writeConnection.close();
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        crud.save(user1);
        crud.save(user4);
        Thread.sleep(200);
    }

    @AfterEach
    void tearDown() throws SQLException {
        readConnection.createStatement().executeUpdate("TRUNCATE TABLE user");
        writeConnection.createStatement().executeUpdate("TRUNCATE TABLE user");
    }

    @Test
    void hasIndexTest() {
        String key = "lName";
        assertFalse(crud.hasIndex(key));
        crud.addIndex(key);
        assertTrue(crud.hasIndex(key));
        crud.removeIndex(key);
        assertFalse(crud.hasIndex(key));
    }

    @Test
    void addIndexTest() {
        String key = "fName";
        crud.addIndex(key);
        assertTrue(crud.hasIndex(key));
    }

    @Test
    void removeIndexTest() {
        String key = "fName";
        crud.addIndex(key);
        assertTrue(crud.hasIndex(key));
        crud.removeIndex(key);
        assertFalse(crud.hasIndex(key));
    }

    @Test
    void addIndexesTest() {
        Set<String> keySet = new HashSet<>();
        assertFalse(crud.hasIndex("fName"));
        assertFalse(crud.hasIndex("lName"));
        assertFalse(crud.hasIndex("age"));
        Collections.addAll(keySet, "fName", "lName", "age");
        crud.addIndexes(keySet);
        assertTrue(crud.hasIndex("fName"));
        assertTrue(crud.hasIndex("lName"));
        assertTrue(crud.hasIndex("age"));
    }

    @Test
    void removeIndexesTest() {
        Set<String> keySet = new HashSet<>();
        Collections.addAll(keySet, "fName", "lName", "age");
        crud.addIndexes(keySet);
        assertTrue(crud.hasIndex("fName"));
        assertTrue(crud.hasIndex("lName"));
        assertTrue(crud.hasIndex("age"));
        crud.removeIndexes(keySet);
        assertFalse(crud.hasIndex("fName"));
        assertFalse(crud.hasIndex("lName"));
        assertFalse(crud.hasIndex("age"));
    }

    @Test
    void findByIndexTest() {
        Optional<List<User>> optional = crud.findByIndex("lName", "Ivanov");
        if (optional.isPresent()) {
            assertEquals(2, optional.get().size());
            assertTrue(user1.getId().equals(optional.get().get(0).getId()) ||
                    user4.getId().equals(optional.get().get(0).getId()));
            assertTrue(user1.getId().equals(optional.get().get(1).getId()) ||
                    user4.getId().equals(optional.get().get(1).getId()));
        } else {
            fail();
        }
    }

    @Test
    void countTest() throws InterruptedException {
        assertEquals(2, crud.count());
        crud.delete(user4);
        TimeUnit.SECONDS.sleep(1);
        assertEquals(1, crud.count());
    }

    @Test
    void existsByIdTest() {
        String idTrue = "71c61354-e77d-3645-b696-ed70d6bc0a5b";
        String idFalse = "85db8c9b-349b-37c3-a217-c4904a1e5b77";
        assertTrue(crud.existsById(idTrue));
        assertFalse(crud.existsById(idFalse));
    }

    @Test
    void findAllTest() {
        List<User> userList = new ArrayList<>();
        crud.findAll().iterator().forEachRemaining(userList::add);
        assertEquals(2, userList.size());
        assertTrue(user1.getId().equals(userList.get(0).getId()) ||
                user4.getId().equals(userList.get(0).getId()));
        assertTrue(user1.getId().equals(userList.get(1).getId()) ||
                user4.getId().equals(userList.get(1).getId()));
    }

    @Test
    void findAllByIdTest() {
        List<String> ids = Arrays.asList("71c61354-e77d-3645-b696-ed70d6bc0a5b",
                "06f25d6a-3ec1-379b-9658-bee7477435d8");
        List<User> userList = new ArrayList<>();
        crud.findAllById(ids).iterator().forEachRemaining(userList::add);
        assertEquals(2, userList.size());
        assertTrue(user1.getId().equals(userList.get(0).getId()) ||
                user4.getId().equals(userList.get(0).getId()));
        assertTrue(user1.getId().equals(userList.get(1).getId()) ||
                user4.getId().equals(userList.get(1).getId()));
    }

    @Test
    void findByIdTest() {
        String id = "71c61354-e77d-3645-b696-ed70d6bc0a5b";
        Optional<User> optional = crud.findById("71c61354-e77d-3645-b696-ed70d6bc0a5b");
        if (optional.isPresent()) {
            assertEquals(id, optional.get().getId());
        } else {
            fail();
        }
    }

    @Test
    void deleteTest() throws SQLException, InterruptedException {
        assertTrue(isEntityPresentInAllDbs(user1));
        crud.delete(user1);
        TimeUnit.SECONDS.sleep(1);
        assertFalse(isEntityPresentInAllDbs(user1));
    }

    @Test
    void deleteAllTest() throws SQLException, InterruptedException {
        assertFalse(TestUtils.isEntityTableIsEmpty(User.class, writeConnection));
        assertFalse(TestUtils.isEntityTableIsEmpty(User.class, readConnection));
        crud.deleteAll();
        TimeUnit.SECONDS.sleep(1);
        assertTrue(TestUtils.isEntityTableIsEmpty(User.class, writeConnection));
        assertTrue(TestUtils.isEntityTableIsEmpty(User.class, readConnection));
    }

    @Test
    void deleteAllParametrized() throws SQLException, InterruptedException {
        assertTrue(isEntityPresentInAllDbs(user1));
        assertTrue(isEntityPresentInAllDbs(user4));
        List<User> userList = Arrays.asList(user1, user4);
        crud.deleteAll(userList);
        TimeUnit.SECONDS.sleep(1);
        assertFalse(isEntityPresentInAllDbs(user1));
        assertFalse(isEntityPresentInAllDbs(user4));
    }

    @Test
    void deleteByIdTest() throws SQLException, InterruptedException {
        assertTrue(isEntityPresentInAllDbs(user4));
        String id = "06f25d6a-3ec1-379b-9658-bee7477435d8";
        crud.deleteById(id);
        TimeUnit.SECONDS.sleep(1);
        assertFalse(isEntityPresentInAllDbs(user4));
    }

    @Test
    void saveTest() throws SQLException, InterruptedException {
        assertFalse(isEntityPresentInAllDbs(user5));
        crud.save(user5);
        TimeUnit.SECONDS.sleep(1);
        assertTrue(isEntityPresentInAllDbs(user5));
    }

    @Test
    void saveAllTest() throws SQLException, InterruptedException {
        assertFalse(isEntityPresentInAllDbs(user2));
        assertFalse(isEntityPresentInAllDbs(user2));
        assertFalse(isEntityPresentInAllDbs(user2));
        List<User> userList = Arrays.asList(user2, user3, user5);
        crud.saveAll(userList);
        TimeUnit.SECONDS.sleep(1);
        assertTrue(isEntityPresentInAllDbs(user2));
        assertTrue(isEntityPresentInAllDbs(user3));
        assertTrue(isEntityPresentInAllDbs(user5));
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