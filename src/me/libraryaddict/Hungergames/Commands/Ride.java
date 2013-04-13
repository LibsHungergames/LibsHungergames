package me.libraryaddict.Hungergames.Commands;


import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ride extends Extender implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd,
      String commandLabel, String[] args) {
    Gamer gamer = pm.getGamer(sender.getName());
    if (args.length == 1 && args[0].equals("rideall") && sender.isOp()) {
      Player p = Bukkit.getPlayerExact(sender.getName());
      Player last = p;
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (player != p) {
          player.leaveVehicle();
          last.setPassenger(player);
          last = player;
        }
      }
      Bukkit.broadcastMessage(ChatColor.RED + "Giddy up horsie!");
    } else {
      gamer.setRiding(!gamer.canRide());
      sender.sendMessage(ChatColor.GREEN + "Toggled riding to "
          + gamer.canRide() + "! Yee-haw!");
    }
    return true;
  }
}
