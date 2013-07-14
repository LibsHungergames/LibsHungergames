package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Managers.ConfigManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Managers.TranslationManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Me implements CommandExecutor {
    private ConfigManager config = HungergamesApi.getConfigManager();
    public String description = "Act out a message";
    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private TranslationManager tm = HungergamesApi.getTranslationManager();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (config.isSpectatorChatHidden() && pm.getGamer((Player) sender).isSpectator()) {
            sender.sendMessage(tm.getCommandMeSpectating());
            return true;
        }
        Bukkit.broadcastMessage("* " + ((Player) sender).getDisplayName() + ChatColor.RESET + " " + StringUtils.join(args, " "));
        return true;
    }
}
