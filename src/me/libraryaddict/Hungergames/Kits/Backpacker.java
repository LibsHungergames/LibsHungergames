package me.libraryaddict.Hungergames.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.Extender;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Backpacker extends Extender implements Listener {

    HashMap<Player, Inventory> backpack = new HashMap<Player, Inventory>();
    HashMap<Player, Long> chestClick = new HashMap<Player, Long>();

    public Backpacker() {
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Backpack");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Double click this chest to open your backpack!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        for (Player p : Bukkit.getOnlinePlayers())
            if (kits.hasAbility(p, "Backpacker")) {
                backpack.put(p, Bukkit.createInventory(p, 54, "Backpack"));
                p.getInventory().setItem(9, item);
            }
    }

    private boolean isBackpack(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Backpack");
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (isBackpack(event.getCurrentItem())) {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            if (chestClick.containsKey(p) && chestClick.get(p) > System.currentTimeMillis() - 1000) {
                // Open backpack
                chestClick.remove(p);
                p.openInventory(backpack.get(p));
            } else
                chestClick.put(p, System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onDeath(PlayerKilledEvent event) {
        Player p = event.getKilled().getPlayer();
        chestClick.remove(event.getKilled().getPlayer());
        Iterator<ItemStack> itel = event.getDrops().iterator();
        while (itel.hasNext())
            if (isBackpack(itel.next())) {
                itel.remove();
                break;
            }
        if (backpack.containsKey(p)) {
            for (ItemStack item : backpack.remove(p)) {
                if (item == null || item.getType() == Material.AIR)
                    continue;
                else if (item.hasItemMeta())
                    p.getWorld().dropItemNaturally(event.getDropsLocation(), item.clone()).getItemStack()
                            .setItemMeta(item.getItemMeta());
                else
                    p.getWorld().dropItemNaturally(event.getDropsLocation(), item);
            }
        }
    }
}
