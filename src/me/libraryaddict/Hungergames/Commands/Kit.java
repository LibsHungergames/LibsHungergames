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
                if (!kits.ownsKit(sender.getName(), kit)) {
                    p.sendMessage(ChatColor.RED + "You do not own this kit!");
                    return true;
                }
                if (!kits.choose) {
                    sender.sendMessage(ChatColor.RED + "You may not choose a kit this game, Everyone is using "
                            + (kits.random ? "a random kit" : kits.defaultKit));
                    return true;
                }
                me.libraryaddict.Hungergames.Types.Kit kito = kits.getKitByPlayer(p.getName());
                if (kito != null)
                    kito.removePlayer(p.getName());
                kit.addPlayer(p.getName());
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
