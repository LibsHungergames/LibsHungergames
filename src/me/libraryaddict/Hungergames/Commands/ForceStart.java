package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.TranslationManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceStart implements CommandExecutor {
    private TranslationManager cm = HungergamesApi.getTranslationManager();
    private Hungergames hg = HungergamesApi.getHungergames();
    public String[] aliases = new String[] { "fstart" };
    public String description = "Change the time until the game starts";

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender.hasPermission("hungergames.forcestart")) {
            if (hg.currentTime >= 0) {
                sender.sendMessage(cm.getCommandForceStartAlreadyStarted());
                return true;
            }
            if (args.length > 0) {
                if (hg.isNumeric(args[0]) && Integer.parseInt(args[0]) > 0) {
                    hg.currentTime = -Math.abs(Integer.parseInt(args[0]));
                    Bukkit.broadcastMessage(String.format(cm.getCommandForceStartChangedCountdownTime(), sender.getName(),
                            hg.returnTime(hg.currentTime)));
                } else {
                    sender.sendMessage(String.format(cm.getCommandForceStartNotANumber(), args[0]));
                }
            } else {
                hg.startGame();
            }
        } else
            sender.sendMessage(cm.getCommandForceStartNoPermission());
        return true;
    }
}
