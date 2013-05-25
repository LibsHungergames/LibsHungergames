package me.libraryaddict.Hungergames.Managers;

import java.util.Arrays;
import java.util.List;

import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.KitInventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryManager {

    private ItemStack kitSelector = null;

    public ItemStack generateItem(int id, int dataValue, String name, List<String> lore) {
        ItemStack item = new ItemStack(id, 1, (short) dataValue);
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.setDisplayName(ChatColor.WHITE + name);
        }
        if (lore != null && lore.size() > 0) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack generateItem(int type, int dataValue, String name, String[] lore) {
        return generateItem(type, dataValue, name, Arrays.asList(lore));
    }

    public ItemStack generateItem(Material type, int dataValue, String name, List<String> lore) {
        return generateItem(type.getId(), dataValue, name, lore);
    }

    public ItemStack generateItem(Material type, int dataValue, String name, String[] lore) {
        return generateItem(type.getId(), dataValue, name, Arrays.asList(lore));
    }

    public KitInventory getInventory(Player p) {
        if (p.hasMetadata("KitInventory"))
            return (KitInventory) p.getMetadata("KitInventory").get(0).value();
        return null;
    }

    public ItemStack getKitSelector() {
        if (kitSelector == null) {
            ItemStack item = HungergamesApi.getConfigManager().getKitSelectorIcon();
            kitSelector = generateItem(item.getType(), item.getDurability(), getKitSelectorName(), HungergamesApi
                    .getTranslationManager().getItemKitSelectorDescription());
        }
        return kitSelector;
    }

    public String getKitSelectorName() {
        return HungergamesApi.getTranslationManager().getItemKitSelectorName();
    }

    public void openKitInventory(Player p) {
        KitInventory inv = new KitInventory(p);
        inv.setKits();
        inv.openInventory();
    }
}
