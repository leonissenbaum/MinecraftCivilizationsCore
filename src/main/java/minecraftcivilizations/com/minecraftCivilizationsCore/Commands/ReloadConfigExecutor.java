package minecraftcivilizations.com.minecraftCivilizationsCore.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import minecraftcivilizations.com.minecraftCivilizationsCore.Config.ConfigFilesManager;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;

@CommandAlias("reloadconfig")
public class ReloadConfigExecutor extends BaseCommand {

    @Default
    public void onReloadConfig() {
        ConfigFilesManager.reloadConfigFiles();
    }
}
