package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
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
        title = ChatColor.DARK_GRAY + "Alive gamers";
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
                    Gamer toTeleport = pm.getGamer(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                    if (toTeleport != null) {
                        event.getWhoClicked().teleport(toTeleport.getPlayer());
                    }
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
        ArrayList<ItemStack> heads = new ArrayList<ItemStack>();
        for (String name : names) {
            Gamer gamer = pm.getGamer(name);
            ItemStack head = new ItemStack(Material.SKULL_ITEM, 0, (short) new Random().nextInt(4));
            ItemMeta meta = head.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + name);
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GREEN + "Kills: " + ChatColor.BLUE + gamer.getKills());
            lore.add(ChatColor.GREEN
                    + "Kit: "
                    + ChatColor.BLUE
                    + (kits.getKitByPlayer(gamer.getPlayer()) == null ? tm.getMessagePlayerShowKitsNoKit() : kits.getKitByPlayer(
                            gamer.getPlayer()).getName()));
            meta.setLore(lore);
            head.setItemMeta(meta);
            heads.add(head);
        }
        setPages(heads);
    }
}
