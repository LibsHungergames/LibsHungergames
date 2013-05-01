package me.libraryaddict.Hungergames.Commands;

import java.util.ArrayList;
import me.libraryaddict.Hungergames.Managers.ChatManager;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.GiveKitThread;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BuyKit implements CommandExecutor {
    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private KitManager kits = HungergamesApi.getKitManager();
    private ChatManager cm = HungergamesApi.getChatManager();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (args.length > 0) {
            me.libraryaddict.Hungergames.Types.Kit kit = kits.getKitByName(StringUtils.join(args, " "));
            if (kit != null) {
                if (gamer.getBalance() < kit.getPrice()) {
                    sender.sendMessage(cm.getCommandBuyKitCantAfford());
                    return true;
                }
                if (kit.getPrice() == -1 || kit.isFree()) {
                    sender.sendMessage(cm.getCommandBuyKitCantBuyKit());
                    return true;
                }
                if (kits.ownsKit(gamer.getPlayer(), kit)) {
                    sender.sendMessage(cm.getCommandBuyKitAlreadyOwn());
                    return true;
                }
                if (!HungergamesApi.getConfigManager().isMySqlEnabled()) {
                    sender.sendMessage(cm.getCommandBuyKitMysqlNotEnabled());
                    return true;
                }
                gamer.addBalance(-kit.getPrice());
                if (!kits.hisKits.containsKey(gamer.getName()))
                    kits.hisKits.put(gamer.getName(), new ArrayList<me.libraryaddict.Hungergames.Types.Kit>());
                kits.hisKits.get(gamer.getName()).add(kit);
                new GiveKitThread(gamer.getName(), kit.getName()).start();
                sender.sendMessage(cm.getCommandBuyKitPurchasedKit());
                return true;
            }
        }
        sender.sendMessage(cm.getCommandBuyKitNoArgs());
        return true;
    }
}
