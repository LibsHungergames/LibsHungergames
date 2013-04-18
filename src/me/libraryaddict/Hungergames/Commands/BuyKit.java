package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.GiveKitThread;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BuyKit implements CommandExecutor {
    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private KitManager kits = HungergamesApi.getKitManager();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (cmd.getName().equalsIgnoreCase("buykit")) {
            if (args.length > 0) {
                me.libraryaddict.Hungergames.Types.Kit kit = kits.getKitByName(StringUtils.join(args, " "));
                if (kit != null) {
                    if (gamer.getBalance() < kit.getPrice()) {
                        sender.sendMessage(ChatColor.AQUA + "You can't afford this kit!");
                        return true;
                    }
                    if (kit.getPrice() == -1 || kit.isFree()) {
                        sender.sendMessage(ChatColor.AQUA + "You can't buy this kit!");
                        return true;
                    }
                    if (kits.ownsKit(gamer.getPlayer(), kit)) {
                        sender.sendMessage(ChatColor.AQUA + "You already own this kit!");
                        return true;
                    }
                    if (!HungergamesApi.getConfigManager().isMySqlEnabled()) {
                        sender.sendMessage(ChatColor.AQUA
                                + "Magical forces render you powerless and- No. The server owner did not setup mysql.");
                        return true;
                    }
                    gamer.addBalance(-kit.getPrice());
                    kits.hisKits.get(gamer.getName()).add(kit);
                    new GiveKitThread(gamer.getName(), kit.getName()).start();
                    sender.sendMessage(ChatColor.AQUA + "Successfully purchased kit " + kit.getName() + "!");
                    return true;
                }
            }
            sender.sendMessage(ChatColor.AQUA + "You must define a kit id or name.");
        }
        return true;
    }
}
