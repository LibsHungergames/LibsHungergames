package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class KitItems implements CommandExecutor {

    private KitManager kits = HungergamesApi.getKitManager();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length > 0)
            kits.sendKitItems(sender, StringUtils.join(args, " "));
        else
            sender.sendMessage(ChatColor.AQUA + "You need to define a kit name!");
        return true;
    }

}
