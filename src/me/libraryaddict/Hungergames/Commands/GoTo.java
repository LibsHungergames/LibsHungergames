package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GoTo extends Extender implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (args.length > 0 && args[0] != null && !gamer.isAlive()) {
            if (Bukkit.getPlayer(args[0]) != null) {
                Bukkit.getPlayerExact(sender.getName()).teleport(
                        sender.getServer().getPlayer(args[0]).getLocation().add(0, 0.1, 0));
                return true;
            }
            if (args[0].equalsIgnoreCase("feast")) {
                if (hg.feastLoc.getBlockY() > 0) {
                    Bukkit.getPlayerExact(sender.getName()).teleport(
                            hg.feastLoc.getWorld().getHighestBlockAt(hg.feastLoc).getLocation().clone().add(0.5, 1, 0.5));
                } else
                    sender.sendMessage(ChatColor.YELLOW + "The feast has not started yet!");
                return true;
            }
        }
        sender.sendMessage(ChatColor.YELLOW + "Either you are not a spectator or player doesnt exist!");
        return true;
    }
}
