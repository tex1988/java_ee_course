package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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

@Slf4j
class JdbcCqrsIndexedCrudRepositoryTest {
    private static final UserEntity user1 = new UserEntity("Ivan", "Ivanov", 30); // ID: 71c61354-e77d-3645-b696-ed70d6bc0a5b
    private static final UserEntity user2 = new UserEntity("Petr", "Petrov", 40); // ID: d454dba7-997a-38bc-bc48-dc3c642d86b2
    private static final UserEntity user3 = new UserEntity("Ivan", "Alexandrov", 50); // ID: d0c9962d-59bb-3c6e-a38e-92f6ea1237fa
    private static final UserEntity user4 = new UserEntity("Petr", "Ivanov", 60); // ID: 06f25d6a-3ec1-379b-9658-bee7477435d8
    private static final UserEntity user5 = new UserEntity("Alexandr", "Alexandrov", 60); // ID: 85db8c9b-349b-37c3-a217-c4904a1e5b77

    JdbcCqrsIndexedCrudRepository<UserEntity, String> crud;

    private static Connection readConnection;
    private static Connection writeConnection;

    @BeforeEach
    void setUp() throws SQLException, InterruptedException {
        readConnection = DriverManager.getConnection("jdbc:h2:mem:readTest");
        writeConnection = DriverManager.getConnection("jdbc:h2:mem:writeTest");
        //readConnection = DriverManager.getConnection("jdbc:postgresql://localhost/test?user=postgres&password=&ssl=false");
        //writeConnection = DriverManager.getConnection("jdbc:postgresql://localhost/test?user=postgres&password=&ssl=false");
        JdbcCqrsWriteRepository<UserEntity, String> writeRepo = new JdbcCqrsWriteRepository<>(writeConnection, UserEntity.class);
        JdbcCqrsIndexedReadRepository<UserEntity, String> readRepo = new JdbcCqrsIndexedReadRepository<>(readConnection, UserEntity.class);
        crud = new JdbcCqrsIndexedCrudRepository<>(readRepo, writeRepo);
        crud.save(user1);
        crud.save(user4);
        TimeUnit.SECONDS.sleep(1);
    }

    @AfterEach
    void tearDown() throws SQLException {
        //Statement statement = writeConnection.createStatement();
        //statement.executeUpdate("DROP TABLE " + UserEntity.class.getSimpleName().toLowerCase());
        writeConnection.close();
        readConnection.close();
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
    void addIndex() {
        String key = "fName";
        assertFalse(crud.hasIndex(key));
        crud.addIndex(key);
        assertTrue(crud.hasIndex(key));
    }

    @Test
    void removeIndex() {
        String key = "fName";
        crud.addIndex(key);
        assertTrue(crud.hasIndex(key));
        crud.removeIndex(key);
        assertFalse(crud.hasIndex(key));
    }

    @Test
    void addIndexes() {
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
    void removeIndexes() {
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
    void findByIndex() {
        Optional<List<UserEntity>> optional = crud.findByIndex("lName", "Ivanov");
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
    void count() throws InterruptedException {
        assertEquals(2, crud.count());
        crud.delete(user4);
        TimeUnit.SECONDS.sleep(1);
        assertEquals(1, crud.count());
    }

    @Test
    void existsById() {
        String idTrue = "71c61354-e77d-3645-b696-ed70d6bc0a5b";
        String idFalse = "85db8c9b-349b-37c3-a217-c4904a1e5b77";
        assertTrue(crud.existsById(idTrue));
        assertFalse(crud.existsById(idFalse));
    }

    @Test
    void findAll() {
        List<UserEntity> userList = new ArrayList<>();
        crud.findAll().iterator().forEachRemaining(userList::add);
        assertEquals(2, userList.size());
        assertTrue(user1.getId().equals(userList.get(0).getId()) ||
                user4.getId().equals(userList.get(0).getId()));
        assertTrue(user1.getId().equals(userList.get(1).getId()) ||
                user4.getId().equals(userList.get(1).getId()));
    }

    @Test
    void findAllById() {
        List<String> ids = Arrays.asList("71c61354-e77d-3645-b696-ed70d6bc0a5b",
                "06f25d6a-3ec1-379b-9658-bee7477435d8");
        List<UserEntity> userList = new ArrayList<>();
        crud.findAllById(ids).iterator().forEachRemaining(userList::add);
        assertEquals(2, userList.size());
        assertTrue(user1.getId().equals(userList.get(0).getId()) ||
                user4.getId().equals(userList.get(0).getId()));
        assertTrue(user1.getId().equals(userList.get(1).getId()) ||
                user4.getId().equals(userList.get(1).getId()));
    }

    @Test
    void findById() {
        String id = "71c61354-e77d-3645-b696-ed70d6bc0a5b";
        Optional<UserEntity> optional = crud.findById("71c61354-e77d-3645-b696-ed70d6bc0a5b");
        if (optional.isPresent()) {
            assertEquals(id, optional.get().getId());
        } else {
            fail();
        }
    }

    @Test
    void delete() throws SQLException, InterruptedException {
        assertTrue(isEntityPresentInAllDbs(user1));
        crud.delete(user1);
        TimeUnit.SECONDS.sleep(1);
        assertFalse(isEntityPresentInAllDbs(user1));
    }

    @Test
    void deleteAll() throws SQLException, InterruptedException {
        assertFalse(TestUtils.isEntityTableIsEmpty(UserEntity.class, writeConnection));
        assertFalse(TestUtils.isEntityTableIsEmpty(UserEntity.class, readConnection));
        crud.deleteAll();
        TimeUnit.SECONDS.sleep(1);
        assertTrue(TestUtils.isEntityTableIsEmpty(UserEntity.class, writeConnection));
        assertTrue(TestUtils.isEntityTableIsEmpty(UserEntity.class, readConnection));
    }

    @Test
    void deleteAllParametrized() throws SQLException, InterruptedException {
        assertTrue(isEntityPresentInAllDbs(user1));
        assertTrue(isEntityPresentInAllDbs(user4));
        List<UserEntity> userList = Arrays.asList(user1, user4);
        crud.deleteAll(userList);
        TimeUnit.SECONDS.sleep(1);
        assertFalse(isEntityPresentInAllDbs(user1));
        assertFalse(isEntityPresentInAllDbs(user4));
    }

    @Test
    void deleteById() throws SQLException, InterruptedException {
        assertTrue(isEntityPresentInAllDbs(user4));
        String id = "06f25d6a-3ec1-379b-9658-bee7477435d8";
        crud.deleteById(id);
        TimeUnit.SECONDS.sleep(1);
        assertFalse(isEntityPresentInAllDbs(user4));
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
        assertFalse(isEntityPresentInAllDbs(user2));
        assertFalse(isEntityPresentInAllDbs(user2));
        assertFalse(isEntityPresentInAllDbs(user2));
        List<UserEntity> userList = Arrays.asList(user2, user3, user5);
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