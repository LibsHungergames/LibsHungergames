package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Managers.TranslationManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpectateInventory extends PageInventory {
    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private KitManager kits = HungergamesApi.getKitManager();

    public SpectateInventory(Player player) {
        super(player, true);
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
                    // Do whatever
                }
            }
        }
    }

    public void setHeads() {
        ArrayList<String> names = new ArrayList<String>();
        for (Gamer gamer : pm.getGamers()) {
            if (gamer.isAlive())
                names.add(gamer.getName());
        }
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        Iterator<String> itel = names.iterator();
        for (int i = 0; i < 27; i++) {
            if (i >= 21 && i <= 23)
                continue;
            if (itel.hasNext()) {
                String name = itel.next();
                Gamer gamer = pm.getGamer(name);
                ItemStack head = new ItemStack(Material.SKULL_ITEM, 0, (short) 3);
                ItemMeta meta = head.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + name);
                List<String> lore = new ArrayList<String>();
                lore.add(ChatColor.GREEN + "Kills:" + ChatColor.BLUE + gamer.getKills() + gamer.getKills());
                lore.add(ChatColor.GREEN
                        + "Kit: "
                        + ChatColor.BLUE
                        + (kits.getKitByPlayer(gamer.getPlayer()) == null ? tm.getMessagePlayerShowKitsNoKit() : kits
                                .getKitByPlayer(gamer.getPlayer()).getName()));
                meta.setLore(lore);
                head.setItemMeta(meta);
                inv.setItem(i, head);
            } else {
                ItemStack item = inv.getItem(i);
                if (item != null && item.getType() != Material.AIR)
                    inv.setItem(i, new ItemStack(0));
            }
        }
    }
}
