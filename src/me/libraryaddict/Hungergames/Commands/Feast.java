package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.TranslationManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Feast implements CommandExecutor {
    private TranslationManager cm = HungergamesApi.getTranslationManager();
    private Hungergames hg = HungergamesApi.getHungergames();
    public String description = "Point your compass towards the feast";

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (hg.feastLoc.getY() > 0) {
            ((Player) sender).setCompassTarget(hg.feastLoc);
            sender.sendMessage(cm.getCommandFeastHappened());
        } else
            sender.sendMessage(cm.getCommandFeastNotHappened());
        return true;
    }
}
