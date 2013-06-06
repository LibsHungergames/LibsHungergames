package me.libraryaddict.Hungergames.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}