package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Spawn extends Extender implements CommandExecutor {
    Hungergames hg;

    public Spawn(Hungergames hG) {
        hg = hG;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (cmd.getName().equalsIgnoreCase("spawn")) {
            if (!gamer.isAlive()) {
                pm.sendToSpawn(gamer.getPlayer());
                return true;
            } else
                gamer.getPlayer().sendMessage(ChatColor.RED + "Spectators only command");
        }
        return true;
    }

}
