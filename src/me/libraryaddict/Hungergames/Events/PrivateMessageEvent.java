package me.libraryaddict.Hungergames.Events;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PrivateMessageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private String message;
    private CommandSender sender;
    private CommandSender receiver;
    private String displayNameSender;
    private String displayNameReceiver;
    private boolean replying;

    public PrivateMessageEvent(CommandSender sender, CommandSender receiver, String message, boolean reply) {
        this.sender = sender;
        this.receiver = receiver;
        Player pSender = (sender instanceof Player ? (Player) sender : Bukkit.getPlayerExact(sender.getName()));
        Player pReciever = (receiver instanceof Player ? (Player) receiver : Bukkit.getPlayerExact(receiver.getName()));
        if (pSender != null)
            displayNameSender = pSender.getDisplayName();
        else
            displayNameSender = sender.getName();
        if (pReciever != null)
            displayNameReceiver = pReciever.getDisplayName();
        else
            displayNameReceiver = receiver.getName();
        this.message = message;
        this.replying = reply;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public String getSenderDisplayName() {
        return displayNameSender;
    }

    public String getReceiverDisplayName() {
        return displayNameReceiver;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean setCancelled) {
        cancelled = setCancelled;
    }

    public void setMessage(String newMessage) {
        message = newMessage;
    }

    public String getMessage() {
        return message;
    }

    public CommandSender getSender() {
        return sender;
    }

    public CommandSender getReceiver() {
        return receiver;
    }

    public boolean isReply() {
        return replying;
    }

}
