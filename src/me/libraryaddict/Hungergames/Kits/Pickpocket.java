package me.libraryaddict.Hungergames.Kits;

import java.util.HashMap;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
    
    private KitManager kits = HungergamesApi.getKitManager();
    private Hungergames hg = HungergamesApi.getHungergames();

    class Pick {
        Player victim;
        Inventory picking;
        int taken;
        long lastPicked;
    }

    public Pickpocket() {
        for (Player p : Bukkit.getOnlinePlayers())
            if (kits.hasAbility(p, "Pickpocket")) {
                pickpockets.put(p, new Pick());
            }
    }

    HashMap<Player, Pick> pickpockets = new HashMap<Player, Pick>();

    // He can pick when taken is there.
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getItemInHand();
        if (event.getRightClicked() instanceof Player && item != null && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Thieving Stick")
                && pickpockets.containsKey(event.getPlayer())) {
            if (pickpockets.get(event.getPlayer()).lastPicked > (System.currentTimeMillis() / 1000)) {
                event.getPlayer().sendMessage(ChatColor.BLUE + "You may not pickpocket again at this time");
            } else if (event.getRightClicked().hasMetadata("Picking")) {
                event.getPlayer().sendMessage(ChatColor.BLUE + "This player is already being pickpocketed!");
            } else {
                event.getRightClicked().setMetadata("Picking", new FixedMetadataValue(hg, event.getPlayer()));
                pickpockets.get(event.getPlayer()).taken = 0;
                pickpockets.get(event.getPlayer()).victim = (Player) event.getRightClicked();
                pickpockets.get(event.getPlayer()).picking = ((Player) event.getRightClicked()).getInventory();
                event.getPlayer().openInventory(pickpockets.get(event.getPlayer()).picking);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().hasMetadata("Picking")) {
            Player picker = (Player) event.getPlayer().getMetadata("Picking").get(0).value();
            if (event.getTo().distance(picker.getLocation()) > 6) {
                picker.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (pickpockets.containsKey(event.getPlayer())) {
            Pick pick = pickpockets.get(event.getPlayer());
            if (pick.victim != null) {
                pick.victim.removeMetadata("Picking", hg);
                pick.lastPicked = (System.currentTimeMillis() / 1000) + 10;
                pick.victim = null;
                pick.picking = null;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryClickEvent event) {
        if (pickpockets.containsKey(event.getWhoClicked())) {
            Pick pick = pickpockets.get(event.getWhoClicked());
            if (pick.victim != null) {
                if (event.getRawSlot() < 36) {
                    if (pick.taken < 4) {
                        if (event.getRawSlot() > 8) {
                            pick.taken++;
                        } else if (event.getRawSlot() < 9) {
                            event.setCancelled(true);
                            ((Player) event.getWhoClicked()).sendMessage(ChatColor.BLUE + "Thats their hotbar!");
                        }
                    } else {
                        event.setCancelled(true);
                        ((Player) event.getWhoClicked()).sendMessage(ChatColor.BLUE
                                + "You have pickpocketed your max for this player!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        if (pickpockets.containsKey(event.getKilled().getPlayer())
                && pickpockets.get(event.getKilled().getPlayer()).victim != null) {
            InventoryCloseEvent invEvent = new InventoryCloseEvent(event.getKilled().getPlayer().getOpenInventory());
            onInventoryClose(invEvent);
            pickpockets.remove(event.getKilled().getPlayer());
        }
        for (HumanEntity human : event.getKilled().getPlayer().getInventory().getViewers())
            human.closeInventory();
    }
}
