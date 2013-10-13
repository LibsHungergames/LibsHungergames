package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Listeners.LibsFeastManager;

import me.libraryaddict.Hungergames.Types.HungergamesApi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Feast implements CommandExecutor {
    private TranslationConfig cm = HungergamesApi.getConfigManager().getTranslationsConfig();
    public String description = "Point your compass towards the feast";

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (LibsFeastManager.getFeastManager().getFeastLocation().getY() > 0) {
            ((Player) sender).setCompassTarget(LibsFeastManager.getFeastManager().getFeastLocation());
            sender.sendMessage(cm.getCommandFeastHappened());
        } else {
            sender.sendMessage(cm.getCommandFeastNotHappened());
        }
        return true;
    }
}
