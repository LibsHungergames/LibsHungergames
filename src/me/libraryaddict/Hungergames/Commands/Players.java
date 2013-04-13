package me.libraryaddict.Hungergames.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Players extends Extender implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<Gamer> gamers = pm.getAliveGamers();
        sender.sendMessage(ChatColor.DARK_GRAY + "There are " + gamers.size() + " gamers and "
                + (pm.getGamers().size() - gamers.size()) + " spectators");
        ArrayList<String> gamerNames = new ArrayList<String>();
        for (Gamer game : gamers)
            gamerNames.add(game.getPlayer().getDisplayName());
        Collections.sort(gamerNames);
        sender.sendMessage(ChatColor.DARK_GRAY + "Gamers: " + ChatColor.GRAY + StringUtils.join(gamerNames, ChatColor.GRAY + ", " + ChatColor.GRAY));
        if (hg.currentTime >= 0)
            sender.sendMessage(ChatColor.DARK_GRAY + "The game has been going for " + hg.returnTime(hg.currentTime) + ".");
        else
            sender.sendMessage(ChatColor.DARK_GRAY + "The game is starting in " + hg.returnTime(hg.currentTime) + ".");
        return true;
    }

}
