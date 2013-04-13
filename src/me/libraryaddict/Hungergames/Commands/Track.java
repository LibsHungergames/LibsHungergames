package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Kits.Tracker;
import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Track extends Extender implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (kits.hasAbility(gamer.getPlayer(), "Tracker")) {
            if (args.length > 0) {
                Gamer victim = pm.getGamer(Bukkit.getPlayer(args[0]));
                if (victim.isAlive()) {
                    Tracker.locked.remove(sender.getName());
                    gamer.getPlayer().setCompassTarget(victim.getPlayer().getPlayer().getLocation());
                    Tracker.tracking.put(gamer.getPlayer(), victim.getPlayer());
                    Tracker.locked.add(sender.getName());
                    sender.sendMessage(ChatColor.YELLOW + "Compass pointing at " + victim.getPlayer().getName() + " who is "
                            + ((int) victim.getPlayer().getLocation().distance(gamer.getPlayer().getLocation()))
                            + " blocks away at location (" + victim.getPlayer().getLocation().getBlockX() + ", "
                            + victim.getPlayer().getLocation().getBlockY() + ", " + victim.getPlayer().getLocation().getBlockZ()
                            + ")");
                }
            } else {
                if (Tracker.locked.contains(sender.getName())) {
                    sender.sendMessage(ChatColor.YELLOW + "No longer locked on target");
                    Tracker.locked.remove(sender.getName());
                } else
                    sender.sendMessage(ChatColor.YELLOW + "Use /lock <Target Player>");
            }
        } else
            sender.sendMessage(ChatColor.YELLOW + "You are not kit Tracker!");
        return true;
    }
}
