package me.libraryaddict.Hungergames.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Pickpocket implements Listener {
    // TODO Rewrite this

    private Hungergames hg = HungergamesApi.getHungergames();

    HashMap<ItemStack, Pick> pickpockets = new HashMap<ItemStack, Pick>();

    // Pickpocket. Multiple people can pickpocket at the time.
    // Store itemstack. Its used to measure the cooldown and items you can
    // click.
    // Lets see. On click, cooldown is now 30secs. And the taken items is now 4.

    class Pick {
        Player pickpocket = null;
        int itemsStolen = 0;
        long lastUsed = 0;
    }

    // He can pick when taken is there.
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getItemInHand();
        if (event.getRightClicked() instanceof Player && item != null && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().startsWith(ChatColor.WHITE + "")
                && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("Thieving Stick")) {
            Pick pick = new Pick();
            if (pickpockets.containsKey(item))
                pick = pickpockets.get(item);
            if (pick.lastUsed > System.currentTimeMillis()) {
                event.getPlayer().sendMessage(
                        ChatColor.BLUE + "You may not pickpocket again for "
                                + (-((System.currentTimeMillis() - pick.lastUsed) / 1000)) + " seconds!");
            } else {
                pickpockets.put(item, pick);
                pick.lastUsed = System.currentTimeMillis() + 30000;
                pick.pickpocket = event.getPlayer();
                List<Player> pickers = new ArrayList<Player>();
                if (event.getRightClicked().hasMetadata("Picking"))
                    pickers = (List<Player>) event.getRightClicked().getMetadata("Picking").get(0).value();
                pickers.add(event.getPlayer());
                event.getRightClicked().setMetadata("Picking", new FixedMetadataValue(hg, pickers));
                event.getPlayer().openInventory(((Player) event.getRightClicked()).getInventory());
            }
        }
    }

    // If he is being pickpocketed. Then the thief is added to metadata on him.
    // The

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().hasMetadata("Picking")) {
            List<Player> pickers = (List<Player>) event.getPlayer().getMetadata("Picking").get(0).value();
            List<Player> cloned = new ArrayList<Player>();
            for (Player p : pickers)
                cloned.add(p);
            for (Player picker : cloned)
                if (event.getTo().distance(picker.getLocation()) > 6) {
                    picker.closeInventory();
                }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        closedInv(event.getInventory(), (Player) event.getPlayer());
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && pickpockets.containsKey(event.getCurrentItem())
                && pickpockets.get(event.getCurrentItem()).pickpocket != null) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).sendMessage(ChatColor.BLUE + "You cannot touch that!");
            ((Player) event.getWhoClicked()).updateInventory();
        }
        List<Player> peverts = this.getPerverts(event.getInventory());
        if (peverts.contains(event.getWhoClicked())) {
            if (event.getRawSlot() < 36) {
                Pick pick = getPick((Player) event.getWhoClicked());
                if (pick.itemsStolen < 4) {
                    if (event.getRawSlot() > 8) {
                        pick.itemsStolen++;
                    } else {
                        event.setCancelled(true);
                        ((Player) event.getWhoClicked()).sendMessage(ChatColor.BLUE + "Thats their hotbar!");
                        ((Player) event.getWhoClicked()).updateInventory();
                    }
                } else {
                    event.setCancelled(true);
                    ((Player) event.getWhoClicked()).sendMessage(ChatColor.BLUE + "You have pickpocketed your max!");
                    ((Player) event.getWhoClicked()).updateInventory();
                }
            }
        }
    }

    private void closedInv(Inventory inv, Player player) {
        List<Player> peverts = this.getPerverts(inv);
        if (peverts.contains(player)) {
            peverts.remove(player);
            // This guy is a pickpocket and he is done for some reason.. Who
            // cares.
            // Lets wipe his pick.
            if (peverts.size() == 0)
                ((Player) inv.getHolder()).removeMetadata("Picking", hg);
            for (Pick pick : pickpockets.values()) {
                if (pick.pickpocket == player) {
                    pick.pickpocket = null;
                    pick.itemsStolen = 0;
                }
            }
        }
    }

    private Pick getPick(Player thief) {
        for (Pick pick : pickpockets.values())
            if (pick.pickpocket == thief)
                return pick;
        return null;
    }

    private List<Player> getPerverts(Inventory inv) {
        if (inv.getHolder() instanceof Player && ((Player) inv.getHolder()).hasMetadata("Picking"))
            return (List<Player>) ((Player) inv.getHolder()).getMetadata("Picking").get(0).value();
        return new ArrayList<Player>();
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        for (HumanEntity entity : event.getKilled().getPlayer().getInventory().getViewers())
            closedInv(event.getKilled().getPlayer().getInventory(), (Player) entity);
        closedInv(event.getKilled().getPlayer().getInventory(), event.getKilled().getPlayer());
    }
}
