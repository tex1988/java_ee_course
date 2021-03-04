package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.common.repository.exception.UncheckedRepositoryException;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.exceptions.RepositoryUtilsSqlException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class JdbcUtils<T extends BaseEntity<ID>, ID> {
    private final List<Field> fields;
    private final String tableName;
    private final static Map<String, String> javaToSqlTypeMap = new HashMap<>();
    private final ReflectionUtils<T> reflectionUtils;

    static {
        javaToSqlTypeMap.put("String", "VARCHAR (40)");
        javaToSqlTypeMap.put("ID", "VARCHAR (40)");
        javaToSqlTypeMap.put("int", "INTEGER");
        javaToSqlTypeMap.put("double", "DOUBLE PRECISION");
        javaToSqlTypeMap.put("long", "BIGINT");
        javaToSqlTypeMap.put("float", "REAL");
        javaToSqlTypeMap.put("boolean", "BIT");
    }

    public JdbcUtils(Class<T> clazz) {
        this.tableName = clazz.getSimpleName().toLowerCase();
        this.reflectionUtils = new ReflectionUtils<>(clazz);
        this.fields = ReflectionUtils.addClassFields(clazz);
    }

    public String getSqlType(Field field) {
        String result;
        String fieldType = field.getType().getSimpleName();
        result = javaToSqlTypeMap.get(fieldType);
        if (result == null) {
            UncheckedRepositoryException exception = new UncheckedRepositoryException(
                    new IllegalArgumentException("Unsupported field type"));
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
        return result;
    }

    public Optional<T> getEntityFromDbById(Connection connection, String id) {
        T entity = reflectionUtils.createEntity();
        try (Statement statement = connection.createStatement()) {
            String query = String.format("SELECT* FROM %s WHERE id = '%s'", tableName, id);
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            for (Field field : fields) {
                reflectionUtils.setFieldValue(field, entity, resultSet.getString(field.getName()));
            }
        } catch (SQLException e) {
            LOGGER.debug("Exception: ", e);
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    public List<T> getEntitiesFromDbByQuery(Connection connection, String query) {
        List<T> result = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                T entity = reflectionUtils.createEntity();
                for (Field field : fields) {
                    String fieldValue = resultSet.getString(field.getName());
                    reflectionUtils.setFieldValue(field, entity, fieldValue);
                }
                result.add(entity);
            }
        } catch (SQLException e) {
            LOGGER.debug("Exception: ", e);
            return result;
        }
        return result;
    }

    public String entityToQuery(T entity) {
        String result;
        StringBuilder values = new StringBuilder();
        StringBuilder columnNames = new StringBuilder();
        for (Field field : fields) {
            try {
                columnNames
                        .append(field.getName())
                        .append(", ");
                values
                        .append("'")
                        .append(field.get(entity))
                        .append("', ");
            } catch (IllegalAccessException e) {
                LOGGER.error("Exception: ", e);
                throw new UncheckedRepositoryException(e);
            }
        }
        values = new StringBuilder(values.substring(0, values.lastIndexOf(",")));
        columnNames = new StringBuilder(columnNames.substring(0, columnNames.lastIndexOf(",")));
        result = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnNames, values);
        return result;
    }

    public String getCreateTableQuery() {
        String result = String.format("CREATE TABLE IF NOT EXISTS %s", tableName);
        StringBuilder columns = new StringBuilder("(id VARCHAR(40) PRIMARY KEY");
        for (Field field : fields) {
            if (field.getName().equals("id")) {
                continue;
            }
            columns
                    .append(", ")
                    .append(field.getName())
                    .append(" ")
                    .append(getSqlType(field));
        }
        result = String.format("%s %s)", result, columns);
        return result;
    }

    public List<T> getEntitiesFromResultSet(ResultSet resultSet) {
        List<T> entitiesList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                T entity = reflectionUtils.createEntity();
                for (Field field : fields) {
                    String fieldValue = resultSet.getString(field.getName());
                    reflectionUtils.setFieldValue(field, entity, fieldValue);
                }
                entitiesList.add(entity);
            }
        } catch (SQLException e) {
            RepositoryUtilsSqlException exception = new RepositoryUtilsSqlException(e);
            LOGGER.error("Exception: ", exception);
            throw exception;
        }
        return entitiesList;
    }
}