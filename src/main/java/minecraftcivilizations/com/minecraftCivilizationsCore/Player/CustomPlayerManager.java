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
import java.util.function.Consumer;

public class CustomPlayerManager<T extends CustomPlayer> implements Listener {
    private final List<T> customPlayers = new ArrayList<>();
    @Getter
    @Setter
    private Consumer<Player> onPlayerJoin = player -> {
        T customPlayer = load(player.getUniqueId());
        if (customPlayer == null) return;
        addCustomPlayer((T) new CustomPlayer(player.getUniqueId()));
    };

    @Getter
    @Setter
    @Deprecated(since = "forever, needs implementation", forRemoval = false)
    private Consumer<AsyncPlayerPreLoginEvent> onPrePlayerJoin = player -> {
        T customPlayer = load(player.getUniqueId());
        if (customPlayer == null) return;
        addCustomPlayer((T) new CustomPlayer(player.getUniqueId()));
    };

    @Setter
    private Class<? extends T> customPlayerClass;

    @Setter
    private Consumer<PlayerQuitEvent> onPlayerQuit = event -> {removeCustomPlayer(event.getPlayer().getUniqueId());};
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public T getCustomPlayer(UUID uuid) {
        for (T player : customPlayers) {
            if (player.getUuid().equals(uuid)) {
                return player;
            }
        }
        return null;
    }

    public void saveAll() {
        for (T player : customPlayers) {
            try (FileWriter writer = new FileWriter(MinecraftCivilizationsCore.getInstance().getDataFolder() + "/" + player.getUuid().toString() + ".json")) {
                String json = gson.toJson(player, customPlayerClass);
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public T load(String UUID) {
        try (FileReader reader = new FileReader(MinecraftCivilizationsCore.getInstance().getDataFolder() + "/" + UUID + ".json")) {
            T customPlayer = gson.fromJson(reader, customPlayerClass);
            addCustomPlayer(customPlayer);
            return customPlayer;
        } catch (IOException e) {
            return null;
        }
    }

    public T load(UUID UUID) {
        return load(UUID.toString());
    }

    public void save(String UUID) {
        save(UUID);
    }

    public void save(UUID UUID) {
        try (FileWriter writer = new FileWriter(MinecraftCivilizationsCore.getInstance().getDataFolder() + "/" + UUID.toString() + ".json")) {
            String json = gson.toJson(MinecraftCivilizationsCore.getInstance().getCustomPlayerManager().getCustomPlayer(UUID), customPlayerClass);
            writer.write(json);
        } catch (IOException e) {
            return;
        }
    }

    public void addCustomPlayer(T player) {
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
}
