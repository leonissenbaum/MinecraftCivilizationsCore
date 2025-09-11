package minecraftcivilizations.com.minecraftCivilizationsCore.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import minecraftcivilizations.com.minecraftCivilizationsCore.Config.ConfigFilesManager;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import org.bukkit.entity.Player;

@CommandAlias("reloadconfig")
@CommandPermission("core.reload")
public class ReloadConfigExecutor extends BaseCommand {

    @Default
    @CommandPermission("core.reload")
    public void onReloadConfig(Player player) {
        if(player.isOp()) {
            ConfigFilesManager.reloadConfigFiles();
        }
    }
}
