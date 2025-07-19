package minecraftcivilizations.com.minecraftCivilizationsCore;

import minecraftcivilizations.com.minecraftCivilizationsCore.API.Config;
import minecraftcivilizations.com.minecraftCivilizationsCore.API.Field;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public final class MinecraftCivilizationsCore extends JavaPlugin {
    public static Logger logger;
    private Config dbConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();

        dbConfig = new Config(this,
                "db",
                "Database Configuration, if not understood please contact the developer. MCCore uses PostgresQL!",
                new Field[]{
                        new Field<>("HOST", String.class),
                        new Field<>("PORT", Integer.class),
                        new Field<>("DATABASE", String.class),
                        new Field<>("USERNAME", String.class),
                        new Field<>("PASSWORD", String.class)
                }
        );

        String jdbcUrl = "jdbc:postgresql://" +
                (dbConfig.getString("HOST").equals("off") ? "localhost" : dbConfig.getString("HOST")) +
                ":" +
                (dbConfig.getInteger("PORT") == 0 ? 5432 : dbConfig.getInteger("PORT")) +
                "/" +
                (dbConfig.getString("DATABASE").equals("off") ? "postgres" : dbConfig.getString("DATABASE"));
        String username = dbConfig.getString("USERNAME");
        String password = dbConfig.getString("PASSWORD");
        Connection connection = null;

        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            logger.info("Successfully connected to the PostgreSQL database!");

            // Perform database operations here (e.g., execute queries)

        } catch (ClassNotFoundException e) {
            logger.severe("PostgreSQL JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            logger.severe("Connection to database failed: " + e.getMessage());
        } finally {
            // Close the connection in a finally block to ensure it's always closed
            if (connection != null) {
                try {
                    connection.close();
                    logger.info("Database connection closed.");
                } catch (SQLException e) {
                    logger.severe("Error closing connection: " + e.getMessage());
                }
            }
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MinecraftCivilizationsCore getInstance() {
        return getPlugin(MinecraftCivilizationsCore.class);
    }

}
