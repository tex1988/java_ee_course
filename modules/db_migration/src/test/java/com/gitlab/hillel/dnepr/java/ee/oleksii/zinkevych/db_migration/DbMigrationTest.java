package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.db_migration;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class DbMigrationTest {
    private static Connection connection;
    private static User user1;
    private static User user2;
    private static User user3;
    private static User user4;
    private static User user5;

    static {
        try {
            user1 = new User("Ivan", "Ivanov", 20, "1990-01-01");
            user2 = new User("Petr", "Petrov", 40, "1990-01-01");
            user3 = new User("Ivan", "Alexandrov", 50, "1990-01-01");
            user4 = new User("Petr", "Ivanov", 20, "1990-01-01");
            user5 = new User("Alexandr", "Alexandrov", 60, "1990-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() throws ClassNotFoundException, SQLException, LiquibaseException {
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection("jdbc:h2:mem:test");
        //Class.forName("org.postgresql.Driver");
        //connection = DriverManager.getConnection("jdbc:postgresql://localhost/test?user=postgres&password=&ssl=false");
        runLiquibase(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void checkUsers() throws SQLException, ParseException {
        List<User> users = getAllFromDb(connection);
        assertTrue(users.containsAll(Arrays.asList(user1, user2, user3, user4, user5)));
    }

    public void runLiquibase(Connection connection) throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new liquibase.Liquibase("/liquibase/data/master.xml", new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());
    }

    private List<User> getAllFromDb(Connection connection) throws SQLException, ParseException {
        PreparedStatement statement = connection.prepareStatement("SELECT* from users");
        ResultSet resultSet = statement.executeQuery();
        return getUsersFromResultSet(resultSet);
    }

    List<User> getUsersFromResultSet(ResultSet resultSet) throws SQLException, ParseException {
        List<User> result = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String fName = resultSet.getString("fname");
            String lName = resultSet.getString("lName");
            String date = resultSet.getString("birthdate");
            int age = resultSet.getInt("age");
            User user = new User(fName, lName, age, date);
            result.add(user);
        }
        return result;
    }
}