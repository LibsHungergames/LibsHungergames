package me.libraryaddict.Hungergames.Commands;


import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Managers.SpectatorManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvisCommand implements CommandExecutor {
    private TranslationConfig cm = HungergamesApi.getConfigManager().getTranslationsConfig();
    public String description = "Used for toggling invisibility";
    private PlayerManager pm = HungergamesApi.getPlayerManager();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender.hasPermission("hungergames.invis")) {
            Gamer gamer = pm.getGamer(sender.getName());
            Player p = gamer.getPlayer();
            if (args.length > 0) {
                if (args[0].toLowerCase().equals(cm.getCommandInvisNameOfShow())) {
                    p.sendMessage(cm.getCommandInvisShow());
                    gamer.setSeeInvisibleSpectators(true);
                    SpectatorManager.getInstance().updateCanSeeSpectators(gamer);
                } else if (args[0].toLowerCase().equals(cm.getCommandInvisNameOfHide())) {
                    gamer.setSeeInvisibleSpectators(false);
                    SpectatorManager.getInstance().updateCanSeeSpectators(gamer);
                    p.sendMessage(cm.getCommandInvisHide());
                } else if (args[0].toLowerCase().equals(cm.getCommandInvisNameOfShowAll())) {
                    for (Gamer game : pm.getGamers()) {
                        gamer.setSeeInvisibleSpectators(true);
                        SpectatorManager.getInstance().updateCanSeeSpectators(gamer);
                    }
                    p.sendMessage(cm.getCommandInvisShowAll());
                } else if (args[0].toLowerCase().equals(cm.getCommandInvisNameOfHideAll())) {
                    for (Gamer game : pm.getGamers()) {
                        gamer.setSeeInvisibleSpectators(false);
                        SpectatorManager.getInstance().updateCanSeeSpectators(gamer);
                    }
                    p.sendMessage(cm.getCommandInvisHideAll());
                } else if (args[0].toLowerCase().equals(cm.getCommandInvisNameOfHidePlayer())) {
                    if (args.length > 1) {
                        if (Bukkit.getPlayer(args[1]) != null) {
                            throw new UnsupportedOperationException("Unsupported in 1.8");
                            //p.sendMessage(String.format(cm.getCommandInvisHidePlayerSuccess(), Bukkit.getPlayer(args[1].getName()));
                        } else
                            p.sendMessage(String.format(cm.getCommandInvisHidePlayerFail(), args[1]));
                    } else
                        p.sendMessage(cm.getCommandInvisHidePlayerNoArgs());
                } else if (args[0].toLowerCase().equals(cm.getCommandInvisNameOfShowPlayer())) {
                    if (args.length > 1) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if (player != null) {
                            throw new UnsupportedOperationException("Unsupported in 1.8");
                            //p.sendMessage(String.format(cm.getCommandInvisShowPlayerSuccess(), player.getName()));
                        } else
                            p.sendMessage(String.format(cm.getCommandInvisShowPlayerFail(), args[1]));
                    } else
                        p.sendMessage(cm.getCommandInvisShowPlayerNoArgs());
                } else
                    p.sendMessage(cm.getCommandInvisNotEnoughArguments());
            } else
                p.sendMessage(cm.getCommandInvisNotEnoughArguments());
        } else
            sender.sendMessage(cm.getCommandInvisNoPermission());
        return true;
    }
}
