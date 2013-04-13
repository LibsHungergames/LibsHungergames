package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Invis extends Extender implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (cmd.getName().equalsIgnoreCase("invis")) {
            Player p = gamer.getPlayer();
            if (p.isOp() || sender.hasPermission("hungergames.invis")) {
                if (args.length > 0) {
                    if (args[0].toLowerCase().equals("show")) {
                        p.sendMessage(ChatColor.RED + "You just forced all current spectators to show themselves to you.");
                        gamer.seeInvis(true);
                        gamer.updateOthersToSelf();
                    } else if (args[0].toLowerCase().equals("hide")) {
                        gamer.seeInvis(false);
                        gamer.updateOthersToSelf();
                        p.sendMessage(ChatColor.RED + "Hidden spectators");
                    } else if (args[0].toLowerCase().equals("showall")) {
                        for (Gamer game : pm.getGamers()) {
                            game.seeInvis(true);
                            game.updateSelfToOthers();
                        }
                        p.sendMessage(ChatColor.RED + "All current players are now visible to each other");
                    } else if (args[0].toLowerCase().equals("hideall")) {
                        for (Gamer game : pm.getGamers()) {
                            game.seeInvis(false);
                            game.updateSelfToOthers();
                        }
                        p.sendMessage(ChatColor.RED + "Hidden all spectators");
                    } else if (args[0].toLowerCase().equals("hideplayer")) {
                        if (args.length > 1) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                pm.getGamer(Bukkit.getPlayer(args[1])).hide();
                                p.sendMessage(ChatColor.RED + "Hidden " + Bukkit.getPlayer(args[1]).getName());
                            } else
                                p.sendMessage(ChatColor.RED + "Can't find the player " + args[1]);
                        } else
                            p.sendMessage(ChatColor.RED + "You must give a playername");
                    } else if (args[0].toLowerCase().equals("showplayer")) {
                        if (args.length > 1) {
                            if (Bukkit.getPlayer(args[1]) != null) {
                                pm.getGamer(Bukkit.getPlayer(args[1])).show();
                                p.sendMessage(ChatColor.RED + "Revealed " + Bukkit.getPlayer(args[1]).getName());
                            } else
                                p.sendMessage(ChatColor.RED + "You must define a proper player name");
                        } else
                            p.sendMessage(ChatColor.RED + "You must give a playername");
                    } else
                        p.sendMessage(ChatColor.RED
                                + "Dude.. Use show, showall, hide, hideall, showplayer, hideplayer as parameters");
                } else
                    p.sendMessage(ChatColor.RED + "Dude.. Use show, showall, hide, hideall, showplayer, hideplayer as parameters");
            } else
                p.sendMessage(ChatColor.RED + "Op only command");
            return true;
        }

        return true;
    }

}
