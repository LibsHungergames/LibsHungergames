package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CreatorCommand implements CommandExecutor {
    public String[] aliases = new String[] { "download" };
    private TranslationConfig cm = HungergamesApi.getConfigManager().getTranslationsConfig();
    public String description = "View the author of this great plugin";

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String msg = cm.getCommandCreator().replace("%s", "libraryaddict");
        if (!msg.toLowerCase().contains("libraryaddict")) {
            msg = ChatColor.GOLD + "All worship king libraryaddict!";
        }
        sender.sendMessage(msg);
        return true;
    }
}
