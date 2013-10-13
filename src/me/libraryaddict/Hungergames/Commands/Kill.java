package me.libraryaddict.Hungergames.Commands;


import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Kill implements CommandExecutor {
    private TranslationConfig cm = HungergamesApi.getConfigManager().getTranslationsConfig();
    public String description = "Use /suicide instead to suicide";
    private PlayerManager pm = HungergamesApi.getPlayerManager();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(cm.getCommandKillUseSuicide());
        } else {
            if (!sender.hasPermission("hungergames.kill"))
                sender.sendMessage(cm.getCommandKillSomeoneNoPermission());
            else {
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null)
                    sender.sendMessage(cm.getCommandKillPlayerNotFound());
                else {
                    Gamer murdered = pm.getGamer(Bukkit.getPlayer(args[0]));
                    if (murdered.isAlive())
                        pm.killPlayer(murdered, null, murdered.getPlayer().getLocation(), murdered.getInventory(),
                                String.format(cm.getCommandKillMurderMessage(), murdered.getName()));
                    else
                        sender.sendMessage(cm.getCommandKillNotAlive());
                }
            }
        }
        return true;
    }
}
