package minecraftcivilizations.com.minecraftCivilizationsCore;

import lombok.Getter;
import minecraftcivilizations.com.minecraftCivilizationsCore.Config.Config;
import minecraftcivilizations.com.minecraftCivilizationsCore.Inventory.InventoryListener;
import minecraftcivilizations.com.minecraftCivilizationsCore.Item.CustomItem;
import minecraftcivilizations.com.minecraftCivilizationsCore.Item.CustomItemRegistry;
import minecraftcivilizations.com.minecraftCivilizationsCore.Options.Field;
import minecraftcivilizations.com.minecraftCivilizationsCore.GUI.GUIManager;
import minecraftcivilizations.com.minecraftCivilizationsCore.Player.CustomPlayer;
import minecraftcivilizations.com.minecraftCivilizationsCore.Player.CustomPlayerManager;
import minecraftcivilizations.com.minecraftCivilizationsCore.ProtocolLib.PacketManager;
import minecraftcivilizations.com.minecraftCivilizationsCore.Recipe.RecipeListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Logger;

@Getter
public final class MinecraftCivilizationsCore extends JavaPlugin {
    public static Logger logger;
    private Config dbConfig;
    private GUIManager guiManager;
    private CustomPlayerManager<CustomPlayer> customPlayerManager;

    // TODO: DataManager???

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();




        ItemStack itemStack = CustomItem.newCustomItem(Material.PAPER,

                Component.text("Bandage"),
                new ArrayList<>() {
                    {
                        add(Component.empty());
                        add(Component.text("If used by a healer able to heal players"));
                    }
                },
                this);

        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(this, "bandage"), itemStack);
        shapelessRecipe.addIngredient(new ItemStack(Material.PAPER, 8));
        shapelessRecipe.addIngredient(new ItemStack(Material.SUGAR_CANE));

        Bukkit.addRecipe(shapelessRecipe, true);

        CustomItem customItem = new CustomItem(
                itemStack,
                shapelessRecipe
        );




        CustomItemRegistry.register("bandage", customItem);


        guiManager = new GUIManager();
        getServer().getPluginManager().registerEvents(guiManager, this);

        customPlayerManager = new CustomPlayerManager<>();
        getServer().getPluginManager().registerEvents(customPlayerManager, this);

        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new RecipeListener(), this);

        PacketManager.init();



        dbConfig = new Config(this,
                "db",
                "Database Configuration, if not understood please contact the developer. MCCore uses PostgresQL!", fields -> {
            fields.add(new Field<>("HOST", String.class, "localhost"));
            fields.add(new Field<>("PORT", Integer.class, 5432));
            fields.add(new Field<>("DATABASE", String.class, "postgres"));
            fields.add(new Field<>("USERNAME", String.class, "postgres"));
            fields.add(new Field<>("PASSWORD", String.class, "pass"));
        }
        );

//        String jdbcUrl = "jdbc:postgresql://" +
//                (dbConfig.getString("HOST").equals("off") ? "localhost" : dbConfig.getString("HOST")) +
//                ":" +
//                (dbConfig.getInteger("PORT") == 0 ? 5432 : dbConfig.getInteger("PORT")) +
//                "/" +
//                (dbConfig.getString("DATABASE").equals("off") ? "postgres" : dbConfig.getString("DATABASE"));
//        String username = dbConfig.getString("USERNAME");
//        String password = dbConfig.getString("PASSWORD");
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

}
