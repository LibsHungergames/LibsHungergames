package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.ConfigManager;
import me.libraryaddict.Hungergames.Managers.FeastManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceFeast implements CommandExecutor {
    private ConfigManager config = HungergamesApi.getConfigManager();
    private Hungergames hg = HungergamesApi.getHungergames();
    private FeastManager fm = HungergamesApi.getFeastManager();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender.hasPermission("Hungergames.forcefeast")) {
            int radius = config.getFeastSize();
            int chestLayers = config.getChestLayers();
            if (args.length > 0) {
                if (hg.isNumeric(args[0])) {
                    radius = Integer.parseInt(args[0]);
                    if (radius <= 0) {
                        sender.sendMessage(ChatColor.RED + "Don't be ridiculous!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "'" + args[0] + "' isn't a number!");
                    return true;
                }
                if (args.length > 1) {
                    if (hg.isNumeric(args[1])) {
                        chestLayers = Integer.parseInt(args[1]);
                        if (radius <= 0) {
                            sender.sendMessage(ChatColor.RED + "Don't be ridiculous!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "'" + args[1] + "' isn't a number!");
                        return true;
                    }
                }
            }
            Player p = (Player) sender;
            Location loc = p.getLocation().clone();
            int height = fm.getSpawnHeight(loc, radius);
            loc.setY(height);
            fm.generateSpawn(loc, height, radius);
            fm.generateChests(loc, chestLayers);
            Bukkit.broadcastMessage(ChatColor.RED + "A feast has been spawned at (" + loc.getBlockX() + ", " + loc.getBlockY()
                    + ", " + loc.getBlockZ() + ")");
        }
        return true;
    }
}
