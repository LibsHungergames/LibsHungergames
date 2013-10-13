package me.libraryaddict.Hungergames.Commands;


import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Kit;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class KitInfo implements CommandExecutor {
    private TranslationConfig cm = HungergamesApi.getConfigManager().getTranslationsConfig();
    public String description = "View the information on a kit";
    private KitManager kits = HungergamesApi.getKitManager();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length > 0) {
            Kit kit = kits.getKitByName(StringUtils.join(args, " "));
            if (kit == null) {
                sender.sendMessage(cm.getMessagePlayerKitDescriptionDoesntExist());
                return true;
            }
            sender.sendMessage(String.format(cm.getMessagePlayerKitDescriptionName(), kit.getName()));
            sender.sendMessage(String.format(cm.getMessagePlayerKitDesciptionId(), kit.getId()));
            sender.sendMessage(kit.getDescription());
            if (kit.isFree())
                sender.sendMessage(cm.getMessagePlayerKitDesciprionPriceFree());
            else if (kit.getPrice() == -1)
                sender.sendMessage(cm.getMessagePlayerKitDesciprionPriceUnbuyable());
            else
                sender.sendMessage(String.format(cm.getMessagePlayerKitDesciprionPrice(), kit.getPrice()));
            sender.sendMessage(String.format(cm.getMessagePlayerKitDescritionMoreInfo(), kit.getName()));
        } else
            sender.sendMessage(cm.getCommandKitInfoDefineKitName());
        return true;
    }

}
