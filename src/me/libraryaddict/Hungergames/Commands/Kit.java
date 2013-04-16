package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Types.Extender;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Kit extends Extender implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = Bukkit.getPlayerExact(sender.getName());
        if (args.length > 0) {
            if (hg.currentTime < 0) {
                String kitName = StringUtils.join(args, " ");
                me.libraryaddict.Hungergames.Types.Kit kit = kits.getKitByName(kitName);
                if (kit == null) {
                    p.sendMessage(ChatColor.RED + "This kit does not exist!");
                    p.sendMessage(ChatColor.RED + "Type /kit for all the kits you can use!");
                    return true;
                }
                if (!kits.ownsKit((Player) sender, kit)) {
                    p.sendMessage(ChatColor.RED + "You do not have access to this kit!");
                    return true;
                }
                if (kit == kits.getKitByPlayer(sender.getName())) {
                    sender.sendMessage(ChatColor.RED + "Already using kit " + kit.getName() + "!");
                    return true;
                }
                kits.setKit(p, kit.getName());
                p.sendMessage(ChatColor.RED + "Now using kit " + kit.getName() + ChatColor.RED + "!");
            } else {
                p.sendMessage(ChatColor.RED + "The game has already started!");
            }
        } else {
            kits.showKits(p);
        }
        return true;
    }

}
