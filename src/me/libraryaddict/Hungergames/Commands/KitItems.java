package me.libraryaddict.Hungergames.Commands;

import java.util.ArrayList;
import java.util.Collections;

import me.libraryaddict.Hungergames.Managers.ChatManager;
import me.libraryaddict.Hungergames.Managers.EnchantmentManager;
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

    private KitManager kits = HungergamesApi.getKitManager();
    private ChatManager cm = HungergamesApi.getChatManager();

    private String itemToName(ItemStack item) {
        // TODO Add chat translation
        if (item == null)
            return "null";
        String name = (item.getAmount() > 1 ? item.getAmount() + " " : "")
                + (item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? ChatColor.stripColor(item.getItemMeta()
                        .getDisplayName()) : kits.toReadable(item.getType().name())) + (item.getAmount() > 1 ? "s" : "");
        ArrayList<String> enchants = new ArrayList<String>();
        for (Enchantment enchant : item.getEnchantments().keySet()) {
            String eName = EnchantmentManager.getReadableName(enchant);
            enchants.add(kits.toReadable((eName.contains("%no%") ? eName.replace("%no%", "" + item.getEnchantmentLevel(enchant))
                    : eName + " " + item.getEnchantmentLevel(enchant))));
        }
        Collections.sort(enchants);
        if (enchants.size() > 0)
            name += " with enchant" + (enchants.size() > 1 ? "s" : "") + ": " + StringUtils.join(enchants, ", ");
        return name;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length > 0) {
            Kit kit = kits.getKitByName(StringUtils.join(args, " "));
            if (kit == null) {
                sender.sendMessage(cm.getMessagePlayerSendKitItemsDoesntExist());
                return true;
            }
            sender.sendMessage(String.format(cm.getMessagePlayerSendKitItemsKitName(), kit.getName()));
            sender.sendMessage(String.format(cm.getMessagePlayerSendKitItemsKitHelmet(), itemToName(kit.getArmor()[3])));
            sender.sendMessage(String.format(cm.getMessagePlayerSendKitItemsKitChestplate(), itemToName(kit.getArmor()[2])));
            sender.sendMessage(String.format(cm.getMessagePlayerSendKitItemsKitLeggings(), itemToName(kit.getArmor()[1])));
            sender.sendMessage(String.format(cm.getMessagePlayerSendKitItemsKitBoots(), itemToName(kit.getArmor()[0])));
            ArrayList<String> items = new ArrayList<String>();
            for (ItemStack item : kit.getItems())
                items.add(itemToName(item));
            Collections.sort(items);
            sender.sendMessage(String.format(cm.getMessagePlayerSendKitItemsOtherItems(),
                    (items.size() > 0 ? StringUtils.join(items, ", ") + "." : cm.getMessagePlayerSendKitItemsNoItems())));
        } else
            sender.sendMessage(cm.getCommandKitItemsDefineKitName());
        return true;
    }

}
