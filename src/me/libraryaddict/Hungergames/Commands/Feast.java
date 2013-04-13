package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Feast extends Extender implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (hg.feastLoc.getY() > 0) {
            gamer.getPlayer().setCompassTarget(hg.feastLoc);
            sender.sendMessage(ChatColor.YELLOW + "Compass now pointing to the feast!");
        } else
            sender.sendMessage(ChatColor.YELLOW + "The feast has not happened yet!");
        return true;
    }
}
