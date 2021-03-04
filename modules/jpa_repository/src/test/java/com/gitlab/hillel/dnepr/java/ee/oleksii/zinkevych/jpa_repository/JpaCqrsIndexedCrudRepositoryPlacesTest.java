package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository.entities.City;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository.entities.Country;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_repository.entities.Region;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.TestUtils;
import liquibase.exception.LiquibaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
class JpaCqrsIndexedCrudRepositoryPlacesTest {
    private static final Country country1 = new Country(1005, "Country1");
    private static final Region region1 = new Region(100500, "Region1");
    private static final Region region2 = new Region(100501, "Region2");
    private static final City city1 = new City(1005001, "City1");
    private static final City city2 = new City(1005002, "City2");
    private static final City city3 = new City(1005011, "City3");
    private static final City city4 = new City(1005012, "City4");

    private static final Country country2 = new Country(173, "Ангуилья");
    private static final Region region3 = new Region(174, "Anguilla");
    private static final City city5 = new City(175, "Ангуилья");
    private static final City city6 = new City(176, "Сомбреро");

    private static final Country country3 = new Country(7716093, "Арулько");
    private static final Region region4 = new Region(7716133, "Арулько");
    private static final City city7 = new City(771614, "Балайм");

    private static final Country country4 = new Country(9999999, "Country4");
    private static final Region region5 = new Region(99999991, "Region5");
    private static final City city8 = new City(999999911, "City8");

    private JpaCqrsIndexedCrudRepository<Country, Integer> crud;

    private Connection readConnection;
    private Connection writeConnection;

    @BeforeEach
    void setUp() throws SQLException, LiquibaseException {
        region1.setCountry(country1);
        region2.setCountry(country1);
        city1.setRegion(region1);
        city1.setCountry(country1);
        city2.setRegion(region1);
        city2.setCountry(country1);
        city3.setRegion(region2);
        city3.setCountry(country1);
        city4.setRegion(region2);
        city4.setCountry(country1);
        List<City> region1Cities = Arrays.asList(city1, city2);
        region1.setCities(region1Cities);
        List<City> region2Cities = Arrays.asList(city3, city4);
        region2.setCities(region2Cities);
        List<Region> country1regions = Arrays.asList(region1, region2);
        country1.setRegions(country1regions);

        region3.setCountry(country2);
        city5.setRegion(region3);
        city5.setCountry(country2);
        city6.setRegion(region3);
        city6.setCountry(country2);
        List<City> region3Cities = Arrays.asList(city5, city6);
        region3.setCities(region3Cities);
        List<Region> country2regions = Collections.singletonList(region3);
        country2.setRegions(country2regions);

        region4.setCountry(country3);
        city7.setRegion(region4);
        city7.setCountry(country3);
        List<City> region4Cities = Collections.singletonList(city7);
        region4.setCities(region4Cities);
        List<Region> country3regions = Collections.singletonList(region4);
        country3.setRegions(country3regions);

        region5.setCountry(country4);
        city8.setRegion(region5);
        city8.setCountry(country4);
        List<City> region5Cities = Collections.singletonList(city8);
        region5.setCities(region5Cities);
        List<Region> country5regions = Collections.singletonList(region5);
        country4.setRegions(country5regions);

        readConnection = DriverManager.getConnection("jdbc:h2:mem:readTest");
        writeConnection = DriverManager.getConnection("jdbc:h2:mem:writeTest");
        //readConnection = DriverManager.getConnection("jdbc:postgresql://localhost/test?user=postgres&password=&ssl=false");
        //writeConnection = DriverManager.getConnection("jdbc:postgresql://localhost/test?user=postgres&password=&ssl=false");

        runLiquibase();

        EntityManagerFactory readEntityManagerFactory =
                Persistence.createEntityManagerFactory("persistence-unit-oz-repository-read");
        EntityManagerFactory writeEntityManagerFactory =
                Persistence.createEntityManagerFactory("persistence-unit-oz-repository-write");

        EntityManager readEntityManager = readEntityManagerFactory.createEntityManager();
        EntityManager writeEntityManager = writeEntityManagerFactory.createEntityManager();

        JpaCqrsWriteRepository<Country, Integer> writeRepository =
                new JpaCqrsWriteRepository<>(writeEntityManager, Country.class);
        JpaCqrsIndexedReadRepository<Country, Integer> readRepository =
                new JpaCqrsIndexedReadRepository<>(readEntityManager, Country.class);

        crud = new JpaCqrsIndexedCrudRepository<>(readRepository, writeRepository);
    }

    @AfterEach
    void tearDown() throws SQLException, LiquibaseException {
        dropAllLiquibase();
        readConnection.close();
        writeConnection.close();
    }

    @Test
    void hasIndex() {
        String key = "country_id";
        assertFalse(crud.hasIndex(key));
        crud.addIndex(key);
        assertTrue(crud.hasIndex(key));
        crud.removeIndex(key);
        assertFalse(crud.hasIndex(key));
    }

