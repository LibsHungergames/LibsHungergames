package me.libraryaddict.Hungergames.Commands;


import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Creator implements CommandExecutor {
    public String[] aliases = new String[] { "download" };
    private TranslationConfig cm = HungergamesApi.getConfigManager().getTranslationsConfig();
    public String description = "View the author of this great plugin";

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        sender.sendMessage(String.format(cm.getCommandCreator(), "libraryaddict", "http://ow.ly/kWBpO"));
        return true;
    }
}
