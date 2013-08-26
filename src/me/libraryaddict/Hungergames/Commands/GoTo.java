package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.TranslationManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GoTo implements CommandExecutor {
    public String[] aliases = new String[] { "watch" };
    private TranslationManager cm = HungergamesApi.getTranslationManager();
    public String description = "A command for spectators to teleport to people";
    private Hungergames hg = HungergamesApi.getHungergames();
    private PlayerManager pm = HungergamesApi.getPlayerManager();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (!gamer.isAlive()) {
            if (args.length > 0) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    gamer.getPlayer().eject();
                    gamer.getPlayer().teleport(sender.getServer().getPlayer(args[0]).getLocation().add(0, 0.1, 0));
                    return true;
                } else if (args[0].equalsIgnoreCase(cm.getCommandGotoNameOfFeast())) {
                    gamer.getPlayer().eject();
                    if (hg.feastLoc.getBlockY() > 0) {
                        gamer.getPlayer().teleport(
                                hg.feastLoc.getWorld().getHighestBlockAt(hg.feastLoc).getLocation().clone().add(0.5, 1, 0.5));
                    } else
                        sender.sendMessage(cm.getCommandGotoFeastFailed());
                    return true;
                } else
                    sender.sendMessage(cm.getCommandGotoPlayerDoesntExist());
            } else
                sender.sendMessage(cm.getCommandGotoNotEnoughArgs());
        } else
            sender.sendMessage(cm.getCommandGotoNotSpectator());
        return true;
    }
}
