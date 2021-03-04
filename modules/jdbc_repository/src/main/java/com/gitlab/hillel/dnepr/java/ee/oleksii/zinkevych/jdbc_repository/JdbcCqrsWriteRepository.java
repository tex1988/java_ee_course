package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.BaseCqrsWriteRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.common.Observer;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository.exceptions.SqlJdbcRepositoryException;

import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.CommonUtils;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.JdbcUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class JdbcCqrsWriteRepository<T extends BaseEntity<ID>, ID>
        extends BaseCqrsWriteRepository<T, ID> {
    private final Connection connection;
    private final String tableName;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final JdbcUtils<T, ID> jdbcUtils;
    private final CommonUtils<T, ID> commonUtils;

    public JdbcCqrsWriteRepository(Connection connection, Class<T> clazz) {
        Objects.requireNonNull(connection, "Connection is undefined");
        Objects.requireNonNull(clazz, "Class is undefined");
        this.connection = connection;
        this.tableName = clazz.getSimpleName().toLowerCase();
        this.jdbcUtils = new JdbcUtils<>(clazz);
        this.commonUtils = new CommonUtils<>(clazz);
        try (Statement statement = connection.createStatement()) {
            String createTableQuery = jdbcUtils.getCreateTableQuery();
            statement.execute(createTableQuery);
        } catch (SQLException e) {
            LOGGER.error("Exception", e);
            throw new SqlJdbcRepositoryException(e);
        }
    }

    @Override
    public void delete(T entity) {
        commonUtils.validate(entity);
        ID id = entity.getId();
        deleteFromDbById(entity, id);
    }

    @Override
    public void deleteAll() {
        lock.writeLock().lock();
        try (Statement deleteAllStatement = connection.createStatement()) {
            deleteAllStatement.executeUpdate("TRUNCATE TABLE " + tableName);
            onChange(Observer.Action.DELETE_ALL, null);
        } catch (SQLException e) {
            LOGGER.error("Exception: ", e);
            throw new SqlJdbcRepositoryException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        Objects.requireNonNull(entities, "Entities is undefined");
        entities.forEach(this::delete);
    }

    @Override
    public void deleteById(ID id) {
        Objects.requireNonNull(id, "ID is undefined");
        Optional<T> optional = jdbcUtils.getEntityFromDbById(connection, id.toString());
        if (optional.isEmpty()) {
            LOGGER.debug("Failed to delete. Entity with ID: {} is not present in DB", id);
        } else {
            deleteFromDbById(optional.get(), id);
        }
    }

    private void deleteFromDbById(T entity, ID id) {
        lock.writeLock().lock();
        List<T> entities = new ArrayList<>();
        entities.add(entity);
        try (Statement deleteEntityStatement = connection.createStatement()) {
            deleteEntityStatement.executeUpdate("DELETE FROM " + tableName + " WHERE id = '" + id + "'");
            onChange(Observer.Action.DELETE, entities);
        } catch (SQLException e) {
            LOGGER.error("Exception: ", e);
            throw new SqlJdbcRepositoryException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public <S extends T> S save(S entity) {
        commonUtils.validate(entity);
        String saveEntitySql = jdbcUtils.entityToQuery(entity);
        List<T> entities = new ArrayList<>();
        entities.add(entity);
        lock.writeLock().lock();
        try (Statement saveEntityStatement = connection.createStatement()) {
            saveEntityStatement.executeUpdate(saveEntitySql);
            onChange(Observer.Action.CREATE, entities);
        } catch (SQLException e) {
            LOGGER.error("Exception: ", e);
            throw new SqlJdbcRepositoryException(e);
        } finally {
            lock.writeLock().unlock();
        }
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        Objects.requireNonNull(entities, "Entities is undefined");
        entities.forEach(this::save);
        return entities;
    }

    @Override
    public void close() {
        while (lock.isWriteLocked()) {
            Thread.yield();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Exception");
            throw new SqlJdbcRepositoryException(e);
        }
    }
}