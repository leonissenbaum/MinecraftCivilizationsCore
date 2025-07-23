package minecraftcivilizations.com.minecraftCivilizationsCore.ProtocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import minecraftcivilizations.com.minecraftCivilizationsCore.Component.ComponentUtils;
import minecraftcivilizations.com.minecraftCivilizationsCore.GUI.GUI;
import minecraftcivilizations.com.minecraftCivilizationsCore.GUI.ListGUI;
import minecraftcivilizations.com.minecraftCivilizationsCore.GUI.SearchSignGUI;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import minecraftcivilizations.com.minecraftCivilizationsCore.Options.GUIPlaceOption;
import minecraftcivilizations.com.minecraftCivilizationsCore.Options.Option;
import minecraftcivilizations.com.minecraftCivilizationsCore.Player.CustomPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PacketManager {

    public static ProtocolManager protocolManager;

    public static void init() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(MinecraftCivilizationsCore.getInstance(), PacketType.Play.Server.OPEN_SIGN_EDITOR) {
            @Override
            public void onPacketSending(PacketEvent packetEvent) {
                MinecraftCivilizationsCore.logger.info(String.valueOf(packetEvent.getPacket().getBooleans().read(0)));
                packetEvent.getPlayer().sendMessage(ComponentUtils.deserializeComponent("Sigma"));
            }
        });
        protocolManager.addPacketListener(new PacketAdapter(MinecraftCivilizationsCore.getInstance(), PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent packetEvent) {
                Player player = packetEvent.getPlayer();


                MinecraftCivilizationsCore.logger.info(packetEvent.getPacket().getStringArrays().read(0)[0]);
                MinecraftCivilizationsCore.logger.info(String.valueOf(SearchSignGUI.searchItemsAndBlocks(packetEvent.getPacket().getStringArrays().read(0)[0])));
                packetEvent.getPlayer().sendMessage(ComponentUtils.deserializeComponent("Sigma2"));
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


