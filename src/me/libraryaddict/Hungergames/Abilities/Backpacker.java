package me.libraryaddict.Hungergames.Abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Backpacker extends AbilityListener {

    private transient HashMap<Player, Inventory> backpack = new HashMap<Player, Inventory>();
    public int backpackInventoryRows = 6;
    public int backpackItem = Material.ENDER_CHEST.getId();
    public String backPackItemDescription = ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC
            + "Double click this chest to open your backpack!";
    public String backPackItemName = ChatColor.LIGHT_PURPLE + "Backpack";
    private transient HashMap<Player, Long> chestClick = new HashMap<Player, Long>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (isSpecialItem(event.getCurrentItem(), backPackItemName) && event.getCurrentItem().getTypeId() == backpackItem) {
            event.setCancelled(true);
            final Player p = (Player) event.getWhoClicked();
            if (chestClick.containsKey(p) && chestClick.get(p) > System.currentTimeMillis() - 1000) {
                // Open backpack
                chestClick.remove(p);
                Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                    @Override
                    public void run() {
                        p.closeInventory();
                        p.updateInventory();
                        p.openInventory(backpack.get(p));
                    }
                }, 5);
            } else
                chestClick.put(p, System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onDeath(PlayerKilledEvent event) {
        Player p = event.getKilled().getPlayer();
        chestClick.remove(event.getKilled().getPlayer());
        Iterator<ItemStack> itel = event.getDrops().iterator();
        while (itel.hasNext()) {
            ItemStack item = itel.next();
            if (isSpecialItem(item, backPackItemName) && item.getTypeId() == backpackItem) {
                itel.remove();
                break;
            }
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

    @Override
    public void registerPlayer(Player p) {
        super.registerPlayer(p);
        ItemStack item = new ItemStack(backpackItem);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(backPackItemName);
        List<String> lore = new ArrayList<String>();
        lore.add(backPackItemDescription);
        meta.setLore(lore);
        item.setItemMeta(meta);
        backpack.put(p, Bukkit.createInventory(null, backpackInventoryRows * 9, backPackItemName));
        p.getInventory().setItem(9, item);
    }
}
