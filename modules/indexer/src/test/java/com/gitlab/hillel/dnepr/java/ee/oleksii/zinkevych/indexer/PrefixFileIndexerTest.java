package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.indexer;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.indexer.common.Indexer;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class PrefixFileIndexerTest {
    private static final String TEMP_DIR_PREFIX = "oleksii_zinkevych_repo_test_";
    private String TEST_ROOT;
    private static final User user1 = new User("Ivan", "Ivanov", 30); // ID: 71c61354-e77d-3645-b696-ed70d6bc0a5b
    private static final User user2 = new User("Petr", "Petrov", 40); // ID: d454dba7-997a-38bc-bc48-dc3c642d86b2
    private static final User user3 = new User("Ivan", "Alexandrov", 50); // ID: d0c9962d-59bb-3c6e-a38e-92f6ea1237fa
    private static final User user4 = new User("Petr", "Ivanov", 60); // ID: 06f25d6a-3ec1-379b-9658-bee7477435d8
    private static final User user5 = new User("Alexandr", "Alexandrov", 60); // ID: 85db8c9b-349b-37c3-a217-c4904a1e5b77
    private Indexer<User, String> indexer;

    @BeforeEach
    void setUp() {
        try {
            TEST_ROOT = Files
                    .createTempDirectory(TEMP_DIR_PREFIX)
                    .toAbsolutePath()
                    .toString();
            LOGGER.info("Temp directory successfully created. TMP_DIR: {}", TEST_ROOT);
        } catch (IOException e) {
            LOGGER.error("Failed to create temp directory", e);
            throw new IllegalStateException("Failed to create temp directory");
        }
        indexer = new PrefixFileIndexer<>(User.class, TEST_ROOT, ".txt");
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(Path.of(TEST_ROOT).toFile());
    }

    @Test
    void hasIndex() {
        assertFalse(indexer.hasIndex("age"));
        indexer.addIndex("age", Collections.singletonList(user1));
        assertTrue(indexer.hasIndex("age"));
    }

    @Test
    void addIndex() throws IOException {
        List<String> lines;
        assertFalse(indexer.hasIndex("age"));
        indexer.addIndex("age", Collections.singletonList(user1));
        assertTrue(indexer.hasIndex("age"));
        File indexFile = Path.of(TEST_ROOT,
                "index",
                "User",
                "age",
                "34",
                "17",
                "3c",
                "34173cb3-8f07-389d-9beb-c2ac9128303f.txt")
                .toFile();
        lines = FileUtils.readLines(indexFile, StandardCharsets.UTF_8);
        assertTrue(lines.contains("71c61354-e77d-3645-b696-ed70d6bc0a5b"));
    }

    @Test
    void removeIndex() {
        indexer.addIndex("age", Collections.singletonList(user1));
        File indexFolder = Path.of(TEST_ROOT,
                "index",
                "User",
                "age").toFile();
        assertTrue(indexFolder.exists());
        assertTrue(indexer.hasIndex("age"));
        indexer.removeIndex("age");
        assertFalse(indexFolder.exists());
        assertFalse(indexer.hasIndex("age"));
    }

    @Test
    void addIndexes() throws IOException {
        Set<String> keySet = new HashSet<>(Arrays.asList("age", "lName"));
        List<String> lines;
        assertFalse(indexer.hasIndex("age"));
        assertFalse(indexer.hasIndex("lName"));
        File ageIndexFile = Path.of(TEST_ROOT,
                "index",
                "User",
                "age",
                "34",
                "17",
                "3c",
                "34173cb3-8f07-389d-9beb-c2ac9128303f.txt").toFile();
        File lNameIndexFile = Path.of(TEST_ROOT,
                "index",
                "User",
                "lName",
                "3d",
                "14",
                "13",
                "3d14138f-a92c-34e3-b7a0-146fc1939477.txt").toFile();
        assertFalse(ageIndexFile.exists());
        assertFalse(lNameIndexFile.exists());
        indexer.addIndexes(keySet, Collections.singletonList(user1));
        lines = FileUtils.readLines(ageIndexFile, StandardCharsets.UTF_8);
        assertTrue(lines.contains("71c61354-e77d-3645-b696-ed70d6bc0a5b"));
        lines = FileUtils.readLines(lNameIndexFile, StandardCharsets.UTF_8);
        assertTrue(lines.contains("71c61354-e77d-3645-b696-ed70d6bc0a5b"));
    }

    @Test
    void removeIndexes() {
        Set<String> keySet = new HashSet<>(Arrays.asList("age", "lName"));
        indexer.addIndexes(keySet, Collections.singletonList(user1));
        File ageIndexFolder = Path.of(TEST_ROOT,
                "index",
                "User",
                "age").toFile();
        File lNameIndexFolder = Path.of(TEST_ROOT,
                "index",
                "User",
                "lName").toFile();
        assertTrue(ageIndexFolder.exists());
        assertTrue(lNameIndexFolder.exists());
        indexer.removeIndexes(keySet);
        assertFalse(ageIndexFolder.exists());
        assertFalse(lNameIndexFolder.exists());
    }

    @Test
    void getEntityIdsByIndex() {
        indexer.addIndex("lName", Arrays.asList(user1, user2, user3, user4, user5));
        List<String> ids = indexer.getEntityIdsByIndex("lName", "Ivanov");
        assertEquals(2, ids.size());
        assertTrue(ids.contains("71c61354-e77d-3645-b696-ed70d6bc0a5b"));
        assertTrue(ids.contains("06f25d6a-3ec1-379b-9658-bee7477435d8"));
    }

    @Test
    void addEntityToIndexes() throws IOException {
        List<String> lines;
        indexer.addIndex("age", Arrays.asList(user2, user3, user4, user5));
        indexer.addIndex("lName", Arrays.asList(user2, user3, user4, user5));
        indexer.addEntityToIndexes(user1);
        File ageIndexFile = Path.of(TEST_ROOT,
                "index",
                "User",
                "age",
                "34",
                "17",
                "3c",
                "34173cb3-8f07-389d-9beb-c2ac9128303f.txt").toFile();
        File lNameIndexFile = Path.of(TEST_ROOT,
                "index",
                "User",
                "lName",
                "3d",
                "14",
                "13",
                "3d14138f-a92c-34e3-b7a0-146fc1939477.txt").toFile();
        lines = FileUtils.readLines(ageIndexFile, StandardCharsets.UTF_8);
        assertTrue(lines.contains("71c61354-e77d-3645-b696-ed70d6bc0a5b"));
        lines = FileUtils.readLines(lNameIndexFile, StandardCharsets.UTF_8);
        assertTrue(lines.contains("71c61354-e77d-3645-b696-ed70d6bc0a5b"));
    }

    @Test
    void removeEntityFromIndexes() throws IOException {
        List<String> lines;
        indexer.addIndex("age", Arrays.asList(user1, user2, user3, user4, user5));
        indexer.addIndex("lName", Arrays.asList(user2, user3, user4, user5));
        indexer.removeEntityFromIndexes(user1);
        File ageIndexFile = Path.of(TEST_ROOT,
                "index",
                "User",
                "age",
                "34",
                "17",
                "3c",
                "34173cb3-8f07-389d-9beb-c2ac9128303f.txt").toFile();
        File lNameIndexFile = Path.of(TEST_ROOT,
                "index",
                "User",
                "lName",
                "3d",
                "14",
                "13",
                "3d14138f-a92c-34e3-b7a0-146fc1939477.txt").toFile();
        assertFalse(ageIndexFile.exists());
        lines = FileUtils.readLines(lNameIndexFile, StandardCharsets.UTF_8);
        assertFalse(lines.contains("71c61354-e77d-3645-b696-ed70d6bc0a5b"));
    }

    @Test
    void removeAllIndexes() {
        Set<String> keySet = new HashSet<>(Arrays.asList("age", "lName"));
        indexer.addIndexes(keySet, Collections.singletonList(user1));
        File ageIndexFolder = Path.of(TEST_ROOT,
                "index",
                "User",
                "age").toFile();
        File lNameIndexFolder = Path.of(TEST_ROOT,
                "index",
                "User",
                "lName").toFile();
        assertTrue(ageIndexFolder.exists());
        assertTrue(lNameIndexFolder.exists());
        indexer.removeAllIndexes();
        assertFalse(ageIndexFolder.exists());
        assertFalse(lNameIndexFolder.exists());
    }
}