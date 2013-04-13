package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Kill extends Extender implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Please use /suicide");
        } else {
            if (!(sender.isOp() || sender.hasPermission("hungergames.kill")))
                sender.sendMessage(ChatColor.RED + "You may not kill someone..");
            else if (Bukkit.getPlayer(args[0]) == null)
                sender.sendMessage(ChatColor.RED + "He doesn't exist");
            else {
                Gamer murdered = pm.getGamer(Bukkit.getPlayer(args[0]));
                pm.killPlayer(murdered, null, murdered.getPlayer().getLocation(), murdered.getInventory(), murdered.getName()
                        + " was killed by a command.");
            }
        }
        return true;
    }
}
