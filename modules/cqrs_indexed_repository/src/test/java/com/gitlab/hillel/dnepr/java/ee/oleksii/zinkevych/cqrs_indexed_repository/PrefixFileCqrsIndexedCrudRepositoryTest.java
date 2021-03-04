package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.cqrs_indexed_repository;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.indexer.PrefixFileIndexer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.CsvSerializer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.JavaObjectSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class PrefixFileCqrsIndexedCrudRepositoryTest {
    private static final String TEMP_DIR_PREFIX = "oleksii_zinkevych_repo_test_";
    private String REPO_ROOT;
    private static final User user1 = new User("Ivan", "Ivanov", 30); // ID: 71c61354-e77d-3645-b696-ed70d6bc0a5b
    private static final User user2 = new User("Petr", "Petrov", 40); // ID: d454dba7-997a-38bc-bc48-dc3c642d86b2
    private static final User user3 = new User("Ivan", "Alexandrov", 50); // ID: d0c9962d-59bb-3c6e-a38e-92f6ea1237fa
    private static final User user4 = new User("Petr", "Ivanov", 60); // ID: 06f25d6a-3ec1-379b-9658-bee7477435d8
    private static final User user5 = new User("Alexandr", "Alexandrov", 60); // ID: 85db8c9b-349b-37c3-a217-c4904a1e5b77
    private PrefixFileCqrsIndexedCrudRepository<User, String> crud;

    @BeforeEach
    void setUp() throws InterruptedException {
        try {
            REPO_ROOT = Files
                    .createTempDirectory(TEMP_DIR_PREFIX)
                    .toAbsolutePath()
                    .toString();
            LOGGER.info("Temp directory successfully created. TMP_DIR: {}", REPO_ROOT);
        } catch (IOException e) {
            LOGGER.error("Failed to create temp directory", e);
            throw new IllegalStateException("Failed to create temp directory");
        }
        PrefixFileCqrsWriteRepository<User, String> writeRepo = new PrefixFileCqrsWriteRepository<>(Path
                .of(REPO_ROOT, "writeRepo")
                .toAbsolutePath()
                .toString(),
                User.class, new JavaObjectSerializer<>(), ".bin");

        PrefixFileCqrsIndexedReadRepository<User, String> readRepo = new PrefixFileCqrsIndexedReadRepository<>(
                Path.of(REPO_ROOT, "readRepo").toAbsolutePath().toString(),
                User.class,
                new CsvSerializer<>(User.class),
                new PrefixFileIndexer<>(User.class,
                        Path.of(REPO_ROOT, "readRepo").toAbsolutePath().toString(),
                        ".txt"),
                ".csv");

        crud = new PrefixFileCqrsIndexedCrudRepository<>(readRepo, writeRepo);

        crud.addIndex("lName");
        crud.addIndex("fName");
        crud.addIndex("age");

        crud.save(user1);
        crud.save(user2);
        crud.save(user3);
        crud.save(user4);
        crud.save(user5);
        Thread.sleep(1000);
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(Path.of(REPO_ROOT).toFile());
    }

    @Test
    void addIndex() throws IOException {
        List<String> lines;
        String key = "id";
        File userIdIndexFile = Path.of(REPO_ROOT, "readRepo",
                "index",
                "User",
                "id",
                "0a",
                "4a",
                "d4",
                "0a4ad44a-55d6-399d-b2e9-da2ca516b29d.txt")
                .toFile();
        assertFalse(userIdIndexFile.exists());
        crud.addIndex(key);
        assertTrue(userIdIndexFile.exists());
        lines = FileUtils.readLines(userIdIndexFile, StandardCharsets.UTF_8);
        assertTrue(lines.contains("d454dba7-997a-38bc-bc48-dc3c642d86b2"));
    }

    @Test
    void removeIndex() throws InterruptedException {
        File ageIndexFolder = Path.of(REPO_ROOT, "readRepo",
                "index",
                "User",
                "age")
                .toFile();
        assertTrue(ageIndexFolder.exists());
        crud.removeIndex("age");
        Thread.sleep(1000);
        assertFalse(ageIndexFolder.exists());
    }

    @Test
    void findByIndex() {
        Optional<List<User>> optional = crud.findByIndex("lName", "Ivanov");
        if (optional.isPresent()) {
            List<User> users = optional.get();
            assertTrue(users.contains(user1));
            assertTrue(users.contains(user4));
        } else {
            fail();
        }
    }

    @Test
    void count() throws InterruptedException {
        long count = crud.count();
        assertEquals(5, count);
        crud.delete(user2);
        Thread.sleep(1000);
        count = crud.count();
        assertEquals(4, count);
        crud.deleteAll();
        Thread.sleep(1000);
        count = crud.count();
        assertEquals(0, count);
        List<User> usersList = new ArrayList<>(Arrays.asList(user1, user2, user3));
        crud.saveAll(usersList);
        Thread.sleep(1000);
        count = crud.count();
        assertEquals(3, count);
        usersList.remove(user3);
        crud.deleteAll(usersList);
        Thread.sleep(1000);
        count = crud.count();
        assertEquals(1, count);
    }

    @Test
    void existsById() {
        boolean result1 = crud.existsById("d454dba7-997a-38bc-bc48-dc3c642d86b2");
        assertTrue(result1);
        boolean result2 = crud.existsById("d554dba7-997a-38bc-bc48-dc3c642d86b2e");
        assertFalse(result2);
    }

    @Test
    void findAll() {
        List<User> usersList = new ArrayList<>();
        crud.findAll().iterator().forEachRemaining(usersList::add);
        assertEquals(5, usersList.size());
        usersList.forEach(user -> LOGGER.debug("Entity was found {}", user.toString()));
    }

    @Test
    void findById() {
        User user;
        Optional<User> opt = crud.findById("06f25d6a-3ec1-379b-9658-bee7477435d8");
        if (opt.isPresent()) {
            user = opt.get();
            assertEquals("Petr", user.getfName());
            assertEquals("Ivanov", user.getlName());
            assertEquals(60, user.getAge());
            assertEquals("06f25d6a-3ec1-379b-9658-bee7477435d8", user.getId());
        } else {
            fail();
        }
    }

    @Test
    void delete() throws InterruptedException, IOException {
        List<String> lines;
        File writeRepoEntity = Path
                .of(REPO_ROOT, "writeRepo",
                        "User",
                        "71",
                        "c6",
                        "13",
                        "71c61354-e77d-3645-b696-ed70d6bc0a5b.bin")
                .toFile();
        File readRepoEntity = Path
                .of(REPO_ROOT, "readRepo",
                        "entities",
                        "User",
                        "71",
                        "c6",
                        "13",
                        "71c61354-e77d-3645-b696-ed70d6bc0a5b.csv")
                .toFile();
        File ageIndexFile = Path
                .of(REPO_ROOT, "readRepo",
                        "index",
                        "User",
                        "age",
                        "34",
                        "17",
                        "3c",
                        "34173cb3-8f07-389d-9beb-c2ac9128303f.txt").toFile();
        File lNameIndexFile = Path
                .of(REPO_ROOT, "readRepo",
                        "index",
                        "User",
                        "lName",
                        "3d",
                        "14",
                        "13",
                        "3d14138f-a92c-34e3-b7a0-146fc1939477.txt").toFile();
        assertTrue(writeRepoEntity.exists());
        assertTrue(readRepoEntity.exists());
        assertTrue(ageIndexFile.exists());
        lines = FileUtils.readLines(lNameIndexFile, StandardCharsets.UTF_8);
        assertTrue(lines.contains("71c61354-e77d-3645-b696-ed70d6bc0a5b"));
        crud.delete(user1); //ID: 71c61354-e77d-3645-b696-ed70d6bc0a5b
        Thread.sleep(500);
        assertFalse(writeRepoEntity.exists());
        assertFalse(readRepoEntity.exists());
        assertFalse(ageIndexFile.exists());
        lines = FileUtils.readLines(lNameIndexFile, StandardCharsets.UTF_8);
        assertFalse(lines.contains("71c61354-e77d-3645-b696-ed70d6bc0a5b"));
    }

    @Test
    void deleteAll() throws InterruptedException {
        crud.deleteAll();
        Thread.sleep(500);
        assertFalse(Path
                .of(REPO_ROOT, "writeRepo", "User")
                .toFile().exists());
        assertFalse(Path
                .of(REPO_ROOT, "readRepo", "entities", "User")
                .toFile().exists());
        assertFalse(Path
                .of(REPO_ROOT, "readRepo", "index", "User")
                .toFile().exists());
    }

    @Test
    void deleteAllParametrized() throws InterruptedException, IOException {
        List<User> userList = Arrays.asList(user1, user4);
        List<String> lines;
        File writeRepoEntityUser1 = Path
                .of(REPO_ROOT, "writeRepo",
                        "User",
                        "71",
                        "c6",
                        "13",
                        "71c61354-e77d-3645-b696-ed70d6bc0a5b.bin")
                .toFile();
        File readRepoEntityUser1 = Path
                .of(REPO_ROOT, "readRepo",
                        "entities",
                        "User",
                        "71",
                        "c6",
                        "13",
                        "71c61354-e77d-3645-b696-ed70d6bc0a5b.csv")
                .toFile();
        File ageIndexFileUser1 = Path
                .of(REPO_ROOT, "readRepo",
                        "index",
                        "User",
                        "age",
                        "34",
                        "17",
                        "3c",
                        "34173cb3-8f07-389d-9beb-c2ac9128303f.txt").toFile();
        File writeRepoEntityUser2 = Path
                .of(REPO_ROOT, "writeRepo",
                        "User",
                        "06",
                        "f2",
                        "5d",
                        "06f25d6a-3ec1-379b-9658-bee7477435d8.bin")
                .toFile();
        File readRepoEntityUser2 = Path
                .of(REPO_ROOT, "readRepo",
                        "entities",
                        "User",
                        "06",
                        "f2",
                        "5d",
                        "06f25d6a-3ec1-379b-9658-bee7477435d8.csv")
                .toFile();
        File ageIndexFileUser2 = Path
                .of(REPO_ROOT, "readRepo",
                        "index",
                        "User",
                        "age",
                        "07",
                        "2b",
                        "03",
                        "072b030b-a126-32f4-b237-4f342be9ed44.txt").toFile();
        File lNameIndexFile = Path
                .of(REPO_ROOT, "readRepo",
                        "index",
                        "User",
                        "lName",
                        "3d",
                        "14",
                        "13",
                        "3d14138f-a92c-34e3-b7a0-146fc1939477.txt").toFile();
        assertTrue(writeRepoEntityUser1.exists());
        assertTrue(readRepoEntityUser1.exists());
        assertTrue(writeRepoEntityUser2.exists());
        assertTrue(readRepoEntityUser2.exists());
        lines = FileUtils.readLines(ageIndexFileUser1, StandardCharsets.UTF_8);
        assertTrue(lines.contains("71c61354-e77d-3645-b696-ed70d6bc0a5b"));
        lines = FileUtils.readLines(lNameIndexFile, StandardCharsets.UTF_8);
        assertTrue(lines.contains("71c61354-e77d-3645-b696-ed70d6bc0a5b"));
        lines = FileUtils.readLines(lNameIndexFile, StandardCharsets.UTF_8);
        assertTrue(lines.contains("06f25d6a-3ec1-379b-9658-bee7477435d8"));
        lines = FileUtils.readLines(ageIndexFileUser2, StandardCharsets.UTF_8);
        assertTrue(lines.contains("06f25d6a-3ec1-379b-9658-bee7477435d8"));
        crud.deleteAll(userList);
        Thread.sleep(500);
        assertFalse(writeRepoEntityUser1.exists());
        assertFalse(readRepoEntityUser1.exists());
        assertFalse(writeRepoEntityUser2.exists());
        assertFalse(readRepoEntityUser2.exists());
        assertFalse(ageIndexFileUser1.exists());
        assertFalse(lNameIndexFile.exists());
        lines = FileUtils.readLines(ageIndexFileUser2, StandardCharsets.UTF_8);
        assertFalse(lines.contains("06f25d6a-3ec1-379b-9658-bee7477435d8"));
    }

    @Test
    void deleteById() throws InterruptedException, IOException {
        List<String> lines;
        String id = "85db8c9b-349b-37c3-a217-c4904a1e5b77";
        File writeRepoEntity = Path
                .of(REPO_ROOT, "writeRepo",
                        "User",
                        "85",
                        "db",
                        "8c",
                        "85db8c9b-349b-37c3-a217-c4904a1e5b77.bin")
                .toFile();
        File readRepoEntity = Path
                .of(REPO_ROOT, "readRepo",
                        "entities",
                        "User",
                        "85",
                        "db",
                        "8c",
                        "85db8c9b-349b-37c3-a217-c4904a1e5b77.csv")
                .toFile();
        File ageIndexFile = Path
                .of(REPO_ROOT, "readRepo",
                        "index",
                        "User",
                        "age",
                        "07",
                        "2b",
                        "03",
                        "072b030b-a126-32f4-b237-4f342be9ed44.txt").toFile();
        File fNameIndexFile = Path
                .of(REPO_ROOT, "readRepo",
                        "index",
                        "User",
                        "fName",
                        "d6",
                        "0e",
                        "87",
                        "d60e87b4-09d3-3c66-99e1-85da5b56702c.txt").toFile();
        assertTrue(writeRepoEntity.exists());
        assertTrue(readRepoEntity.exists());
        assertTrue(fNameIndexFile.exists());
        lines = FileUtils.readLines(ageIndexFile, StandardCharsets.UTF_8);
        assertTrue(lines.contains("85db8c9b-349b-37c3-a217-c4904a1e5b77"));
        crud.deleteById(id);
        Thread.sleep(1000);
        assertFalse(writeRepoEntity.exists());
        assertFalse(readRepoEntity.exists());
        assertFalse(fNameIndexFile.exists());
        lines = FileUtils.readLines(ageIndexFile, StandardCharsets.UTF_8);
        assertFalse(lines.contains("85db8c9b-349b-37c3-a217-c4904a1e5b77"));
    }

    @Test
    void save() throws InterruptedException, IOException {
        List<String> lines;
        File writeRepoEntity = Path
                .of(REPO_ROOT, "writeRepo",
                        "User",
                        "e6",
                        "a5",
                        "15",
                        "e6a51505-fd14-36d9-b9c4-0d79380ef6b4.bin")
                .toFile();
        File readRepoEntity = Path
                .of(REPO_ROOT, "readRepo",
                        "entities",
                        "User",
                        "e6",
                        "a5",
                        "15",
                        "e6a51505-fd14-36d9-b9c4-0d79380ef6b4.csv")
                .toFile();
        File ageIndexFile = Path
                .of(REPO_ROOT, "readRepo",
                        "index",
                        "User",
                        "age",
                        "d6",
                        "45",
                        "92",
                        "d645920e-395f-3dad-bbbb-ed0eca3fe2e0.txt").toFile();
        File fNameIndexFile = Path
                .of(REPO_ROOT, "readRepo",
                        "index",
                        "User",
                        "fName",
                        "ec",
                        "57",
                        "c0",
                        "ec57c08a-ec4b-3c72-a528-e878f74685a7.txt").toFile();
        assertFalse(writeRepoEntity.exists());
        assertFalse(readRepoEntity.exists());
        assertFalse(fNameIndexFile.exists());
        lines = FileUtils.readLines(ageIndexFile, StandardCharsets.UTF_8);
        assertFalse(lines.contains("e6a51505-fd14-36d9-b9c4-0d79380ef6b4"));
        crud.save(new User("Sergey", "Petrov", 40)); //ID: e6a51505-fd14-36d9-b9c4-0d79380ef6b4
        Thread.sleep(1000);
        assertTrue(writeRepoEntity.exists());
        assertTrue(readRepoEntity.exists());
        lines = FileUtils.readLines(ageIndexFile, StandardCharsets.UTF_8);
        assertTrue(lines.contains("e6a51505-fd14-36d9-b9c4-0d79380ef6b4"));
    }

    @Test
    void saveAll() throws InterruptedException, IOException {
        List<String> lines;
        List<User> userList = Arrays.asList(
                new User("Aleksey", "Alexandrov", 45), //ID: e4b1c856-dc77-32a1-bbfe-a76a97ada788
                new User("Sergey", "Alexeev", 45));  //ID: 45cbd4d3-ba34-36ed-a16e-df6c8ce68b20

        File writeRepoEntityUser1 = Path
                .of(REPO_ROOT, "writeRepo",
                        "User",
                        "45",
                        "cb",
                        "d4",
                        "45cbd4d3-ba34-36ed-a16e-df6c8ce68b20.bin")
                .toFile();
        File readRepoEntityUser1 = Path
                .of(REPO_ROOT, "readRepo",
                        "entities",
                        "User",
                        "45",
                        "cb",
                        "d4",
                        "45cbd4d3-ba34-36ed-a16e-df6c8ce68b20.csv")
                .toFile();
        File lNameIndexFileUser1 = Path
                .of(REPO_ROOT, "readRepo",
                        "index",
                        "User",
                        "lName",
                        "02",
                        "40",
                        "e1",
                        "0240e1f0-4cf3-310e-9d70-d12aa3cdc617.txt").toFile();
        File writeRepoEntityUser2 = Path
                .of(REPO_ROOT, "writeRepo",
                        "User",
                        "e4",
                        "b1",
                        "c8",
                        "e4b1c856-dc77-32a1-bbfe-a76a97ada788.bin")
                .toFile();
        File readRepoEntityUser2 = Path
                .of(REPO_ROOT, "readRepo",
                        "entities",
                        "User",
                        "e4",
                        "b1",
                        "c8",
                        "e4b1c856-dc77-32a1-bbfe-a76a97ada788.csv")
                .toFile();
        File lNameIndexFileUser2 = Path
                .of(REPO_ROOT, "readRepo",
                        "index",
                        "User",
                        "lName",
                        "19",
                        "b2",
                        "8b",
                        "19b28bb8-6dd9-30ad-bcb7-875d0f7073c1.txt").toFile();
        File ageIndexFile = Path
                .of(REPO_ROOT, "readRepo",
                        "index",
                        "User",
                        "age",
                        "6c",
                        "83",
                        "49",
                        "6c8349cc-7260-3e62-a3b1-396831a8398f.txt").toFile();
        assertFalse(writeRepoEntityUser1.exists());
        assertFalse(readRepoEntityUser1.exists());
        assertFalse(writeRepoEntityUser2.exists());
        assertFalse(readRepoEntityUser2.exists());
        assertFalse(lNameIndexFileUser1.exists());
        assertFalse(ageIndexFile.exists());
        lines = FileUtils.readLines(lNameIndexFileUser2, StandardCharsets.UTF_8);
        assertFalse(lines.contains("e4b1c856-dc77-32a1-bbfe-a76a97ada788"));
        crud.saveAll(userList);
        Thread.sleep(500);
        assertTrue(writeRepoEntityUser1.exists());
        assertTrue(readRepoEntityUser1.exists());
        assertTrue(writeRepoEntityUser2.exists());
        assertTrue(readRepoEntityUser2.exists());
        lines = FileUtils.readLines(lNameIndexFileUser1, StandardCharsets.UTF_8);
        assertTrue(lines.contains("45cbd4d3-ba34-36ed-a16e-df6c8ce68b20"));
        lines = FileUtils.readLines(lNameIndexFileUser2, StandardCharsets.UTF_8);
        assertTrue(lines.contains("e4b1c856-dc77-32a1-bbfe-a76a97ada788"));
        lines = FileUtils.readLines(ageIndexFile, StandardCharsets.UTF_8);
        assertTrue(lines.contains("e4b1c856-dc77-32a1-bbfe-a76a97ada788"));
        assertTrue(lines.contains("45cbd4d3-ba34-36ed-a16e-df6c8ce68b20"));
    }
}