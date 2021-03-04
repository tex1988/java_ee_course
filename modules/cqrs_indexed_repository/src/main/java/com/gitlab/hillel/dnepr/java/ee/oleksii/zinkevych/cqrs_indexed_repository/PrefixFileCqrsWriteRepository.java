package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.cqrs_indexed_repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.BaseCqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.common.repository.exception.UncheckedRepositoryException;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.serializer.common.Serializer;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.CommonUtils;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.PathAndFileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class PrefixFileCqrsWriteRepository<T extends BaseEntity<ID>, ID>
        extends BaseCqrsWriteRepository<T, ID> {
    private final String repoRootPath;
    private final int DEFAULT_PREFIX_DEEP = 3;
    private final int prefixDeep;
    private final int DEFAULT_PREFIX_LENGTH = 2;
    private final int prefixLength;
    private final String entityFileExt;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final PathAndFileUtils<ID> fileUtils;
    private final CommonUtils<T, ID> commonUtils;
    private final FileRepositoryUtils<T, ID> fileRepositoryUtils;
    private final Serializer<T, ID> serializer;


    public PrefixFileCqrsWriteRepository(String repoRootPath,
                                         Class<T> clazz,
                                         Serializer<T, ID> serializer,
                                         String entityFileExt) {
        Objects.requireNonNull(clazz, "class is undefined");
        Objects.requireNonNull(repoRootPath, "repoRootPath is undefined");
        Objects.requireNonNull(serializer, "Serializer is undefined");
        Objects.requireNonNull(entityFileExt, "Entity file extension is undefined");
        this.entityFileExt = entityFileExt;
        this.serializer = serializer;
        this.fileUtils = new PathAndFileUtils<>();
        this.commonUtils = new CommonUtils<>(clazz);
        this.fileRepositoryUtils = new FileRepositoryUtils<>();
        try {
            Files.createDirectories(Path.of(repoRootPath));
        } catch (IOException e) {
            throw new UncheckedRepositoryException(e);
        }
        try {
            if (!Files.exists(Path.of(repoRootPath, clazz.getSimpleName()))) {
                Files.createDirectory(Path.of(repoRootPath, clazz.getSimpleName()));
            }
            this.repoRootPath = Path
                    .of(repoRootPath, clazz.getSimpleName())
                    .toAbsolutePath()
                    .toString();
        } catch (IOException e) {
            LOGGER.error("Exception: ", e);
            throw new UncheckedRepositoryException("Failed to create class directory");
        }
        this.prefixDeep = DEFAULT_PREFIX_DEEP;
        this.prefixLength = DEFAULT_PREFIX_LENGTH;
    }

    public void delete(T entity) {
        commonUtils.validate(entity);
        ID entityId = entity.getId();
        String path = getEntityPath(entityId);
        File file = new File(path);
        List<T> entities = new ArrayList<>();
        entities.add(entity);
        lock.writeLock().lock();
        try {
            if (file.delete()) {
                LOGGER.debug("File {} was successfully deleted (Entity: {})", path, entity.toString());
                onChange(Observer.Action.DELETE, entities);
            } else {
                LOGGER.debug("File {} failed to delete (Entity: {})", path, entity.toString());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deleteAll() {
        lock.writeLock().lock();
        try {
            fileUtils.deleteAllFilesAndFolders(repoRootPath);
            onChange(Observer.Action.DELETE_ALL, null);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deleteAll(Iterable<? extends T> entities) {
        Objects.requireNonNull(entities, "Entities is undefined");
        lock.writeLock().lock();
        try {
            entities.forEach(entity -> {
                ID entityId = entity.getId();
                String path = getEntityPath(entityId);
                File file = new File(path);
                if (file.delete()) {
                    LOGGER.debug("File {} was successfully deleted (Entity: {})", path, entity.toString());
                } else {
                    LOGGER.debug("File {} failed to delete (Entity: {})", path, entity.toString());
                }
            });
            List<T> entityList = new ArrayList<>();
            entities.forEach(entityList::add);
            onChange(Observer.Action.DELETE, entityList);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deleteById(ID id) {
        Objects.requireNonNull(id, "ID is undefined");
        String path = getEntityPath(id);
        File file = new File(path);
        T entity = getEntityByPath(path);
        List<T> entities = new ArrayList<>();
        entities.add(entity);
        lock.writeLock().lock();
        try {
            if (file.delete()) {
                LOGGER.debug("File {} was successfully removed (ID: {})", path, id.toString());
                onChange(Observer.Action.DELETE, entities);
            } else {
                LOGGER.debug("File {} failed to removed (ID: {})", path, id.toString());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public <S extends T> S save(S entity) {
        commonUtils.validate(entity);
        ID entityId = entity.getId();
        String entityPath = getEntityPath(entityId);
        List<T> entities = new ArrayList<>();
        entities.add(entity);
        lock.writeLock().lock();
        try {
            fileRepositoryUtils.serialize(entity, entityPath, serializer);
            LOGGER.debug("{} was saved, path: {}", entity.toString(), entityPath);
            onChange(Observer.Action.CREATE, entities);
        } finally {
            lock.writeLock().unlock();
        }
        return entity;
    }

    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        lock.writeLock().lock();
        try {
            entities.forEach(entity -> {
                commonUtils.validate(entity);
                ID entityId = entity.getId();
                String entityPath = getEntityPath(entityId);
                fileRepositoryUtils.serialize(entity, entityPath, serializer);
                LOGGER.debug("{} was saved, path: {}", entity.toString(), entityPath);
            });
            List<T> entityList = new ArrayList<>();
            entities.forEach(entityList::add);
            onChange(Observer.Action.CREATE, entityList);
        } finally {
            lock.writeLock().unlock();
        }
        return entities;
    }

    private String getEntityPath(ID id) {
        String result;
        result = fileUtils.getPrefixPath(id, repoRootPath, prefixDeep, prefixLength) + entityFileExt;
        return result;
    }

    private T getEntityByPath(String path) {
        T result;
        if (Path.of(path).toFile().exists()) {
            result = fileRepositoryUtils.deserialize(path, serializer);
        } else {
            UncheckedRepositoryException e = new UncheckedRepositoryException(
                    new NoSuchFileException("File " + path + " is not exist"));
            LOGGER.error("Exception: ", e);
            throw e;
        }
        return result;
    }

    @Override
    public void close() {
        while (lock.isWriteLocked()) {
            Thread.yield();
        }
    }
}