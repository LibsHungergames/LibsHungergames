package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Managers.ConfigManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;

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
    private TranslationConfig tm = HungergamesApi.getConfigManager().getTranslationsConfig();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (config.getMainConfig().isSpectatorsChatHidden() && pm.getGamer((Player) sender).isSpectator()) {
            sender.sendMessage(tm.getCommandMeSpectating());
            return true;
        }
        if (args.length == 0) {
            return false;
        } else {
            Bukkit.broadcastMessage("* " + ((Player) sender).getDisplayName() + ChatColor.RESET + " " + StringUtils.join(args, " "));
            return true;
        }
    }
}
