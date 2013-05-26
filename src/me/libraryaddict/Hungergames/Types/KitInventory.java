package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.List;

import me.libraryaddict.Hungergames.Managers.KitManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitInventory extends PageInventory {

    public KitInventory(Player player) {
        super(player, HungergamesApi.getConfigManager().isKitSelectorDynamicSize());
        maxInvSize = HungergamesApi.getConfigManager().getKitSelectorInventorySize();
        title = tm.getSelectKitInventoryTitle();
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
        KitManager kits = HungergamesApi.getKitManager();
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        ArrayList<Kit> allKits = kits.getKits();
        for (int currentKit = 0; currentKit < allKits.size(); currentKit++) {
            Kit kit = allKits.get(currentKit);
            ItemStack item = kit.getIcon();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + kit.getName());
            meta.setLore(wrap(kit.getDescription()));
            item.setItemMeta(meta);
            if (item.getAmount() == 1)
                item.setAmount(0);
            items.add(item);
        }
        this.setPages(items);
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
