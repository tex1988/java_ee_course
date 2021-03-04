package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.indexer;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.indexer.common.Indexer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.indexer.exceptions.RepositoryIndexerException;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.CommonUtils;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.PathAndFileUtils;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class PrefixFileIndexer<T extends BaseEntity<ID>, ID> implements Indexer<T, ID> {
    private final List<Field> fields;
    private final String indexRootPath;
    private final List<String> indexList = new ArrayList<>();
    private final PathAndFileUtils<ID> fileUtils;
    private final CommonUtils<T, ID> commonUtils;
    private final int DEFAULT_PREFIX_DEEP = 3;
    private final int prefixDeep;
    private final int DEFAULT_PREFIX_LENGTH = 2;
    private final int prefixLength;
    private final String INDEX_FILE_EXT = ".txt";
    private final String indexFileExt;

    public PrefixFileIndexer(Class<T> clazz, String repoRootPath, String indexFileExt) {
        this.fields = ReflectionUtils.addClassFields(clazz);
        this.indexRootPath = Path
                .of(repoRootPath, "index", clazz.getSimpleName())
                .toAbsolutePath()
                .toString();
        this.commonUtils = new CommonUtils<>(clazz);
        this.indexFileExt = indexFileExt;
        this.prefixDeep = DEFAULT_PREFIX_DEEP;
        this.prefixLength = DEFAULT_PREFIX_LENGTH;
        this.fileUtils = new PathAndFileUtils<>();
    }

    @Override
    public boolean hasIndex(String key) {
        Objects.requireNonNull(key, "Key is undefined");
        return indexList.contains(key);
    }

    @Override
    public void addIndex(String key, Iterable<T> entities) {
        Objects.requireNonNull(key, "Key is undefined");
        Objects.requireNonNull(entities, "Entities is undefined");
        if (!indexList.contains(key)) {
            indexList.add(key);
            entities.forEach(entity -> addEntityToIndex(entity, key));
        } else {
            LOGGER.info("Index {} is already present", key);
        }
    }

    @Override
    public void removeIndex(String key) {
        Objects.requireNonNull(key, "Key is undefined");
        if (indexList.contains(key)) {
            indexList.remove(key);
            fileUtils.deleteAllFilesAndFolders(Path.of(indexRootPath, key).toString());
        } else {
            LOGGER.info("Index {} is already not present", key);
        }
    }

    @Override
    public void addIndexes(Set<String> keySet, Iterable<T> entities) {
        Objects.requireNonNull(keySet, "Key set is undefined");
        Objects.requireNonNull(entities, "Entities is undefined");
        keySet.forEach(key -> this.addIndex(key, entities));
    }

    @Override
    public void removeIndexes(Set<String> keySet) {
        Objects.requireNonNull(keySet, "Key set is undefined");
        keySet.forEach(this::removeIndex);
    }

    @Override
    public List<String> getEntityIdsByIndex(String key, String value) {
        Objects.requireNonNull(key, "Key is undefined");
        Objects.requireNonNull(value, "Value is undefined");
        List<String> result;
        ID indexFieldValueId = commonUtils.getFieldValueId(value);
        String indexPath = fileUtils.getPrefixPath(indexFieldValueId,
                Path.of(indexRootPath, key)
                        .toAbsolutePath()
                        .toString(), prefixDeep, prefixLength) +
                INDEX_FILE_EXT;
        try {
            result = FileUtils.readLines(Path.of(indexPath).toFile(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Exception: e", e);
            throw new RepositoryIndexerException(e);
        }
        return result;
    }

    @Override
    public void addEntityToIndexes(T entity) {
        Objects.requireNonNull(entity, "Entity is undefined");
        indexList.forEach(key -> addEntityToIndex(entity, key));
    }

    @Override
    public void removeEntityFromIndexes(T entity) {
        Objects.requireNonNull(entity, "Entity is undefined");
        indexList.forEach(key -> {
            removeEntityFromIndex(entity, key);
        });
    }

    @Override
    public void removeAllIndexes() {
        fileUtils.deleteAllFilesAndFolders(indexRootPath);
    }

    private void addEntityToIndex(T entity, String key) {
        Objects.requireNonNull(entity, "Entity is undefined");
        Objects.requireNonNull(key, "Key is undefined");
        String indexPath = getIndexPath(entity, key);
        File indexFile = fileUtils.createFileAndSubFolders(indexPath);
        try {
            FileUtils.writeLines(indexFile, Collections.singleton(entity.getId()), true);
            LOGGER.debug("Entity with id: {} was added to index {}, path: {}", entity.getId(), key, indexPath);
        } catch (IOException e) {
            LOGGER.error("Exception: ", e);
            throw new RepositoryIndexerException(e);
        }
    }

    private void removeEntityFromIndex(T entity, String key) {
        Objects.requireNonNull(entity, "Entity is undefined");
        Objects.requireNonNull(key, "Key is undefined");
        commonUtils.validate(entity);
        String indexPath = getIndexPath(entity, key);
        File indexFile = Path.of(indexPath).toFile();
        if (indexFile.exists()) {
            removeIdFromIndexFile(entity.getId(), indexFile);
            if (indexFile.length() == 0) {
                if (indexFile.delete()) {
                    LOGGER.debug("Index file: {} is empty and was successfully deleted", indexFile.getAbsolutePath());
                }
            }
        }
    }

    private void removeIdFromIndexFile(ID id, File indexFile) {
        String entityId = String.valueOf(id);
        try {
            List<String> lines = FileUtils.readLines(indexFile, StandardCharsets.UTF_8);
            List<String> updatedLines = lines.stream().filter(line -> !line.equals(entityId)).collect(Collectors.toList());
            FileUtils.writeLines(indexFile, updatedLines, false);
        } catch (IOException e) {
            LOGGER.error("Exception: ", e);
            throw new RepositoryIndexerException(e);
        }
    }

    private String getIndexPath(T entity, String key) {
        String result;
        Field indexedField = getIndexedField(key);
        ID indexFieldValueId;
        try {
            String indexedFieldValue = String.valueOf(indexedField.get(entity));
            indexFieldValueId = commonUtils.getFieldValueId(indexedFieldValue);
        } catch (IllegalAccessException e) {
            LOGGER.error("Exception: ", e);
            throw new RepositoryIndexerException();
        }
        result = fileUtils.getPrefixPath(indexFieldValueId,
                Path.of(indexRootPath, key)
                        .toAbsolutePath()
                        .toString(), prefixDeep, prefixLength) +
                indexFileExt;
        return result;
    }

    private Field getIndexedField(String key) {
        Field result = null;
        for (Field field : this.fields) {
            if (field.getName().equals(key)) {
                result = field;
                break;
            }
        }
        Objects.requireNonNull(result, "Indexed field " + key + " is not found");
        return result;
    }
}