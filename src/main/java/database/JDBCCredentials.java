package database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public record JDBCCredentials(@NotNull String prefix, @NotNull String host, @NotNull String port, @NotNull String dbName, @NotNull String login, @NotNull String password) {

    public static final @NotNull JDBCCredentials DEFAULT_CREDENTIALS = new JDBCCredentials(
            "jdbc:postgresql",
            "localhost",
            "5432",
            "organization",
            "postgres",
            "admin"
    );

    public static @NotNull Connection getDefaultConnection() throws SQLException {
        return getDefaultConnection(DEFAULT_CREDENTIALS);
    }

    public static @NotNull Connection getDefaultConnection(@NotNull JDBCCredentials jdbcCredentials) throws SQLException {
        return DriverManager.getConnection(
                jdbcCredentials.url(),
                jdbcCredentials.login(),
                jdbcCredentials.password()
        );
    }

    public @NotNull String url() {
        return String.format("%s://%s:%s/%s", prefix, host, port, dbName);
    }

}
