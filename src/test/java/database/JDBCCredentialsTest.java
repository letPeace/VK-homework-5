package database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCCredentialsTest {

    public static final @NotNull JDBCCredentials DEFAULT_CREDENTIALS = new JDBCCredentials(
            "jdbc:postgresql",
            "localhost",
            "5432",
            "organizationTest",
            "postgres",
            "admin"
    );

    public static @NotNull Connection getDefaultConnection() throws SQLException {
        return JDBCCredentials.getDefaultConnection(DEFAULT_CREDENTIALS);
    }

}
