package me.libraryaddict.Hungergames.Types;

import java.lang.reflect.Field;

import me.libraryaddict.Hungergames.Configs.TranslationConfig;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ClickInventory implements Listener {
    protected TranslationConfig tm = HungergamesApi.getConfigManager().getTranslationsConfig();
    protected static JavaPlugin plugin = HungergamesApi.getHungergames();
    protected Inventory currentInventory;
    protected boolean inventoryInUse;
    private boolean modifiable;
    private Player player;

    public ClickInventory(Player player) {
        this.player = player;
    }

    public void closeInventory() {
        closeInventory(true);
    }

    private void closeInventory(boolean force) {
        inventoryInUse = false;
        if (getPlayer().hasMetadata(getClass().getSimpleName())
                && getPlayer().getMetadata(getClass().getSimpleName()).get(0).value() == this) {
            getPlayer().removeMetadata(getClass().getSimpleName(), plugin);
        }
        HandlerList.unregisterAll(this);
        if (force && getPlayer().getOpenInventory() != null
                && getPlayer().getOpenInventory().getTopInventory().getViewers().equals(currentInventory.getViewers())) {
            getPlayer().closeInventory();
        }
    }

    /**
     * Gets the item in a slot. Returns null if no item or if item is null
     */
    public ItemStack getItem(int slot) {
        if (currentInventory != null && currentInventory.getSize() > slot)
            return currentInventory.getItem(slot);
        return null;
    }

    /**
     * Gets the player using this
     */
    public Player getPlayer() {
        return player;
    }

    public boolean isInventoryInUse() {
        return this.inventoryInUse;
    }

    public boolean isModifiable() {
        return modifiable;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() == getPlayer() && isInventoryInUse()) {
            closeInventory(false);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!this.isModifiable() && event.getWhoClicked() == getPlayer()) {
            for (int slot : event.getRawSlots()) {
                if (slot < currentInventory.getSize()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer() == getPlayer()) {
            closeInventory(false);
        }
    }

    /**
     * Internal method to open the inventory or switch them
     */
    protected void openInv() {
        /**
         * If ever getting bugs with opening a inventory and items glitch and no itemclickevent fires. Make sure you cancel the
         * click event you used to get this..
         */
        boolean isSwitchingInventory = isInventoryInUse();
        inventoryInUse = false;
        ItemStack heldItem = null;
        if (isSwitchingInventory) {
            heldItem = getPlayer().getItemOnCursor();
            getPlayer().setItemOnCursor(new ItemStack(Material.AIR));
        }
        try {
            Object player = getPlayer().getClass().getDeclaredMethod("getHandle").invoke(getPlayer());
            Class c = Class.forName(player.getClass().getName().replace("Player", "Human"));
            Object defaultContainer = c.getField("defaultContainer").get(player);
            Field activeContainer = c.getField("activeContainer");
            if (activeContainer.get(player) == defaultContainer) {
                getPlayer().openInventory(currentInventory);
            } else {
                // Do this so that other inventories know their time is over.
                Class.forName("org.bukkit.craftbukkit." + c.getName().split("\\.")[3] + ".event.CraftEventFactory")
                        .getMethod("handleInventoryCloseEvent", c).invoke(null, player);
                activeContainer.set(player, defaultContainer);
                getPlayer().openInventory(currentInventory);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!isSwitchingInventory) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            getPlayer().setMetadata(getClass().getSimpleName(), new FixedMetadataValue(plugin, this));
        } else {
            if (heldItem != null && heldItem.getType() != Material.AIR) {
                getPlayer().setItemOnCursor(heldItem);
                getPlayer().updateInventory();
            }
        }
        inventoryInUse = true;
    }

    /* @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getPlayer() == getPlayer()) {
            closeInventory(false);
        }
    }*/

    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }

    public abstract void setTitle(String newTitle);

}
