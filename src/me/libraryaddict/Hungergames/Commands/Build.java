package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Build extends Extender implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (sender.hasPermission("Hungergames.build")) {
            if (args.length > 0) {
                Player player = sender.getServer().getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(ChatColor.YELLOW + "Player doesn't exist");
                    return true;
                }
                Gamer game = pm.getGamer(player);
                game.setBuild(!game.canBuild());
                game.getPlayer().sendMessage(
                        ChatColor.YELLOW + sender.getName() + " has set your build mode to " + game.canBuild());
                sender.sendMessage(ChatColor.YELLOW + "You have set " + game.getName() + " build mode to " + game.canBuild());
            } else {
                gamer.setBuild(!gamer.canBuild());
                sender.sendMessage(ChatColor.YELLOW + "Changed own build mode to " + gamer.canBuild());
            }
        }
        return true;
    }
}
