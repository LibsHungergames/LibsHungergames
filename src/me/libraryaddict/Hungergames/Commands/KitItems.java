package me.libraryaddict.Hungergames.Commands;

import java.util.ArrayList;
import java.util.Collections;

import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Managers.NameManager;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Kit;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class KitItems implements CommandExecutor {

    public String description = "View the items given with a kit";
    private final KitManager kits = HungergamesApi.getKitManager();
    private final NameManager name = HungergamesApi.getNameManager();
    private final TranslationConfig tm = HungergamesApi.getConfigManager().getTranslationsConfig();

    private String itemToName(ItemStack item) {
        if (item == null)
            return "null";
        String itemName = (item.getAmount() > 1 ? item.getAmount() + " " : "")
                + (item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? ChatColor.stripColor(item.getItemMeta()
                        .getDisplayName()) : name.getItemName(item)) + (item.getAmount() > 1 ? "s" : "");
        ArrayList<String> enchants = new ArrayList<String>();
        for (Enchantment enchant : item.getEnchantments().keySet()) {
            String eName = name.getEnchantName(enchant);
            enchants.add(toReadable(eName + " " + item.getEnchantmentLevel(enchant)));
        }
        Collections.sort(enchants);
        if (enchants.size() > 0) {
            if (enchants.size() == 1)
                itemName = String.format(tm.getCommandKitItemsItemWithEnchant(), itemName, StringUtils.join(enchants, ", "));
            else
                itemName = String.format(tm.getCommandKitItemsItemWithEnchant(), itemName, StringUtils.join(enchants, ", "));
        }
        return itemName;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length > 0) {
            Kit kit = kits.getKitByName(StringUtils.join(args, " "));
            if (kit == null) {
                sender.sendMessage(tm.getMessagePlayerSendKitItemsDoesntExist());
                return true;
            }
            sender.sendMessage(String.format(tm.getMessagePlayerSendKitItemsKitName(), kit.getName()));
            sender.sendMessage(String.format(tm.getMessagePlayerSendKitItemsKitHelmet(), itemToName(kit.getArmor()[3])));
            sender.sendMessage(String.format(tm.getMessagePlayerSendKitItemsKitChestplate(), itemToName(kit.getArmor()[2])));
            sender.sendMessage(String.format(tm.getMessagePlayerSendKitItemsKitLeggings(), itemToName(kit.getArmor()[1])));
            sender.sendMessage(String.format(tm.getMessagePlayerSendKitItemsKitBoots(), itemToName(kit.getArmor()[0])));
            ArrayList<String> items = new ArrayList<String>();
            for (ItemStack item : kit.getItems())
                items.add(itemToName(item));
            Collections.sort(items);
            sender.sendMessage(String.format(tm.getMessagePlayerSendKitItemsOtherItems(),
                    (items.size() > 0 ? StringUtils.join(items, ", ") + "." : tm.getMessagePlayerSendKitItemsNoItems())));
        } else
            sender.sendMessage(tm.getCommandKitItemsDefineKitName());
        return true;
    }

    private String toReadable(String string) {
        String[] names = string.split("_");
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].substring(0, 1) + names[i].substring(1).toLowerCase();
        }
        return StringUtils.join(names, " ");
    }

}
