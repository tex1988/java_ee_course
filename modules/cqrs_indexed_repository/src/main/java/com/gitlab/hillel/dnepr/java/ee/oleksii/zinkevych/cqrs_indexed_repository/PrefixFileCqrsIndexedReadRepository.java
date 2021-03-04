package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.cqrs_indexed_repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.SyncFuture;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.common.repository.exception.UncheckedRepositoryException;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.indexer.common.Indexer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.common.Serializer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.CommonUtils;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.PathAndFileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

@Slf4j
public class PrefixFileCqrsIndexedReadRepository<T extends BaseEntity<ID>, ID>
        implements CqrsIndexedReadRepository<T, ID> {
    private final Map<Action, Function<Iterable<T>, Future<?>>> actionMap = new HashMap<>();
    private final String entitiesRootPath;
    private final List<Field> fields = new ArrayList<>();
    private final int DEFAULT_PREFIX_DEEP = 3;
    private final int prefixDeep;
    private final int DEFAULT_PREFIX_LENGTH = 2;
    private final int prefixLength;
    private final String entityFileExt;
    private int count = 0;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Serializer<T, ID> serializer;
    private final Indexer<T, ID> indexer;
    private final PathAndFileUtils<ID> fileUtils;
    private final CommonUtils<T, ID> commonUtils;
    private final FileRepositoryUtils<T, ID> fileRepositoryUtils;;

    public PrefixFileCqrsIndexedReadRepository(String repoRootPath,
                                               Class<T> clazz,
                                               Serializer<T, ID> serializer,
                                               Indexer<T, ID> indexer,
                                               String entityFileExt) {
        Objects.requireNonNull(clazz, "class is undefined");
        Objects.requireNonNull(repoRootPath, "repoRootPath is undefined");
        this.serializer = serializer;
        this.indexer = indexer;
        this.fileUtils = new PathAndFileUtils<>();
        this.commonUtils = new CommonUtils<>(clazz);
        this.fileRepositoryUtils = new FileRepositoryUtils<>();
        this.entityFileExt = entityFileExt;
        this.entitiesRootPath = Path
                .of(repoRootPath, "entities", clazz.getSimpleName())
                .toAbsolutePath()
                .toString();
        createRepoRootDir(repoRootPath);
        createEntitiesDir();
        this.prefixDeep = DEFAULT_PREFIX_DEEP;
        this.prefixLength = DEFAULT_PREFIX_LENGTH;
        addActions();
    }

    private void createRepoRootDir(String repoRootPath) {
        try {
            if (!Files.exists(Path.of(repoRootPath))) {
                Files.createDirectory(Path.of(repoRootPath));
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create root directory", e);
            throw new UncheckedRepositoryException("Failed to create repository directory");
        }
    }

    private void createEntitiesDir() {
        try {
            if (!Files.exists(Path.of(this.entitiesRootPath))) {
                Files.createDirectories(Path.of(this.entitiesRootPath));
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create entities directory", e);
            throw new UncheckedRepositoryException("Failed to create entities directory");
        }
    }

    private void addActions() {
        this.actionMap.put(Action.CREATE, this::saveEntity);
        this.actionMap.put(Action.CREATE_OR_UPDATE, this::saveEntity);
        this.actionMap.put(Action.UPDATE, this::saveEntity);
        this.actionMap.put(Action.DELETE, this::deleteEntity);
        this.actionMap.put(Action.DELETE_ALL, this::deleteAll);
    }

    @Override
    public boolean hasIndex(String key) {
        return indexer.hasIndex(key);
    }

    @Override
    public void addIndex(String key) {
        Objects.requireNonNull(key, "Key is undefined");
        if (!indexer.hasIndex(key)) {
            lock.writeLock().lock();
            try {
                Iterable<T> entities = findAll();
                indexer.addIndex(key, entities);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            LOGGER.info("Index {} is already present", key);
        }
    }

    @Override
    public void removeIndex(String key) {
        Objects.requireNonNull(key, "Key is undefined");
        if (indexer.hasIndex(key)) {
            lock.writeLock().lock();
            try {
                indexer.removeIndex(key);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            LOGGER.info("Index {} is not present", key);
        }
    }

    @Override
    public void addIndexes(Set<String> keySet) {
        Objects.requireNonNull(keySet, "Keys is undefined");
        boolean hasIndex = isAnyKeyInKeySetPresentInIndex(keySet);
        if (hasIndex) {
            Iterable<T> entities = findAll();
            lock.writeLock().lock();
            try {
                indexer.addIndexes(keySet, entities);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void removeIndexes(Set<String> keySet) {
        Objects.requireNonNull(keySet, "Keys is undefined");
        indexer.removeIndexes(keySet);
    }

    @Override
    public Optional<List<T>> findByIndex(String key, Object value) {
        Objects.requireNonNull(key, "Key is undefined");
        Objects.requireNonNull(value, "Value is undefined");
        List<T> entitiesList = new ArrayList<>();
        List<String> entitiesIdsList = indexer.getEntityIdsByIndex(key, String.valueOf(value));
        for (String stringId : entitiesIdsList) {
            ID id = (ID) stringId;
            String entityPath = getEntityPath(id);
            T entity = fileRepositoryUtils.deserialize(entityPath, serializer);
            entitiesList.add(entity);
        }
        if (entitiesList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(entitiesList);
        }
    }

    @Override
    public long count() {
        return this.count;
    }

    @Override
    public boolean existsById(ID id) {
        Objects.requireNonNull(id, "ID is undefined");
        String entityPath = getEntityPath(id);
        return Path.of(entityPath).toFile().exists();
    }

    @Override
    public Iterable<T> findAll() {
        List<T> result = new ArrayList<>();
        fileUtils.applyToAllFiles(entitiesRootPath, path -> {
            T entity = fileRepositoryUtils.deserialize(path, serializer);
            LOGGER.debug("Loaded entity " + entity.toString() + " from path: " + path);
            result.add(entity);
        });
        return result;
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        Objects.requireNonNull(ids, "IDs is undefined");
        List<T> result = new ArrayList<>();
        ids.forEach(id -> {
            Optional<T> entityOpt = findById(id);
            entityOpt.ifPresent(result::add);
        });
        return result;
    }

    @Override
    public Optional<T> findById(ID id) {
        Objects.requireNonNull(id, "ID is undefined");
        String entityPath = getEntityPath(id);
        T entity;
        try {
            entity = fileRepositoryUtils.deserialize(entityPath, serializer);
            return Optional.of(entity);
        } catch (UncheckedRepositoryException e) {
            LOGGER.debug("Exception: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Future<?> apply(Action action, Iterable<T> entityList) {
        return actionMap.get(action).apply(entityList);
    }

    private Future<?> saveEntity(Iterable<T> entities) {
        lock.writeLock().lock();
        try {
            entities.forEach(entity -> {
                commonUtils.validate(entity);
                String entityPath = getEntityPath(entity.getId());
                fileRepositoryUtils.serialize(entity, entityPath, serializer);
                indexer.addEntityToIndexes(entity);
                count++;
                LOGGER.debug("{} was saved, path: {}", entity.toString(), entityPath);
            });
        } finally {
            lock.writeLock().unlock();
        }
        return SyncFuture.empty();
    }

    private Future<?> deleteEntity(Iterable<T> entities) {
        lock.writeLock().lock();
        try {
            entities.forEach(entity -> {
                commonUtils.validate(entity);
                String entityPath = getEntityPath(entity.getId());
                File entityFile = new File(entityPath);
                if (entityFile.delete()) {
                    indexer.removeEntityFromIndexes(entity);
                    count--;
                    LOGGER.debug("Entity: {} was successfully deleted, path: {}", entity.toString(), entityPath);
                } else {
                    LOGGER.debug("Failed to delete entity {}, path: {}", entity.toString(), entityPath);
                }
            });
        } finally {
            lock.writeLock().unlock();
        }
        return SyncFuture.empty();
    }

    private Future<?> deleteAll(Iterable<T> entities) {
        lock.writeLock().lock();
        try {
            fileUtils.deleteAllFilesAndFolders(entitiesRootPath);
            indexer.removeAllIndexes();
            this.count = 0;
        } finally {
            lock.writeLock().unlock();
        }
        return SyncFuture.empty();
    }

    @Override
    public void close() {
        while (lock.isWriteLocked()) {
            Thread.yield();
        }
    }

    private boolean isAnyKeyInKeySetPresentInIndex(Set<String> keySet) {
        boolean result = false;
        while (keySet.iterator().hasNext()) {
            if (indexer.hasIndex(keySet.iterator().next())) {
                result = true;
                break;
            }
        }
        return result;
    }

    private String getEntityPath(ID id) {
        String result;
        result = fileUtils.getPrefixPath(id, entitiesRootPath, prefixDeep, prefixLength) + entityFileExt;
        return result;
    }
}