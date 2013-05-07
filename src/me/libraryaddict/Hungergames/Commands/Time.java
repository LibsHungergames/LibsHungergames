package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.TranslationManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Time implements CommandExecutor {
    private TranslationManager cm = HungergamesApi.getTranslationManager();
    private Hungergames hg = HungergamesApi.getHungergames();
    public String description = "View the current game time";

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (hg.currentTime >= 0)
            sender.sendMessage(String.format(cm.getCommandTimeStatusStarted(), hg.returnTime(hg.currentTime)));
        else
            sender.sendMessage(String.format(cm.getCommandTimeStatusStarting(), hg.returnTime(hg.currentTime)));
        return true;
    }
}
