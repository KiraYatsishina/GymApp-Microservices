package micro.gymapp.unit.healthTest;

import micro.gymapp.health.DatabaseConnectionHealthIndicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.health.Health;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DatabaseConnectionHealthIndicatorTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @InjectMocks
    private DatabaseConnectionHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHealthUpWhenDatabaseIsAvailable() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(1)).thenReturn(true);

        Health health = healthIndicator.health();

        assertEquals(Health.up().withDetail("Database Connection", "Available").build(), health);
        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).isValid(1);
    }

    @Test
    void testHealthDownWhenDatabaseIsUnavailable() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));

        Health health = healthIndicator.health();

        assertEquals(Health.down().withDetail("Database Connection", "Unavailable").build(), health);
        verify(dataSource, times(1)).getConnection();
    }

    @Test
    void testHealthDownWhenConnectionIsNotValid() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(1)).thenReturn(false);

        Health health = healthIndicator.health();

        assertEquals(Health.down().withDetail("Database Connection", "Unavailable").build(), health);
        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).isValid(1);
    }
}