package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.ChatManager;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Kit implements CommandExecutor {
    private Hungergames hg = HungergamesApi.getHungergames();
    private KitManager kits = HungergamesApi.getKitManager();
    private ChatManager cm = HungergamesApi.getChatManager();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = Bukkit.getPlayerExact(sender.getName());
        if (args.length > 0) {
            if (hg.currentTime < 0) {
                String kitName = StringUtils.join(args, " ");
                me.libraryaddict.Hungergames.Types.Kit kit = kits.getKitByName(kitName);
                if (kit == null) {
                    p.sendMessage(cm.getCommandKitKitDoesntExist());
                    return true;
                }
                if (!kits.ownsKit((Player) sender, kit)) {
                    p.sendMessage(cm.getCommandKitNoPermission());
                    return true;
                }
                if (kit == kits.getKitByPlayer(p)) {
                    sender.sendMessage(String.format(cm.getCommandKitAlreadyUsing(), kit.getName()));
                    return true;
                }
                kits.setKit(p, kit.getName());
                p.sendMessage(String.format(cm.getCommandKitNowUsingKit(), kit.getName()));
            } else {
                p.sendMessage(cm.getCommandKitGameAlreadyStarted());
            }
        } else {
            kits.showKits(p);
        }
        return true;
    }

}
