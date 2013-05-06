package me.libraryaddict.Hungergames.Events;

import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTrackEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList() {
        return handlers;
    }
    private boolean cancelled = false;
    private String message;
    private Gamer tracker;

    private Player victim;

    public PlayerTrackEvent(Gamer tracker, Player victim, String trackMessage) {
        this.tracker = tracker;
        this.victim = victim;
        setMessage(trackMessage);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public String getMessage() {
        return message;
    }

    public Gamer getTracker() {
        return tracker;
    }

    public Player getVictim() {
        return victim;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        cancelled = isCancelled;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}