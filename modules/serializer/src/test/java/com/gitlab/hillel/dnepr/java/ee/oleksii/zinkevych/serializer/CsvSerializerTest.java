package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class CsvSerializerTest {
    private static final String TEMP_DIR_PREFIX = "oleksii_zinkevych_repo_test_";
    private String TEST_ROOT;
    private static final File testCsv = new File("src/test/resources/71c61354-e77d-3645-b696-ed70d6bc0a5b.csv");
    private byte[] csvBytes;
    private static final User user1 = new User("Ivan", "Ivanov", 30); // ID: 71c61354-e77d-3645-b696-ed70d6bc0a5b
    CsvSerializer<User, String> csvSerializer;

    @BeforeEach
    void setUp() throws IOException {
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
        csvSerializer = new CsvSerializer<>(User.class);
        csvBytes = FileUtils.readFileToByteArray(testCsv);
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(Path.of(TEST_ROOT).toFile());
    }

    @Test
    void serialize() throws IOException {
        byte[] bytesSer = csvSerializer.serialize(user1);
        String serCsvPath = Path.of(TEST_ROOT, user1.getId()+"-ser.csv").toString();
        File serCsv = new File(serCsvPath);
        FileUtils.writeByteArrayToFile(serCsv, bytesSer);
        assertTrue(serCsv.exists());
        assertTrue(FileUtils.contentEquals(testCsv, serCsv));
    }

    @Test
    void deserialize() {
        User deserUser = csvSerializer.deserialize(csvBytes);
        assertEquals(user1, deserUser);
    }
}