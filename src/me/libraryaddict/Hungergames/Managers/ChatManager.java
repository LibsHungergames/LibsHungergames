package me.libraryaddict.Hungergames.Managers;

import java.util.HashMap;
import java.util.Set;

import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Events.PrivateMessageEvent;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

public class ChatManager {

    private HashMap<String, String> lastMsg = new HashMap<String, String>();
    private TranslationConfig tm = HungergamesApi.getConfigManager().getTranslationsConfig();

    public CommandSender getOtherChatter(String player) {
        if (lastMsg.containsKey(player))
            return getSender(lastMsg.get(player));
        return null;
    }

    public CommandSender getSender(String name) {
        Set<Permissible> permissibles = Bukkit.getPluginManager().getPermissionSubscriptions("ThisIsUsedForMessaging");
        for (Permissible permissible : permissibles) {
            if (permissible instanceof CommandSender) {
                CommandSender user = (CommandSender) permissible;
                if (user.getName().equals(name))
                    return user;
            }
        }
        return null;
    }

    public boolean hasOtherChatter(String player) {
        return lastMsg.containsKey(player);
    }

    public void removeChatter(String chatter) {
        lastMsg.remove(chatter);
    }

    public void sendMessage(CommandSender sender, CommandSender receiver, String message) {
        PrivateMessageEvent event = new PrivateMessageEvent(sender, receiver, message, false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            event.getSender().sendMessage(String.format(tm.getCommandMessageEventCancelled(), event.getReceiverDisplayName()));
        } else {
            event.getSender().sendMessage(
                    String.format(tm.getCommandMessageSendMessage(), event.getReceiverDisplayName(), event.getMessage()));
            event.getReceiver().sendMessage(
                    String.format(tm.getCommandMessageReceiveMessage(), event.getSenderDisplayName(), event.getMessage()));
            setChatter(event.getSender().getName(), event.getReceiver().getName());
            setChatter(event.getReceiver().getName(), event.getSender().getName());
        }
    }

    public void sendReply(CommandSender sender, CommandSender receiver, String message) {
        PrivateMessageEvent event = new PrivateMessageEvent(sender, receiver, message, true);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            event.getSender().sendMessage(String.format(tm.getCommandReplyEventCancelled(), event.getReceiverDisplayName()));
        } else {
            event.getSender().sendMessage(
                    String.format(tm.getCommandReplySendReply(), event.getReceiverDisplayName(), event.getMessage()));
            event.getReceiver().sendMessage(
                    String.format(tm.getCommandReplyReceiveReply(), event.getSenderDisplayName(), event.getMessage()));
            setChatter(event.getSender().getName(), event.getReceiver().getName());
            setChatter(event.getReceiver().getName(), event.getSender().getName());
        }
    }

    public void setChatter(String chatter, String receiver) {
        lastMsg.put(chatter, receiver);
    }
}
