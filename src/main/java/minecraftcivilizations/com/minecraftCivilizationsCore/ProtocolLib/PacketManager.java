package minecraftcivilizations.com.minecraftCivilizationsCore.ProtocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import minecraftcivilizations.com.minecraftCivilizationsCore.Component.ComponentUtils;
import minecraftcivilizations.com.minecraftCivilizationsCore.GUI.GUI;
import minecraftcivilizations.com.minecraftCivilizationsCore.GUI.ListGUI;
import minecraftcivilizations.com.minecraftCivilizationsCore.GUI.SearchSignGUI;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import minecraftcivilizations.com.minecraftCivilizationsCore.Options.GUIPlaceOption;
import minecraftcivilizations.com.minecraftCivilizationsCore.Options.Option;
import minecraftcivilizations.com.minecraftCivilizationsCore.Player.CustomPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class PacketManager {

    public static ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    private static Object convertToComponent(String text) {
        try {
            // For newer Paper servers with Adventure components
            if (hasAdventureComponents()) {
                return net.kyori.adventure.text.Component.text(text);
            } else {
                // For older servers or Spigot with BungeeCord chat components
                return Component.text(text);
            }
        } catch (Exception e) {
            // Fallback to plain string if component conversion fails
            MinecraftCivilizationsCore.logger.warning("Failed to convert to component, using plain string: " + e.getMessage());
            return text;
        }
    }

    private static boolean hasAdventureComponents() {
        try {
            Class.forName("net.kyori.adventure.text.Component");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    public static void init() {
        protocolManager.addPacketListener(new PacketAdapter(MinecraftCivilizationsCore.getInstance(), PacketType.Play.Server.OPEN_SIGN_EDITOR) {
            @Override
            public void onPacketSending(PacketEvent packetEvent) {
                MinecraftCivilizationsCore.logger.info(String.valueOf(packetEvent.getPacket().getBooleans().read(0)));
            }
        });
        protocolManager.addPacketListener(new PacketAdapter(MinecraftCivilizationsCore.getInstance(), PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent packetEvent) {
                Player player = packetEvent.getPlayer();
                
                // Only process if this player has an active search sign
                if (!SearchSignGUI.hasActiveSearchSign(player.getUniqueId())) {
                    return; // Ignore regular sign updates
                }
                
                // Clear the active search sign flag since we're processing it
                SearchSignGUI.clearActiveSearchSign(player.getUniqueId());

                BlockPosition blockPosition = new BlockPosition((int) player.getLocation().getX(), (int) player.getLocation().getY() - 2, (int) player.getLocation().getZ());
                player.sendBlockChange(blockPosition.toLocation(player.getWorld()), player.getWorld().getBlockAt(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()).getBlockData());


                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ListGUI openGUI = new ListGUI(Component.text("Search Results"), SearchSignGUI.searchItemsAndBlocks(packetEvent.getPacket().getStringArrays().read(0)[0]));
                        CustomPlayer customPlayer = MinecraftCivilizationsCore.getInstance().getCustomPlayerManager().getCustomPlayer(player.getUniqueId());
                        openGUI.setParentGUI(MinecraftCivilizationsCore.getInstance().getGuiManager().findGUIById(customPlayer.getCurrentGUI()));
                        openGUI.getOptions().put(GUIPlaceOption.SHOULD_PLACE_BACK, true);
                        openGUI.getOptions().put(GUIPlaceOption.SHOULD_PLACE_SEARCH, false);
                        openGUI.open(player);
                    }
                }.runTaskLater(MinecraftCivilizationsCore.getInstance(), 2L);
            }
        });
    }
}
