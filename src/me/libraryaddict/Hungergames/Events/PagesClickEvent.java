package me.libraryaddict.Hungergames.Events;

import me.libraryaddict.Hungergames.Types.PageInventory;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PagesClickEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancelled;
    private InventoryClickEvent invEvent;
    private PageInventory inv;
    protected int slot;

    public PagesClickEvent(PageInventory inventory, int slot, InventoryClickEvent invEvent) {
        this.slot = slot;
        this.invEvent = invEvent;
        this.inv = inventory;
    }

    public InventoryClickEvent getEvent() {
        return invEvent;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public PageInventory getInventory() {
        return inv;
    }

    public ItemStack getItemStack() {
        if (slot >= 0)
            return inv.getItem(slot);
        return null;
    }

    public Player getPlayer() {
        return inv.getPlayer();
    }
}