    @Test
    void addIndex() {
        String key = "country_id";
        assertFalse(crud.hasIndex(key));
        crud.addIndex("country_id");
        assertTrue(crud.hasIndex(key));
    }

    @Test
    void removeIndex() {
        String key = "country_id";
        crud.addIndex("country_id");
        assertTrue(crud.hasIndex(key));
        crud.removeIndex("country_id");
        assertFalse(crud.hasIndex(key));
    }

    @Test
    void addIndexes() {
        Set<String> keySet = new HashSet<>();
        Collections.addAll(keySet, "country_id", "name");
        assertFalse(crud.hasIndex("country_id"));
        assertFalse(crud.hasIndex("name"));
        crud.addIndexes(keySet);
        assertTrue(crud.hasIndex("country_id"));
        assertTrue(crud.hasIndex("name"));
    }

    @Test
    void removeIndexes() {
        Set<String> keySet = new HashSet<>();
        Collections.addAll(keySet, "country_id", "name");
        crud.addIndex("country_id");
        crud.addIndex("name");
        assertTrue(crud.hasIndex("country_id"));
        assertTrue(crud.hasIndex("name"));
        crud.removeIndexes(keySet);
        assertFalse(crud.hasIndex("country_id"));
        assertFalse(crud.hasIndex("name"));
    }

    @Test
    void findByIndex() {
        Optional<List<Country>> optional = crud.findByIndex("country_id", 173);
        if (optional.isPresent()) {
            List<Country> countries = optional.get();
            assertEquals(1, countries.size());
            Country country = countries.get(0);
            assertEquals(country, country2);
        } else {
            fail();
        }
    }

    @Test
    void count() throws InterruptedException {
        assertEquals(106, crud.count());
        crud.deleteById(3159);
        TimeUnit.SECONDS.sleep(1);
        assertEquals(105, crud.count());
        crud.save(country1);
        TimeUnit.SECONDS.sleep(1);
        assertEquals(106, crud.count());
    }

    @Test
    void existsById() {
        assertTrue(crud.existsById(173));
        assertTrue(crud.existsById(7716093));
    }

    @Test
    void findAll() {
        List<Country> countries = IterableUtils.toList(crud.findAll());
        assertEquals(106, countries.size());
    }

    @Test
    void findAllById() {
        Set<Integer> keySet = new HashSet<>(Arrays.asList(173, 7716093));
        List<Country> countries = IterableUtils.toList(crud.findAllById(keySet));
        assertEquals(2, countries.size());
        assertTrue(countries.contains(country2));
        assertTrue(countries.contains(country3));
    }

    @Test
    void findById() {
        int id = 173;
        Optional<Country> optional = crud.findById(id);
        if (optional.isPresent()) {
            Country country = optional.get();
            assertEquals(country, country2);
        } else {
            fail();
        }
    }

    @Test
    void delete() throws SQLException, InterruptedException {
        assertTrue(isEntityPresentInAllDbs(country2));
        List<Region> regions = country2.getRegions();
        for (Region region : regions) {
            assertTrue(isEntityPresentInAllDbs(region));
            List<City> cities = region.getCities();
            for (City city : cities) {
                assertTrue(isEntityPresentInAllDbs(city));
            }
        }
        crud.delete(country2);
        Thread.sleep(1000);
        assertFalse(isEntityPresentInAllDbs(country2));
        for (Region region : regions) {
            assertFalse(isEntityPresentInAllDbs(region));
            List<City> cities = region.getCities();
            for (City city : cities) {
                assertFalse(isEntityPresentInAllDbs(city));
            }
        }
    }

    @Test
    void deleteAll() throws SQLException, InterruptedException {
        assertFalse(TestUtils.isEntityTableIsEmpty(Country.class, writeConnection));
        assertFalse(TestUtils.isEntityTableIsEmpty(Region.class, writeConnection));
        assertFalse(TestUtils.isEntityTableIsEmpty(City.class, writeConnection));
        assertFalse(TestUtils.isEntityTableIsEmpty(Country.class, readConnection));
        assertFalse(TestUtils.isEntityTableIsEmpty(Region.class, readConnection));
        assertFalse(TestUtils.isEntityTableIsEmpty(City.class, readConnection));
        crud.deleteAll();
        Thread.sleep(2000);
        assertTrue(TestUtils.isEntityTableIsEmpty(Country.class, writeConnection));
        assertTrue(TestUtils.isEntityTableIsEmpty(Region.class, writeConnection));
        assertTrue(TestUtils.isEntityTableIsEmpty(City.class, writeConnection));
        assertTrue(TestUtils.isEntityTableIsEmpty(Country.class, readConnection));
        assertTrue(TestUtils.isEntityTableIsEmpty(Region.class, readConnection));
        assertTrue(TestUtils.isEntityTableIsEmpty(City.class, readConnection));
    }

