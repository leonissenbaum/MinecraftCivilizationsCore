package minecraftcivilizations.com.minecraftCivilizationsCore;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import minecraftcivilizations.com.minecraftCivilizationsCore.Commands.ReloadConfigExecutor;
import minecraftcivilizations.com.minecraftCivilizationsCore.Config.ConfigFile;
import minecraftcivilizations.com.minecraftCivilizationsCore.Inventory.InventoryListener;
import minecraftcivilizations.com.minecraftCivilizationsCore.GUI.GUIManager;
import minecraftcivilizations.com.minecraftCivilizationsCore.Options.Pair;
import minecraftcivilizations.com.minecraftCivilizationsCore.Player.CustomPlayer;
import minecraftcivilizations.com.minecraftCivilizationsCore.Player.CustomPlayerManager;
import minecraftcivilizations.com.minecraftCivilizationsCore.Player.PlayerClickListener;
import minecraftcivilizations.com.minecraftCivilizationsCore.ProtocolLib.PacketManager;
import minecraftcivilizations.com.minecraftCivilizationsCore.Recipe.RecipeListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@Getter
public final class MinecraftCivilizationsCore extends JavaPlugin {
    public static Logger logger;
    private ConfigFile dbConfigFile;
    private GUIManager guiManager;
    private CustomPlayerManager<CustomPlayer> customPlayerManager;

    // TODO: DataManager???

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();

        setupCommands();

        guiManager = new GUIManager();
        getServer().getPluginManager().registerEvents(guiManager, this);

        customPlayerManager = new CustomPlayerManager<>();
        getServer().getPluginManager().registerEvents(customPlayerManager, this);

        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new RecipeListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerClickListener(), this);

        PacketManager.init();



        dbConfigFile = new ConfigFile(this,
                "db",
                "Database Configuration, if not understood please contact the developer. MCCore uses PostgresQL!", fields -> {
            fields.add(new Pair<>("HOST", "localhost"));
            fields.add(new Pair<>("PORT", 5432));
            fields.add(new Pair<>("DATABASE", "postgres"));
            fields.add(new Pair<>("USERNAME", "postgres"));
            fields.add(new Pair<>("PASSWORD", "pass"));
        }
        );

//        String jdbcUrl = "jdbc:postgresql://" +
//                (dbConfigFile.getString("HOST").equals("off") ? "localhost" : dbConfigFile.getString("HOST")) +
//                ":" +
//                (dbConfigFile.getInteger("PORT") == 0 ? 5432 : dbConfigFile.getInteger("PORT")) +
//                "/" +
//                (dbConfigFile.getString("DATABASE").equals("off") ? "postgres" : dbConfigFile.getString("DATABASE"));
//        String username = dbConfigFile.getString("USERNAME");
//        String password = dbConfigFile.getString("PASSWORD");
//        Connection connection = null;
//
//        try {
//            // Load the PostgreSQL JDBC driver
//            Class.forName("org.postgresql.Driver");
//
//            // Establish the connection
//            connection = DriverManager.getConnection(jdbcUrl, username, password);
//
//            logger.info("Successfully connected to the PostgreSQL database!");
//
//            // Perform database operations here (e.g., execute queries)
//
//        } catch (ClassNotFoundException e) {
//            logger.severe("PostgreSQL JDBC driver not found: " + e.getMessage());
//        } catch (SQLException e) {
//            logger.severe("Connection to database failed: " + e.getMessage());
//        } finally {
//            // Close the connection in a finally block to ensure it's always closed
//            if (connection != null) {
//                try {
//                    connection.close();
//                    logger.info("Database connection closed.");
//                } catch (SQLException e) {
//                    logger.severe("Error closing connection: " + e.getMessage());
//                }
//            }
//        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MinecraftCivilizationsCore getInstance() {
        return getPlugin(MinecraftCivilizationsCore.class);
    }

    private void setupCommands(){
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new ReloadConfigExecutor());
    }
}
