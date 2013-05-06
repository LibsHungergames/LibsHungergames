package me.libraryaddict.Hungergames.Abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Pickpocket extends AbilityListener {

    class Pick {
        int itemsStolen = 0;
        long lastUsed = 0;
        Player pickpocket = null;
    }
    public String attemptedToPickpocketWhilePickpocketing = ChatColor.BLUE + "Cannot touch that!";
    public String attemptedToStealHotbar = ChatColor.BLUE + "Thats their hotbar!";
    public int cooldown = 30;
    public String cooldownThieving = ChatColor.BLUE + "You may not pickpocket again for %s seconds!";
    public int maxItems = 4;
    public String pickpocketedMax = ChatColor.BLUE + "You have pickpocketed your max!";
    private transient HashMap<ItemStack, Pick> pickpockets = new HashMap<ItemStack, Pick>();
    public boolean stealHotbar = false;
    public int thievingStickItemId = Material.BLAZE_ROD.getId();

    // Pickpocket. Multiple people can pickpocket at the time.
    // Store itemstack. Its used to measure the cooldown and items you can
    // click.
    // Lets see. On click, cooldown is now 30secs. And the taken items is now 4.

    public String thievingStickItemName = ChatColor.WHITE + "Thieving Stick";

    private void closedInv(Inventory inv, Player player) {
        List<Player> peverts = this.getPerverts(inv);
        if (peverts.contains(player)) {
            peverts.remove(player);
            // This guy is a pickpocket and he is done for some reason.. Who
            // cares.
            // Lets wipe his pick.
            if (peverts.size() == 0)
                ((Player) inv.getHolder()).removeMetadata("Picking", HungergamesApi.getHungergames());
            for (Pick pick : pickpockets.values()) {
                if (pick.pickpocket == player) {
                    pick.pickpocket = null;
                    pick.itemsStolen = 0;
                }
            }
        }
    }

    // If he is being pickpocketed. Then the thief is added to metadata on him.
    // The

    private List<Player> getPerverts(Inventory inv) {
        if (inv.getHolder() instanceof Player && ((Player) inv.getHolder()).hasMetadata("Picking"))
            return (List<Player>) ((Player) inv.getHolder()).getMetadata("Picking").get(0).value();
        return new ArrayList<Player>();
    }

    private Pick getPick(Player thief) {
        for (Pick pick : pickpockets.values())
            if (pick.pickpocket == thief)
                return pick;
        return null;
    }

    // He can pick when taken is there.
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getItemInHand();
        if (event.getRightClicked() instanceof Player && isSpecialItem(item, thievingStickItemName)
                && item.getTypeId() == thievingStickItemId) {
            Pick pick = new Pick();
            if (pickpockets.containsKey(item))
                pick = pickpockets.get(item);
            if (pick.lastUsed > System.currentTimeMillis()) {
                event.getPlayer().sendMessage(
                        String.format(cooldownThieving, +(-((System.currentTimeMillis() - pick.lastUsed) / 1000))));
            } else {
                pickpockets.put(item, pick);
                pick.lastUsed = System.currentTimeMillis() + (cooldown * 1000);
                pick.pickpocket = event.getPlayer();
                List<Player> pickers = new ArrayList<Player>();
                if (event.getRightClicked().hasMetadata("Picking"))
                    pickers = (List<Player>) event.getRightClicked().getMetadata("Picking").get(0).value();
                pickers.add(event.getPlayer());
                event.getRightClicked().setMetadata("Picking", new FixedMetadataValue(HungergamesApi.getHungergames(), pickers));
                event.getPlayer().openInventory(((Player) event.getRightClicked()).getInventory());
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && pickpockets.containsKey(event.getCurrentItem())
                && pickpockets.get(event.getCurrentItem()).pickpocket != null) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).sendMessage(attemptedToPickpocketWhilePickpocketing);
            ((Player) event.getWhoClicked()).updateInventory();
        }
        List<Player> peverts = this.getPerverts(event.getInventory());
        if (peverts.contains(event.getWhoClicked())) {
            if (event.getRawSlot() < 36) {
                Pick pick = getPick((Player) event.getWhoClicked());
                if (pick.itemsStolen < maxItems) {
                    if (stealHotbar || event.getRawSlot() > 8) {
                        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR)
                            pick.itemsStolen++;
                    } else {
                        event.setCancelled(true);
                        ((Player) event.getWhoClicked()).sendMessage(attemptedToStealHotbar);
                        ((Player) event.getWhoClicked()).updateInventory();
                    }
                } else {
                    event.setCancelled(true);
                    ((Player) event.getWhoClicked()).sendMessage(pickpocketedMax);
                    ((Player) event.getWhoClicked()).updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        closedInv(event.getInventory(), (Player) event.getPlayer());
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        for (HumanEntity entity : event.getKilled().getPlayer().getInventory().getViewers())
            closedInv(event.getKilled().getPlayer().getInventory(), (Player) entity);
        closedInv(event.getKilled().getPlayer().getInventory(), event.getKilled().getPlayer());
    }

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
}
