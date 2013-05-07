package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.ChatManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceTime implements CommandExecutor {
    private ChatManager cm = HungergamesApi.getChatManager();
    private Hungergames hg = HungergamesApi.getHungergames();
    public String[] aliases = new String[] { "ftime" };

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender.hasPermission("Hungergames.forcetime")) {
            if (args.length > 0) {
                if (hg.isNumeric(args[0])) {
                    int newTime = Integer.parseInt(args[0]);
                    if (newTime == 0 && hg.currentTime != 0)
                        hg.startGame();
                    else
                        hg.currentTime = newTime;
                    Bukkit.broadcastMessage(String.format(cm.getCommandForceTimeBroadcast(), sender.getName(), args[0]));
                } else
                    sender.sendMessage(cm.getCommandForceTimeNotANumber());
            } else
                sender.sendMessage(cm.getCommandForceTimeInfo());
        } else
            sender.sendMessage(cm.getCommandForceTimeNoPermission());
        return true;
    }
}
