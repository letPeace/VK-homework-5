package database;

import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;

public class MigrationsInitializer {

    private static final @NotNull JDBCCredentials CREDENTIALS = JDBCCredentials.DEFAULT_CREDENTIALS;

    public static void initialize() {
        final Flyway flyway = Flyway.configure()
                .dataSource(
                        CREDENTIALS.url(),
                        CREDENTIALS.login(),
                        CREDENTIALS.password()
                )
                .cleanDisabled(false)
                .locations("migrations")
                .load();
        flyway.clean();
        flyway.migrate();
    }

}
