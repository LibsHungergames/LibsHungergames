package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Spawn implements CommandExecutor {
    private PlayerManager pm = HungergamesApi.getPlayerManager();

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
