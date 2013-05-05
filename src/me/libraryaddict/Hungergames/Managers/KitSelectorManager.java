package me.libraryaddict.Hungergames.Managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitSelectorManager {
    
    Inventory icon;
    ItemStack kitSelector = null;

    public void createInventory(String invName, ArrayList<ItemStack> items) {
        int size = (int) (Math.ceil((double) items.size() / 9)) * 9;
        icon = Bukkit.createInventory(null, size, invName);
        for (int i = 0; i < items.size(); i++)
            icon.setItem(i, items.get(i));
    }

    public ItemStack generateItem(Material type, int dataValue, String name, List<String> lore) {
        return generateItem(type.getId(), dataValue, name, lore);
    }

    public ItemStack generateItem(int id, int dataValue, String name, List<String> lore) {
        ItemStack item = new ItemStack(id, 1, (short) dataValue);
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.setDisplayName(ChatColor.WHITE + name);
        }
        if (lore != null) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getKitSelector() {
        if (kitSelector == null) {
            ItemStack item = HungergamesApi.getConfigManager().getKitSelectorIcon();
            kitSelector = generateItem(item.getType(), item.getDurability(), getKitSelectorName(),
                    Arrays.asList(getKitSelectorDescription().split("\n")));
        }
        return kitSelector;
    }

    public String getKitSelectorName() {
        return HungergamesApi.getChatManager().getItemKitSelectorName();
    }

    public String getKitSelectorDescription() {
        return HungergamesApi.getChatManager().getItemKitSelectorDescription();
    }

    public Inventory getInventory() {
        return icon;
    }
}
