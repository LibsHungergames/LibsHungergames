package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceStart implements CommandExecutor {
    private Hungergames hg = HungergamesApi.getHungergames();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender.hasPermission("hungergames.forcestart")) {
            if (hg.currentTime >= 0) {
                sender.sendMessage(ChatColor.RED + "The game has already started!");
                return true;
            }
            if (args.length > 0 && hg.isNumeric(args[0]) && Integer.parseInt(args[0]) > 0) {
                hg.currentTime = -Math.abs(Integer.parseInt(args[0]));
                Bukkit.broadcastMessage(ChatColor.RED + sender.getName() + " changed the countdown time to "
                        + hg.returnTime(hg.currentTime) + "!");
            } else if (args.length > 0) {
                sender.sendMessage(ChatColor.RED + "Thats not a number");
            } else {
                hg.startGame();
            }
        } else
            sender.sendMessage("You do not have permission");
        return true;
    }
}
