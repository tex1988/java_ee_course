package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.crud_impl;

import com.gitlab.hillel.dnepr.java.ee.common.repository.CrudRepository;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.config.CrudConfig;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.config.PersistenceConfig;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.City;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.Country;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity.Region;
import com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.utils.TestUtils;
import liquibase.exception.LiquibaseException;
import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class SpringDataCrudImplTest {
    private CrudRepository<Country, Integer> crud;
    private Connection connection;
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

    @BeforeEach
    void setUp() throws SQLException, LiquibaseException {
        connection = DriverManager.getConnection("jdbc:h2:mem:dbTest");
        runLiquibase();

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(CrudConfig.class, PersistenceConfig.class);
        crud = applicationContext.getBean(CrudRepository.class);

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
    }

    @AfterEach
    void tearDown() throws LiquibaseException, SQLException {
        dropAllLiquibase();
        connection.close();
    }

    @Test
    void count() {
        assertEquals(106, crud.count());
        crud.deleteById(3159);
        assertEquals(105, crud.count());
        crud.save(country1);
        assertEquals(106, crud.count());
    }

    @Test
    void existsByIdTest() {
        assertTrue(crud.existsById(173));
        assertTrue(crud.existsById(7716093));
        assertFalse(crud.existsById(353354121));
    }

    @Test
    void findAllTest() {
        List<Country> countries = IterableUtils.toList(crud.findAll());
        assertEquals(106, countries.size());
    }

    @Test
    void findAllByIdTest() {
        Set<Integer> keySet = new HashSet<>(Arrays.asList(173, 7716093));
        List<Country> countries = IterableUtils.toList(crud.findAllById(keySet));
        assertEquals(2, countries.size());
        assertTrue(countries.contains(country2));
        assertTrue(countries.contains(country3));
    }

    @Test
    void findByIdTest() {
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
    void deleteTest() throws SQLException {
        assertTrue(TestUtils.isEntityPresentInDb(country2, connection));
        List<Region> regions = country2.getRegions();
        for (Region region : regions) {
            assertTrue(TestUtils.isEntityPresentInDb(region, connection));
            List<City> cities = region.getCities();
            for (City city : cities) {
                assertTrue(TestUtils.isEntityPresentInDb(city, connection));
            }
        }
        crud.delete(country2);
        assertFalse(TestUtils.isEntityPresentInDb(country2, connection));
        for (Region region : regions) {
            assertFalse(TestUtils.isEntityPresentInDb(region, connection));
            List<City> cities = region.getCities();
            for (City city : cities) {
                assertFalse(TestUtils.isEntityPresentInDb(city, connection));
            }
        }
    }

    @Test
    void deleteAllTest() throws SQLException {
        assertFalse(TestUtils.isEntityTableIsEmpty(Country.class, connection));
        assertFalse(TestUtils.isEntityTableIsEmpty(Region.class, connection));
        assertFalse(TestUtils.isEntityTableIsEmpty(City.class, connection));
        crud.deleteAll();
        assertTrue(TestUtils.isEntityTableIsEmpty(Country.class, connection));
        assertTrue(TestUtils.isEntityTableIsEmpty(Region.class, connection));
        assertTrue(TestUtils.isEntityTableIsEmpty(City.class, connection));

    }

    @Test
    void testDeleteAllTest() throws SQLException {
        List<Country> countries = Arrays.asList(country2, country3);
        assertTrue(TestUtils.isEntityPresentInDb(country2, connection));
        assertTrue(TestUtils.isEntityPresentInDb(country3, connection));
        assertTrue(TestUtils.isEntityPresentInDb(region3, connection));
        assertTrue(TestUtils.isEntityPresentInDb(region4, connection));
        assertTrue(TestUtils.isEntityPresentInDb(city5, connection));
        assertTrue(TestUtils.isEntityPresentInDb(city6, connection));
        crud.deleteAll(countries);
        assertFalse(TestUtils.isEntityPresentInDb(country2, connection));
        assertFalse(TestUtils.isEntityPresentInDb(country3, connection));
        assertFalse(TestUtils.isEntityPresentInDb(region3, connection));
        assertFalse(TestUtils.isEntityPresentInDb(region4, connection));
        assertFalse(TestUtils.isEntityPresentInDb(city5, connection));
        assertFalse(TestUtils.isEntityPresentInDb(city6,connection));
    }

    @Test
    void deleteByIdTest() throws SQLException {
        assertTrue(TestUtils.isEntityPresentInDb(country2, connection));
        crud.deleteById(173);
        assertFalse(TestUtils.isEntityPresentInDb(country2, connection));
    }

    @Test
    void saveTest() throws SQLException {
        assertFalse(TestUtils.isEntityPresentInDb(country1, connection));
        assertFalse(TestUtils.isEntityPresentInDb(region1, connection));
        assertFalse(TestUtils.isEntityPresentInDb(region2, connection));
        assertFalse(TestUtils.isEntityPresentInDb(city1, connection));
        assertFalse(TestUtils.isEntityPresentInDb(city2, connection));
        assertFalse(TestUtils.isEntityPresentInDb(city3, connection));
        assertFalse(TestUtils.isEntityPresentInDb(city4, connection));
        crud.save(country1);
        assertTrue(TestUtils.isEntityPresentInDb(country1, connection));
        assertTrue(TestUtils.isEntityPresentInDb(region1, connection));
        assertTrue(TestUtils.isEntityPresentInDb(region2, connection));
        assertTrue(TestUtils.isEntityPresentInDb(city1, connection));
        assertTrue(TestUtils.isEntityPresentInDb(city2, connection));
        assertTrue(TestUtils.isEntityPresentInDb(city3, connection));
        assertTrue(TestUtils.isEntityPresentInDb(city4, connection));
    }

    @Test
    void saveAllTest() throws SQLException {
        List<Country> countries = Arrays.asList(country1, country4);
        assertFalse(TestUtils.isEntityPresentInDb(country1, connection));
        assertFalse(TestUtils.isEntityPresentInDb(country4, connection));
        assertFalse(TestUtils.isEntityPresentInDb(region1, connection));
        assertFalse(TestUtils.isEntityPresentInDb(region2, connection));
        assertFalse(TestUtils.isEntityPresentInDb(region5, connection));
        assertFalse(TestUtils.isEntityPresentInDb(city1, connection));
        assertFalse(TestUtils.isEntityPresentInDb(city2, connection));
        assertFalse(TestUtils.isEntityPresentInDb(city3, connection));
        assertFalse(TestUtils.isEntityPresentInDb(city4, connection));
        assertFalse(TestUtils.isEntityPresentInDb(city8, connection));
        crud.saveAll(countries);
        assertTrue(TestUtils.isEntityPresentInDb(country1, connection));
        assertTrue(TestUtils.isEntityPresentInDb(country4, connection));
        assertTrue(TestUtils.isEntityPresentInDb(region1, connection));
        assertTrue(TestUtils.isEntityPresentInDb(region2, connection));
        assertTrue(TestUtils.isEntityPresentInDb(region5, connection));
        assertTrue(TestUtils.isEntityPresentInDb(city1, connection));
        assertTrue(TestUtils.isEntityPresentInDb(city2, connection));
        assertTrue(TestUtils.isEntityPresentInDb(city3, connection));
        assertTrue(TestUtils.isEntityPresentInDb(city4, connection));
        assertTrue(TestUtils.isEntityPresentInDb(city8, connection));
    }

    public void runLiquibase() throws LiquibaseException {
        TestUtils.runLiquibase(connection, "/liquibase/data/master.xml");
    }

    public void dropAllLiquibase() throws LiquibaseException {
        TestUtils.dropAllLiquibase(connection, "/liquibase/data/master.xml");
    }
}