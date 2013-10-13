package me.libraryaddict.Hungergames.Commands;


import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Suicide implements CommandExecutor {
    private TranslationConfig cm = HungergamesApi.getConfigManager().getTranslationsConfig();
    public String description = "Commit suicide to this cruel cruel world";
    private PlayerManager pm = HungergamesApi.getPlayerManager();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (args.length == 0) {
            if (gamer.isAlive()) {
                pm.killPlayer(gamer, null, gamer.getPlayer().getLocation(), gamer.getInventory(),
                        String.format(cm.getCommandSuicideKillMessage(), gamer.getName()));
            } else
                sender.sendMessage(cm.getCommandSuicideNotAlive());
        } else {
            if (!sender.hasPermission("hungergames.kill"))
                sender.sendMessage(cm.getCommandSuicideNoPermission());
            else if (Bukkit.getPlayer(args[0]) == null)
                sender.sendMessage(cm.getCommandSuicideDoesntExist());
            else {
                Gamer murdered = pm.getGamer(Bukkit.getPlayer(args[0]));
                pm.killPlayer(murdered, null, murdered.getPlayer().getLocation(), murdered.getInventory(),
                        String.format(cm.getCommandSuicideAssistedDeathMessage(), murdered.getName()));
            }
        }
        return true;
    }
}
