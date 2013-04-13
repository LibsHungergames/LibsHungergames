package me.libraryaddict.Hungergames.Listeners;

import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.LibsCommands.PrivateMessageEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LibsCommandsListener extends Extender implements Listener {
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
