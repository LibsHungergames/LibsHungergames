package me.libraryaddict.Hungergames.Events;

import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTrackEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Gamer tracker;
    private Player victim;
    private String message;
    private boolean cancelled = false;

    public PlayerTrackEvent(Gamer tracker, Player victim, String trackMessage) {
        this.tracker = tracker;
        this.victim = victim;
        setMessage(trackMessage);
    }

    public Player getVictim() {
        return victim;
    }

    public Gamer getTracker() {
        return tracker;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        cancelled = isCancelled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}