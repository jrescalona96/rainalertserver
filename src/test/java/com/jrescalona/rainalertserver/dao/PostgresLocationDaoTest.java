package com.jrescalona.rainalertserver.dao;

import com.jrescalona.rainalertserver.model.Location;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostgresLocationDaoTest {

    PostgresLocationDao locationDao;
    // connect to db
    JdbcTemplate jdbcTemplate = new JdbcTemplate(
            DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .url("jdbc:postgresql://localhost:5432/rain_alert_db_test")
                .username("postgres")
                .password("dbfortesting")
                .build()
    );

    Location l1 = new Location(UUID.randomUUID(), "DAS", 48, 8, -261.253486, -196.207582);
    Location l2 = new Location(UUID.randomUUID(), "UED", 93, 51, 355.769283, -234.434096);
    Location l3 = new Location(UUID.randomUUID(), "CCL", 55, 10, -185.751602, 349.972757);
    Location l4 = new Location(UUID.randomUUID(), "USA", 1, 1, -200.000000, 500.000000);
    List<Location> locations = new ArrayList<>(Arrays.asList(l1,l2,l3));

    @BeforeEach
    void setUp() {
        locationDao = new PostgresLocationDao(jdbcTemplate);

        // Populate location table
        for (Location l : locations) {
            String sql = "INSERT INTO location(id, grid_id, grid_x, grid_y, longitude, latitude) " +
                    "VALUES(" +
                    "'" + l.getId() + "'," +
                    "'" + l.getGridId() + "'," +
                    "'" + l.getGridX() + "'," +
                    "'" + l.getGridY()+ "'," +
                    "'" + l.getLongitude() + "'," +
                    "'" + l.getLatitude() + "'" +
                    ")";
            jdbcTemplate.execute(sql);
        }

    }

    @AfterEach
    void tearDown() {
        // Reset location table
        jdbcTemplate.execute("DELETE FROM location");
    }

    @Test
    void insertLocationShouldInsertLocation() {
        locationDao.insertLocation(l4.getId(), l4);
        Location result = locationDao.selectLocationById(l4.getId()).orElse(null);
        assertNotNull(result);
        assertEquals(l4.getId(), result.getId());
    }

    @Test
    void selectLocationByIdShouldReturnLocation() {
        UUID locationId = l1.getId();
        Location result = locationDao.selectLocationById(locationId).orElse(null);
        assertNotNull(result);
        assertEquals(locationId, result.getId());
    }

    @Test
    void selectAllLocationsShouldHaveThreeLocations() {
        List<Location> results = locationDao.selectAllLocations();
        assertEquals(3, results.size());
    }

    @Test
    void updateLocationByIdShouldUpdateGridIdGridXGridY() {
        UUID locationId = l1.getId();
        Location updateLocation = new Location(null, "LOL", 10, 1, l1.getLongitude(), l1.getLatitude());

        locationDao.updateLocationById(l1.getId(), updateLocation);

        Location result = locationDao.selectLocationById(locationId).orElse(null);
        assertNotNull(result);
        assertNotEquals(l1.getGridId(), result.getGridId());
        assertNotEquals(l1.getGridX(), result.getGridX());
        assertNotEquals(l1.getGridY(), result.getGridY());
    }

    @Test
    void updateLocationByIdShouldUpdateLongitudeLatitude() {
        UUID locationId = l1.getId();
        Location updateLocation = new Location(null, l1.getGridId(), l1.getGridX(), l1.getGridY(), 10.123467, 12.3456789);

        locationDao.updateLocationById(l1.getId(), updateLocation);

        Location result = locationDao.selectLocationById(locationId).orElse(null);
        assertNotNull(result);
        assertNotEquals(l1.getLongitude(), result.getLongitude());
        assertNotEquals(l1.getLatitude(), result.getLatitude());
    }

    @Test
    void deleteLocationById() {
        UUID locationId = l2.getId();
        locationDao.deleteLocationById(locationId);
        Location location = locationDao.selectLocationById(locationId).orElse(null);
        assertNull(location);
    }
}