package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.TranslationManager;
import me.libraryaddict.Hungergames.Managers.KitManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class KitInventory extends PageInventory {

    public KitInventory(Player player) {
        super(player, HungergamesApi.getConfigManager().isKitSelectorDynamicSize());
        maxInvSize = HungergamesApi.getConfigManager().getKitSelectorInventorySize();
        title = HungergamesApi.getTranslationManager().getInventoryWindowSelectKitTitle();
        ItemStack item = HungergamesApi.getConfigManager().getKitSelectorBack();
        backAPage = HungergamesApi.getInventoryManager().generateItem(item.getType(), item.getDurability(),
                tm.getItemKitSelectorBackName(), tm.getItemKitSelectorBackDescription());
        backAPage.setAmount(0);
        item = HungergamesApi.getConfigManager().getKitSelectorForward();
        forwardsAPage = HungergamesApi.getInventoryManager().generateItem(item.getType(), item.getDurability(),
                tm.getItemKitSelectorForwardsName(), tm.getItemKitSelectorForwardsDescription());
        forwardsAPage.setAmount(0);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getViewers().equals(inv.getViewers())) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                if (item.equals(getBackPage())) {
                    setPage(currentPage - 1);
                } else if (item.equals(getForwardsPage())) {
                    setPage(currentPage + 1);
                } else {
                    Kit kit = HungergamesApi.getKitManager().getKitByName(
                            ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                    if (kit != null)
                        Bukkit.dispatchCommand((CommandSender) event.getWhoClicked(), "kit " + kit.getName());
                }
            }
        }
    }

    public void setKits() {
        pages.clear();
        KitManager kits = HungergamesApi.getKitManager();
        boolean usePages = kits.getKits().size() > maxInvSize;
        ItemStack[] items = null;
        int currentSlot = 0;
        ArrayList<Kit> allKits = kits.getKits();
        for (int currentKit = 0; currentKit < allKits.size(); currentKit++) {
            if (items == null) {
                int size = maxInvSize;
                if (dynamicInventorySize) {
                    size = allKits.size() - currentKit;
                    if (usePages)
                        size += 9;
                }
                if (!dynamicInventorySize)
                    size = maxInvSize;
                items = generatePage(size);
            }
            Kit kit = allKits.get(currentKit);
            ItemStack item = kit.getIcon();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + kit.getName());
            meta.setLore(wrap(kit.getDescription()));
            item.setItemMeta(meta);
            if (item.getAmount() == 1)
                item.setAmount(0);
            items[currentSlot++] = item;
            if (currentSlot == items.length - (usePages ? 9 : 0) || currentKit + 1 == allKits.size()) {
                if (usePages) {
                    if (currentPage != 0)
                        items[items.length - 9] = getBackPage();
                    if (currentKit + 1 < allKits.size())
                        items[items.length - 1] = getForwardsPage();
                }
                pages.put(currentPage, items);
                currentPage++;
                currentSlot = 0;
                items = null;
            }
        }
        currentPage = 0;
    }

    private List<String> wrap(String string) {
        String[] split = string.split(" ");
        string = "";
        ChatColor color = ChatColor.BLUE;
        ArrayList<String> newString = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            if (string.length() > 20 || string.endsWith(".") || string.endsWith("!")) {
                newString.add(color + string);
                if (string.endsWith(".") || string.endsWith("!"))
                    newString.add("");
                string = "";
            }
            string += (string.length() == 0 ? "" : " ") + split[i];
        }
        newString.add(color + string);
        return newString;
    }

}
