package minecraftcivilizations.com.minecraftCivilizationsCore.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

public class CustomPlayerManager<T extends CustomPlayer> implements Listener {
    private final List<T> customPlayers = new ArrayList<>();
    @Setter
    private Consumer<PlayerJoinEvent> onPlayerJoin = event -> {
        T customPlayer = load(event.getPlayer().getUniqueId(), new TypeToken<T>() {}.getType());
        if (customPlayer == null) return;
        addCustomPlayer((T) new CustomPlayer(event.getPlayer().getUniqueId()));
    };
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
                String json = gson.toJson(player, new TypeToken<T>() {}.getType());
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public T load(String UUID, Type type) {
        try (FileReader reader = new FileReader(MinecraftCivilizationsCore.getInstance().getDataFolder() + "/" + UUID + ".json")) {
            T customPlayer = gson.fromJson(reader, type);
            addCustomPlayer(customPlayer);
            return customPlayer;
        } catch (IOException e) {
            return null;
        }
    }

    public T load(UUID UUID, Type type) {
        return load(UUID.toString(), type);
    }

    public void save(String UUID) {
        save(UUID);
    }

    public void save(UUID UUID) {
        try (FileWriter writer = new FileWriter(MinecraftCivilizationsCore.getInstance().getDataFolder() + "/" + UUID.toString() + ".json")) {
            String json = gson.toJson(MinecraftCivilizationsCore.getInstance().getCustomPlayerManager().getCustomPlayer(UUID), Object.class);
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
        onPlayerJoin.accept(event);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        onPlayerQuit.accept(event);
    }
}
