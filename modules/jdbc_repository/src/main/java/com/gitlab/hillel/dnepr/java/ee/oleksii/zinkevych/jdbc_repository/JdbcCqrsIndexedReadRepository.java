package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.SyncFuture;
import com.gitlab.hillel.dnepr.java.ee.common.repository.cqrs.CqrsIndexedReadRepository;
import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jdbc_repository.exceptions.SqlJdbcRepositoryException;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.JdbcUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public class JdbcCqrsIndexedReadRepository<T extends BaseEntity<ID>, ID>
        implements CqrsIndexedReadRepository<T, ID> {
    private final Connection connection;
    private final Map<Action, Function<Iterable<T>, Future<?>>> actionMap = new HashMap<>();
    private final String tableName;
    private final List<String> indexKeys = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final JdbcUtils<T, ID> jdbcUtils;

    public JdbcCqrsIndexedReadRepository(Connection connection, Class<T> clazz) {
        Objects.requireNonNull(connection, "Connection is undefined");
        Objects.requireNonNull(clazz, "Class is undefined");
        this.connection = connection;
        this.jdbcUtils = new JdbcUtils<>(clazz);
        this.tableName = clazz.getSimpleName().toLowerCase();
        try {
            Statement statement = connection.createStatement();
            String createTableQuery = jdbcUtils.getCreateTableQuery();
            statement.execute(createTableQuery);
            statement.close();
        } catch (SQLException e) {
            LOGGER.error("Exception", e);
            throw new SqlJdbcRepositoryException(e);
        }
        addActions();
    }

    private void addActions() {
        this.actionMap.put(Action.CREATE, this::createEntity);
        this.actionMap.put(Action.CREATE_OR_UPDATE, this::createEntity);
        this.actionMap.put(Action.UPDATE, this::createEntity);
        this.actionMap.put(Action.DELETE, this::deleteEntity);
        this.actionMap.put(Action.DELETE_ALL, this::deleteAll);
    }

    @Override
    public boolean hasIndex(String key) {
        return indexKeys.contains(key);
    }

    @Override
    public void addIndex(String key) {
        lock.writeLock().lock();
        try (Statement addIndexStatement = connection.createStatement()) {
            String query = String.format("CREATE INDEX IF NOT EXISTS index_%s ON %s (%s)", key, tableName, key);
            addIndexStatement.executeUpdate(query);
            if (!indexKeys.contains(key)) {
                indexKeys.add(key);
            }
        } catch (SQLException e) {
            LOGGER.error("Exception", e);
            throw new SqlJdbcRepositoryException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeIndex(String key) {
        lock.writeLock().lock();
        try (Statement addIndexStatement = connection.createStatement()) {
            String query = String.format("DROP INDEX IF EXISTS index_%s", key);
            addIndexStatement.executeUpdate(query);
            indexKeys.remove(key);
        } catch (SQLException e) {
            LOGGER.error("Exception", e);
            throw new SqlJdbcRepositoryException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addIndexes(Set<String> keySet) {
        Objects.requireNonNull(keySet, "Keys is undefined");
        keySet.forEach(this::addIndex);
    }

    @Override
    public void removeIndexes(Set<String> keySet) {
        Objects.requireNonNull(keySet, "Keys is undefined");
        keySet.forEach(this::removeIndex);
    }

    @Override
    public Optional<List<T>> findByIndex(String key, Object value) {
        Objects.requireNonNull(key, "Key is undefined");
        Objects.requireNonNull(value, "Value is undefined");
        List<T> entitiesList;
        try (Statement finByIndexStatement = connection.createStatement()) {
            String query = String.format("SELECT* FROM %s WHERE %s ='%s'", tableName, key, value);
            ResultSet resultSet = finByIndexStatement.executeQuery(query);
            entitiesList = jdbcUtils.getEntitiesFromResultSet(resultSet);
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
            throw new SqlJdbcRepositoryException(e);
        }
        if (entitiesList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(entitiesList);
        }
    }

    @Override
    public long count() {
        long result;
        try (Statement finByIndexStatement = connection.createStatement()) {
            String query = String.format("SELECT COUNT(*) FROM %s", tableName);
            ResultSet resultSet = finByIndexStatement.executeQuery(query);
            resultSet.next();
            result = resultSet.getLong(1);
        } catch (SQLException e) {
            LOGGER.error("Exception", e);
            throw new SqlJdbcRepositoryException(e);
        }
        return result;
    }

    @Override
    public boolean existsById(ID id) {
        Objects.requireNonNull(id, "ID is undefined");
        boolean result = false;
        Optional<T> optional = jdbcUtils.getEntityFromDbById(connection, id.toString());
        if (optional.isPresent()) {
            if (optional.get().getId().equals(id)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public Iterable<T> findAll() {
        List<T> result;
        String query = "SELECT* FROM " + tableName;
        result = jdbcUtils.getEntitiesFromDbByQuery(connection, query);
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
        return jdbcUtils.getEntityFromDbById(connection, id.toString());
    }

    @Override
    public Future<?> apply(Action action, Iterable<T> entityList) {
        return actionMap.get(action).apply(entityList);
    }

    private Future<?> createEntity(Iterable<T> entities) {
        Objects.requireNonNull(entities, "Entities is undefined");
        lock.writeLock().lock();
        try (Statement createEntity = connection.createStatement()) {
            for (T entity : entities) {
                String saveEntitySql = jdbcUtils.entityToQuery(entity);
                createEntity.executeUpdate(saveEntitySql);
            }
        } catch (SQLException e) {
            LOGGER.error("Exception: ", e);
            throw new SqlJdbcRepositoryException(e);
        } finally {
            lock.writeLock().unlock();
        }
        return SyncFuture.empty();
    }

    private Future<?> deleteEntity(Iterable<T> entities) {
        Objects.requireNonNull(entities, "Entities is undefined");
        lock.writeLock().lock();
        try (Statement deleteEntityStatement = connection.createStatement()) {
            for (T entity : entities) {
                String id = entity.getId().toString();
                String query = String.format("DELETE FROM %s WHERE id = '%s'", tableName, id);
                deleteEntityStatement.executeUpdate(query);
            }
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
            throw new SqlJdbcRepositoryException(e);
        } finally {
            lock.writeLock().unlock();
        }
        return SyncFuture.empty();
    }

    private Future<?> deleteAll(Iterable<T> entities) {
        lock.writeLock().lock();
        try (Statement deleteAllStatement = connection.createStatement()) {
            String query = String.format("TRUNCATE TABLE %s", tableName);
            deleteAllStatement.executeUpdate(query);
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
            throw new SqlJdbcRepositoryException(e);
        } finally {
            lock.writeLock().unlock();
        }
        return SyncFuture.empty();
    }

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