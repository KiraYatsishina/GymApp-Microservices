package micro.gymapp.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component("databaseConnectionHealth")
public class DatabaseConnectionHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseConnectionHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        boolean dbIsUp = checkDatabaseConnection();
        if (dbIsUp) {
            return Health.up().withDetail("Database Connection", "Available").build();
        }
        return Health.down().withDetail("Database Connection", "Unavailable").build();
    }

    private boolean checkDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(1);
        } catch (SQLException e) {
            return false;
        }
    }
}
