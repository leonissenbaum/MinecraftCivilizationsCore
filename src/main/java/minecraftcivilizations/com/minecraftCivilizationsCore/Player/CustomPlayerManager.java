package minecraftcivilizations.com.minecraftCivilizationsCore.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class CustomPlayerManager<T extends CustomPlayer> implements Listener {
    private final List<T> customPlayers = new ArrayList<>();
    @Setter
    private Consumer<PlayerJoinEvent> onPlayerJoin = event -> {addCustomPlayer((T) new CustomPlayer(event.getPlayer().getUniqueId()));};
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
                gson.toJson(player, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        removeCustomPlayer(event.getPlayer().getUniqueId());
    }
}
