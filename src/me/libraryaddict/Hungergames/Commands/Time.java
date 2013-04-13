package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Types.Extender;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Time extends Extender implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (hg.currentTime >= 0)
            sender.sendMessage(ChatColor.DARK_GRAY + "The game has been going for " + hg.returnTime(hg.currentTime) + ".");
        else
            sender.sendMessage(ChatColor.DARK_GRAY + "The game is starting in " + hg.returnTime(hg.currentTime) + ".");
        return true;
    }
}
