package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class TestUtils {
    public static boolean isEntityPresentInDb(BaseEntity<?> entity, Connection connection) throws SQLException {
        boolean result;
        Class<?> clazz = entity.getClass();
        String tableName = getTableName(entity);
        String idColumnName = getIdColumnName(clazz);
        String repoName = getRepoNameByConnection(connection);
        String query = String.format("SELECT * FROM %s WHERE %s = ?", tableName, idColumnName);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, entity.getId());
            ResultSet resultSet = statement.executeQuery();
            result = isEntityPresentInResultSet(entity, resultSet, idColumnName);
            if (result) {
                LOGGER.info("{} is present in DB of {}", entity.toString(), repoName);
            } else {
                LOGGER.info("{} is NOT present in DB of {}", entity.toString(), repoName);
            }
        }
        return result;
    }

    private static String getTableName(BaseEntity<?> entity) {
        String result;
        Class<?> clazz = entity.getClass();
        if(clazz.isAnnotationPresent(Table.class)) {
            result = clazz.getAnnotation(Table.class).name();
        } else {
            result = clazz.getSimpleName();
        }
        return result;
    }

    private static String getIdColumnName(Class<?> clazz) {
        String result;
        List<Field> fields = ReflectionUtils.addClassFields(clazz);
        for (Field field : fields) {
            field.setAccessible(true);
            Id annotation = field.getAnnotation(Id.class);
            if (annotation != null) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                if (columnAnnotation != null) {
                    result = columnAnnotation.name();
                } else {
                    result = field.getName();
                }
                return result;
            }
        }
        throw new IllegalArgumentException("Fields not contains id field");
    }

    private static boolean isEntityPresentInResultSet(BaseEntity<?> entity, ResultSet resultSet, String idColumnName) throws SQLException {
        boolean result = false;
        while (resultSet.next()) {
            String id = resultSet.getString(idColumnName);
            if (id.equals(String.valueOf(entity.getId()))) {
                result = true;
                break;
            }
        }
        return result;
    }

    private static String getRepoNameByConnection(Connection connection) {
        String result = "";
        if (connection.toString().contains("read")) {
            result = "readRepo";
        } else if (connection.toString().contains("write")) {
            result = "writeRepo";
        }
        return result;
    }

    public static boolean isEntityTableIsEmpty(Class<?> entityClazz, Connection connection) throws SQLException {
        boolean result = false;
        String tableName = entityClazz.getSimpleName();
        String query = String.format("SELECT COUNT(*) FROM (SELECT * FROM %s FETCH FIRST 1 ROWS ONLY) AS t;", tableName);
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        long count = resultSet.getLong(1);
        if (count == 0) {
            result = true;
        }
        return result;
    }

    public static void runLiquibase(Connection connection, String changelogPath) throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase(changelogPath, new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());
    }

    public static void dropAllLiquibase(Connection connection, String changelogPath) throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase(changelogPath, new ClassLoaderResourceAccessor(), database);
        liquibase.dropAll();
    }
}
