package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.exceptions.RepositoryUtilsException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class PathAndFileUtils<ID> {
    private static final String separator = File.separator;

    private String[] splitToNChar(String text, int size) {
        List<String> parts = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += size) {
            parts.add(text.substring(i, Math.min(length, i + size)));
        }
        return parts.toArray(new String[0]);
    }

    public String getPrefixPath(ID id, String rootPath, int prefixDeep, int prefixLength) {
        StringBuilder result = new StringBuilder(rootPath);
        String stringId = String.valueOf(id);
        String[] foldersArray = splitToNChar(stringId, prefixLength);
        int count = 0;
        for (String prefix : foldersArray) {
            count++;
            result.append(separator).append(prefix);
            if (count == prefixDeep) {
                break;
            }
        }
        result.append(separator).append(id);
        return result.toString();
    }

    public File createFileAndSubFolders(String path) {
        File result = new File(path);
        if (!result.exists()) {
            try {
                if (result.getParentFile().mkdirs()) {
                    LOGGER.debug("Sub folders on path {}, was successfully created", path);
                } else {
                    LOGGER.debug("Sub folders on path {}, was failed to create", path);
                }
                if (result.createNewFile()) {
                    LOGGER.debug("File: {}, was successfully created", path);
                } else {
                    LOGGER.debug("File: {}, was failed to create", path);
                }
            } catch (IOException e) {
                LOGGER.error("Exception: ", e);
                throw new RepositoryUtilsException(e);
            }
        }
        return result;
    }

    public void deleteAllFilesAndFolders(String path) {
        try (Stream<Path> filesWalkStream = Files.walk(Path.of(path))){
            filesWalkStream
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (file.delete()) {
                            LOGGER.debug("File: {} was successfully removed", file.getPath());
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("Exception", e);
            throw new RepositoryUtilsException(e);
        }
    }

    public void applyToAllFiles(String rootPath, Consumer<String> consumer) {
        try (Stream<Path> filesWalkStream = Files.walk(Path.of(rootPath))){
            filesWalkStream
                    .sorted(Comparator.reverseOrder())
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .forEach((consumer));
        } catch (IOException e) {
            LOGGER.error("Exception: ", e);
            throw new RepositoryUtilsException(e);
        }
    }
}
