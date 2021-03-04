package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.converter;


import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.config.ConverterConfig;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.data_converter.entity.User;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvBinConverterTest {
    private static final File testCsvFile = new File("src/test/resources/71c61354-e77d-3645-b696-ed70d6bc0a5b.csv");
    private static final File testBinFile = new File("src/test/resources/71c61354-e77d-3645-b696-ed70d6bc0a5b.bin");
    private static File convertedToCsvFile;
    private static File convertedToBinFile;
    private static final String TEMP_DIR_PREFIX = "oleksii_zinkevych_repo_test_";
    private String TEST_ROOT;
    private static final ApplicationContext xmlApplicationContext = new ClassPathXmlApplicationContext("ApplicationContext.xml");
    private static final ApplicationContext annotationApplicationContext = new AnnotationConfigApplicationContext(ConverterConfig.class);
    private static final CsvBinConverter<User, String> xmlContextConverter = xmlApplicationContext.getBean(CsvBinConverter.class);
    private static final CsvBinConverter<User, String> annContextConverter = annotationApplicationContext.getBean(CsvBinConverter.class);

    @BeforeEach
    void setUp() {
        try {
            TEST_ROOT = Files
                    .createTempDirectory(TEMP_DIR_PREFIX)
                    .toAbsolutePath()
                    .toString();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create test directory");
        }
        convertedToCsvFile = Path.of(TEST_ROOT, "71c61354-e77d-3645-b696-ed70d6bc0a5b.csv").toFile();
        convertedToBinFile = Path.of(TEST_ROOT, "71c61354-e77d-3645-b696-ed70d6bc0a5b.bin").toFile();
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.deleteDirectory(Path.of(TEST_ROOT).toFile());
    }

    @ParameterizedTest
    @MethodSource("converterProvider")
    void convertBinToCsvTest(CsvBinConverter<User, String> converter) {
        converter.convertBinToCsv(testBinFile.getPath(), convertedToCsvFile.getPath());
        assertTrue(convertedToCsvFile.exists());
    }

    @ParameterizedTest
    @MethodSource("converterProvider")
    void convertCsvToBinTest(CsvBinConverter<User, String> converter) {
        converter.convertCsvToBin(testCsvFile.getPath(), convertedToBinFile.getPath());
        assertTrue(convertedToBinFile.exists());
    }

    private static Stream<Arguments> converterProvider() {
        return Stream.of(
                Arguments.of(xmlContextConverter),
                Arguments.of(annContextConverter));
    }
}