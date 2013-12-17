package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.libraryaddict.Hungergames.Listeners.LibsFeastManager;
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
    private KitManager kits = HungergamesApi.getKitManager();
    private PlayerManager pm = HungergamesApi.getPlayerManager();

    public SpectateInventory(Player player) {
        super(player, true);
        title = tm.getSpectatorInventoryTitle();
        ItemStack item = HungergamesApi.getConfigManager().getMainConfig().getSpectatorItemBack();
        backAPage = HungergamesApi.getInventoryManager().generateItem(item.getType(), item.getDurability(),
                tm.getItemSpectatorInventoryBackName(), tm.getItemSpectatorInventoryBackDescription());
        backAPage.setAmount(0);
        item = HungergamesApi.getConfigManager().getMainConfig().getSpectatorItemForwards();
        forwardsAPage = HungergamesApi.getInventoryManager().generateItem(item.getType(), item.getDurability(),
                tm.getItemSpectatorInventoryForwardsName(), tm.getItemSpectatorInventoryForwardsDescription());
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
                    if (item.getItemMeta().getDisplayName().equals(tm.getSpectatorInventoryFeastName())) {
                        event.getWhoClicked().teleport(
                                LibsFeastManager.getFeastManager().getFeastLocation().getWorld()
                                        .getHighestBlockAt(LibsFeastManager.getFeastManager().getFeastLocation()).getLocation()
                                        .clone().add(0.5, 1, 0.5));
                    } else {
                        Gamer toTeleport = pm.getGamer(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        if (toTeleport != null) {
                            event.getWhoClicked().teleport(toTeleport.getPlayer());
                        }
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
        if (LibsFeastManager.getFeastManager().getFeastLocation().getY() > 0) {
            names.add("Feast");
        }
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        ArrayList<ItemStack> heads = new ArrayList<ItemStack>();
        for (String name : names) {
            if (name.equals("Feast")) {
                ItemStack head = new ItemStack(Material.CAKE, 0);
                ItemMeta meta = head.getItemMeta();
                meta.setDisplayName(tm.getSpectatorInventoryFeastName());
                meta.setLore(Arrays.asList(tm.getSpectatorInventoryFeastDescription().split("\n")));
                head.setItemMeta(meta);
                heads.add(head);
            } else {
                Gamer gamer = pm.getGamer(name);
                ItemStack head = new ItemStack(Material.SKULL_ITEM, 0, (short) 3);
                Kit kit = kits.getKitByPlayer(gamer.getPlayer());
                if (kit != null && kit.getIcon() != null)
                    head = kit.getIcon();
                head.setAmount(0);
                ItemMeta meta = head.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + name);
                List<String> lore = new ArrayList<String>();
                lore.add(String.format(tm.getSpectatorHeadKills(), gamer.getKills()));
                lore.add(String.format(tm.getSpectatorHeadKit(),
                        (kit == null ? tm.getMessagePlayerShowKitsNoKit() : kit.getName())));
                meta.setLore(lore);
                head.setItemMeta(meta);
                heads.add(head);
            }
        }
        setPages(heads);
    }
}
