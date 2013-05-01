package me.libraryaddict.Hungergames.Events;

import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerWinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Gamer winner;

    public PlayerWinEvent(Gamer winner) {
        this.winner = winner;
    }

    public Gamer getWinner() {
        return winner;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}