package minecraftcivilizations.com.minecraftCivilizationsCore.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.papermc.paper.persistence.PersistentDataContainerView;
import lombok.Getter;
import lombok.Setter;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CustomPlayerManager implements Listener {
    private static final NamespacedKey DATA_KEY = new NamespacedKey(MinecraftCivilizationsCore.getInstance(), "CustomPlayerData");

    private final ConcurrentHashMap<UUID, CustomPlayer> customPlayers = new ConcurrentHashMap<>();
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
    private Consumer<PlayerQuitEvent> onPlayerQuit = event -> removeCustomPlayer(event.getPlayer().getUniqueId());
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public CustomPlayer getCustomPlayer(UUID uuid) {
        return customPlayers.get(uuid);
    }

    public void saveAll() {
       customPlayers.keys().asIterator().forEachRemaining(this::save);
    }

    public CustomPlayer load(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        PersistentDataContainerView pdc = player.getPersistentDataContainer();

        if (!pdc.has(DATA_KEY)) return null;
        String data = pdc.get(DATA_KEY, PersistentDataType.STRING);

        CustomPlayer customPlayer = gson.fromJson(data, customPlayerClass);
        addCustomPlayer(customPlayer);
        return customPlayer;
    }

    public void save(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) throw new IllegalArgumentException("Attempted to save player with UUID " + uuid + " who is not online");

        String data = gson.toJson(getCustomPlayer(uuid), customPlayerClass);

        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(DATA_KEY, PersistentDataType.STRING, data);
    }

    public void addCustomPlayer(CustomPlayer player) {
        if (getCustomPlayer(player.getUuid()) != null) {
            customPlayers.remove(player.getUuid());
        }
        customPlayers.put(player.getUuid(), player);
    }

    public void removeCustomPlayer(UUID player) {
        if (getCustomPlayer(player) != null) {
            MinecraftCivilizationsCore.getInstance().getCustomPlayerManager().save(player);
            customPlayers.remove(player);
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
