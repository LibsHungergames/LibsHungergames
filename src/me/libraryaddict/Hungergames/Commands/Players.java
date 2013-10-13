package me.libraryaddict.Hungergames.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.libraryaddict.Hungergames.Hungergames;

import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Players implements CommandExecutor {
    public String[] aliases = new String[] { "list", "who", "gamers" };
    private TranslationConfig cm = HungergamesApi.getConfigManager().getTranslationsConfig();
    public String description = "See the gamers and spectators online";
    private Hungergames hg = HungergamesApi.getHungergames();
    private PlayerManager pm = HungergamesApi.getPlayerManager();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<Gamer> gamers = pm.getAliveGamers();
        ArrayList<String> gamerNames = new ArrayList<String>();
        for (Gamer game : gamers)
            gamerNames.add(game.getPlayer().getDisplayName());
        Collections.sort(gamerNames);
        String gamersName = "No gamers";
        if (gamerNames.size() > 0)
            gamersName = StringUtils.join(gamerNames, ChatColor.GRAY + ", " + ChatColor.GRAY);
        sender.sendMessage(String.format(cm.getCommandPlayers(), gamers.size(), (pm.getGamers().size() - gamers.size()),
                gamersName));
        if (hg.currentTime >= 0)
            sender.sendMessage(String.format(cm.getCommandPlayersTimeStatusStarted(), hg.returnTime(hg.currentTime)));
        else
            sender.sendMessage(String.format(cm.getCommandPlayersTimeStatusStarting(), hg.returnTime(hg.currentTime)));
        return true;
    }
}
