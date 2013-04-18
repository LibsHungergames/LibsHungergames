package me.libraryaddict.Hungergames.Listeners;

import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.LibsCommands.PrivateMessageEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LibsCommandsListener implements Listener {

    /**
     * This is simply code for my own use. Removes the ability for spectators to
     * private message alive players.
     * 
     * Such as revealing someone else is about to attack in a sneak ambush. And
     * ruining the game for the ambusher
     */

    PlayerManager pm = HungergamesApi.getPlayerManager();

    @EventHandler
    public void onMessage(PrivateMessageEvent event) {
        Gamer gamer = pm.getGamer(event.getSender().getName());
        if (gamer != null && !event.getSender().isOp() && !gamer.isAlive()) {
            Gamer g = pm.getGamer(event.getReceiver().getName());
            if (g != null && !event.getReceiver().isOp() && g.isAlive())
                event.setCancelled(true);
        }
    }
}
