package me.libraryaddict.Hungergames.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerShutdownEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    
    public void setCancelled(boolean state) {
        cancelled = state;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
}
