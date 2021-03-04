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
class JavaObjectSerializerTest {
    private static final String TEMP_DIR_PREFIX = "oleksii_zinkevych_repo_test_";
    private String TEST_ROOT;
    private static final File testBin= new File("src/test/resources/71c61354-e77d-3645-b696-ed70d6bc0a5b.bin");
    private byte[] binBytes;
    private static final User user1 = new User("Ivan", "Ivanov", 30); // ID: 71c61354-e77d-3645-b696-ed70d6bc0a5b
    JavaObjectSerializer<User, String> javaObjectSerializer;

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
        javaObjectSerializer = new JavaObjectSerializer<>();
        binBytes = FileUtils.readFileToByteArray(testBin);
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(Path.of(TEST_ROOT).toFile());
    }

    @Test
    void serialize() throws IOException {
        byte[] bytesSer = javaObjectSerializer.serialize(user1);
        String serBinPath = Path.of(TEST_ROOT, user1.getId()+"-ser.bin").toString();
        File serBin = new File(serBinPath);
        FileUtils.writeByteArrayToFile(serBin, bytesSer);
        assertTrue(serBin.exists());
        assertTrue(FileUtils.contentEquals(testBin, serBin));
    }

    @Test
    void deserialize() {
        User deserUser = javaObjectSerializer.deserialize(binBytes);
        assertEquals(user1, deserUser);
    }
}