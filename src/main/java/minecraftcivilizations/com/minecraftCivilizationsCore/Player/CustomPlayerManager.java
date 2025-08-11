package minecraftcivilizations.com.minecraftCivilizationsCore.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.framework.qual.Unused;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class CustomPlayerManager implements Listener {
    private final ConcurrentLinkedQueue<CustomPlayer> customPlayers = new ConcurrentLinkedQueue<>();
    @Getter
    @Setter
    private Consumer<Player> onPlayerJoin = player -> {
        CustomPlayer customPlayer = load(player.getUniqueId());
        if (customPlayer == null) {
            addCustomPlayer(new CustomPlayer(player.getUniqueId()));
        }
    };
    @Getter
    @Setter
    private Consumer<AsyncPlayerPreLoginEvent> onPrePlayerJoin = player -> {
        CustomPlayer customPlayer = load(player.getUniqueId());
        if (customPlayer == null) {
            addCustomPlayer(new CustomPlayer(player.getUniqueId()));
        }
    };

    @Setter
    private Class<? extends CustomPlayer> customPlayerClass;

    @Setter
    private Consumer<PlayerQuitEvent> onPlayerQuit = event -> {removeCustomPlayer(event.getPlayer().getUniqueId());};
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public CustomPlayer getCustomPlayer(UUID uuid) {
        for (CustomPlayer player : customPlayers) {
            if (player.getUuid().equals(uuid)) {
                return player;
            }
        }
        return null;
    }

    public void saveAll() {
        for (CustomPlayer player : customPlayers) {
            save(player.getUuid());
        }
    }

    public CustomPlayer load(String UUID) {
        try (FileReader reader = new FileReader(MinecraftCivilizationsCore.getInstance().getDataFolder() + "/" + UUID + ".json")) {
            CustomPlayer customPlayer = gson.fromJson(reader, customPlayerClass);
            addCustomPlayer(customPlayer);
            return customPlayer;
        } catch (IOException e) {
            return null;
        }
    }

    public CustomPlayer load(UUID UUID) {
        return load(UUID.toString());
    }

    public void save(UUID UUID) {
        try (FileWriter writer = new FileWriter(MinecraftCivilizationsCore.getInstance().getDataFolder() + "/" + UUID.toString() + ".json")) {
            String json = gson.toJson(MinecraftCivilizationsCore.getInstance().getCustomPlayerManager().getCustomPlayer(UUID), customPlayerClass);
            writer.write(json);
        } catch (IOException e) {
            return;
        }
    }

    public void addCustomPlayer(CustomPlayer player) {
        if (getCustomPlayer(player.getUuid()) != null) {
            customPlayers.remove(getCustomPlayer(player.getUuid()));
        }
        customPlayers.add(player);
    }

    public void removeCustomPlayer(UUID player) {
        if (getCustomPlayer(player) != null) {
            MinecraftCivilizationsCore.getInstance().getCustomPlayerManager().save(player);
            customPlayers.remove(getCustomPlayer(player));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        onPlayerJoin.accept(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        onPlayerQuit.accept(event);
    }

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        onPrePlayerJoin.accept(event);
    }
}
