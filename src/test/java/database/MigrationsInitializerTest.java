package database;

import org.jetbrains.annotations.NotNull;

public class MigrationsInitializerTest {

    private static final @NotNull JDBCCredentials CREDENTIALS = JDBCCredentialsTest.DEFAULT_CREDENTIALS;

    public static void initialize() {
        MigrationsInitializer.initialize(CREDENTIALS);
    }

}
