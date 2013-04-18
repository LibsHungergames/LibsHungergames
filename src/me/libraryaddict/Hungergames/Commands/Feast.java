package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Feast implements CommandExecutor {
    private Hungergames hg = HungergamesApi.getHungergames();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (hg.feastLoc.getY() > 0) {
            ((Player) sender).setCompassTarget(hg.feastLoc);
            sender.sendMessage(ChatColor.YELLOW + "Compass now pointing to the feast!");
        } else
            sender.sendMessage(ChatColor.YELLOW + "The feast has not happened yet!");
        return true;
    }
}