    @Test
    void deleteAllParametrized() throws SQLException, InterruptedException {
        List<Country> countries = Arrays.asList(country2, country3);
        assertTrue(isEntityPresentInAllDbs(country2));
        assertTrue(isEntityPresentInAllDbs(country3));
        assertTrue(isEntityPresentInAllDbs(region3));
        assertTrue(isEntityPresentInAllDbs(region4));
        assertTrue(isEntityPresentInAllDbs(city5));
        assertTrue(isEntityPresentInAllDbs(city6));
        crud.deleteAll(countries);
        Thread.sleep(1000);
        assertFalse(isEntityPresentInAllDbs(country2));
        assertFalse(isEntityPresentInAllDbs(country3));
        assertFalse(isEntityPresentInAllDbs(region3));
        assertFalse(isEntityPresentInAllDbs(region4));
        assertFalse(isEntityPresentInAllDbs(city5));
        assertFalse(isEntityPresentInAllDbs(city6));
    }

    @Test
    void deleteById() throws SQLException, InterruptedException {
        assertTrue(isEntityPresentInAllDbs(country2));
        crud.deleteById(173);
        Thread.sleep(1000);
        assertFalse(isEntityPresentInAllDbs(country2));
    }

    @Test
    void save() throws SQLException, InterruptedException {
        assertFalse(isEntityPresentInAllDbs(country1));
        assertFalse(isEntityPresentInAllDbs(region1));
        assertFalse(isEntityPresentInAllDbs(region2));
        assertFalse(isEntityPresentInAllDbs(city1));
        assertFalse(isEntityPresentInAllDbs(city2));
        assertFalse(isEntityPresentInAllDbs(city3));
        assertFalse(isEntityPresentInAllDbs(city4));
        crud.save(country1);
        Thread.sleep(1000);
        assertTrue(isEntityPresentInAllDbs(country1));
        assertTrue(isEntityPresentInAllDbs(region1));
        assertTrue(isEntityPresentInAllDbs(region2));
        assertTrue(isEntityPresentInAllDbs(city1));
        assertTrue(isEntityPresentInAllDbs(city2));
        assertTrue(isEntityPresentInAllDbs(city3));
        assertTrue(isEntityPresentInAllDbs(city4));
    }

    @Test
    void saveAll() throws SQLException, InterruptedException {
        List<Country> countries = Arrays.asList(country1, country4);
        assertFalse(isEntityPresentInAllDbs(country1));
        assertFalse(isEntityPresentInAllDbs(country4));
        assertFalse(isEntityPresentInAllDbs(region1));
        assertFalse(isEntityPresentInAllDbs(region2));
        assertFalse(isEntityPresentInAllDbs(region5));
        assertFalse(isEntityPresentInAllDbs(city1));
        assertFalse(isEntityPresentInAllDbs(city2));
        assertFalse(isEntityPresentInAllDbs(city3));
        assertFalse(isEntityPresentInAllDbs(city4));
        assertFalse(isEntityPresentInAllDbs(city8));
        crud.saveAll(countries);
        Thread.sleep(1000);
        assertTrue(isEntityPresentInAllDbs(country1));
        assertTrue(isEntityPresentInAllDbs(country4));
        assertTrue(isEntityPresentInAllDbs(region1));
        assertTrue(isEntityPresentInAllDbs(region2));
        assertTrue(isEntityPresentInAllDbs(region5));
        assertTrue(isEntityPresentInAllDbs(city1));
        assertTrue(isEntityPresentInAllDbs(city2));
        assertTrue(isEntityPresentInAllDbs(city3));
        assertTrue(isEntityPresentInAllDbs(city4));
        assertTrue(isEntityPresentInAllDbs(city8));
    }

    private boolean isEntityPresentInAllDbs(BaseEntity<?> entity) throws SQLException {
        boolean result = false;
        boolean writeConnectionResult = TestUtils.isEntityPresentInDb(entity, writeConnection);
        boolean readConnectionResult = TestUtils.isEntityPresentInDb(entity, readConnection);
        if (writeConnectionResult && readConnectionResult) {
            result = true;
        }
        return result;
    }

    public void runLiquibase() throws LiquibaseException {
        TestUtils.runLiquibase(writeConnection, "/liquibase/data/master.xml");
        TestUtils.runLiquibase(writeConnection, "/liquibase/data/user-init.xml");
        TestUtils.runLiquibase(readConnection, "/liquibase/data/master.xml");
        TestUtils.runLiquibase(readConnection, "/liquibase/data/user-init.xml");
    }

    public void dropAllLiquibase() throws LiquibaseException {
        TestUtils.dropAllLiquibase(writeConnection, "/liquibase/data/master.xml");
        TestUtils.dropAllLiquibase(readConnection, "/liquibase/data/master.xml");
    }
}