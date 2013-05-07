package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Managers.TranslationManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ride implements CommandExecutor {
    private TranslationManager cm = HungergamesApi.getTranslationManager();
    private PlayerManager pm = HungergamesApi.getPlayerManager();
    public String description = "Toggle the ability to ride on top of mobs and players";

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (args.length == 1 && args[0].equalsIgnoreCase(cm.getCommandRideNameOfRideall()) && sender.isOp()) {
            Player p = Bukkit.getPlayerExact(sender.getName());
            Player last = p;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player != p) {
                    player.leaveVehicle();
                    last.setPassenger(player);
                    last = player;
                }
            }
            Bukkit.broadcastMessage(cm.getCommandRideRideAll());
        } else {
            gamer.setRiding(!gamer.canRide());
            sender.sendMessage(String.format(cm.getCommandRideToggle(), gamer.canRide()));
        }
        return true;
    }
}
