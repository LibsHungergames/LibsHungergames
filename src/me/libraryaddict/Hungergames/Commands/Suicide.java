package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Suicide implements CommandExecutor {
    private PlayerManager pm = HungergamesApi.getPlayerManager();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (args.length == 0) {
            if (gamer.isAlive()) {
                pm.killPlayer(gamer, null, gamer.getPlayer().getLocation(), gamer.getInventory(), gamer.getName()
                        + " commited suicide.");
            } else
                sender.sendMessage(ChatColor.RED + "Dead men can't die");
        } else {
            if (!(sender.isOp() || sender.hasPermission("hungergames.kill")))
                sender.sendMessage(ChatColor.RED + "You may not kill someone..");
            else if (Bukkit.getPlayer(args[0]) == null)
                sender.sendMessage(ChatColor.RED + "He doesn't exist");
            else {
                Gamer murdered = pm.getGamer(Bukkit.getPlayer(args[0]));
                pm.killPlayer(murdered, null, murdered.getPlayer().getLocation(), murdered.getInventory(), murdered.getName()
                        + " was helped on the path to suicide.");
            }
        }
        return true;
    }
}
