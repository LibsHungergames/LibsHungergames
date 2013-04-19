package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceTime implements CommandExecutor {
    private Hungergames hg = HungergamesApi.getHungergames();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender.hasPermission("Hungergames.forcetime")) {
            if (args.length > 0) {
                if (hg.isNumeric(args[0])) {
                    int newTime = Integer.parseInt(args[0]);
                    if (newTime == 0 && hg.currentTime != 0)
                        hg.startGame();
                    else
                        hg.currentTime = newTime;
                    Bukkit.broadcastMessage(ChatColor.RED + sender.getName() + " changed the time to " + args[0] + "!");
                } else
                    sender.sendMessage(ChatColor.RED + "Thats not a number silly!");
            } else
                sender.sendMessage(ChatColor.RED + "/forcetime <NewTime>");
        }
        return true;
    }
}
