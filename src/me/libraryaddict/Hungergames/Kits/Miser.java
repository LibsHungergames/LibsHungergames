package me.libraryaddict.Hungergames.Kits;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.Extender;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class Miser extends Extender implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKilled(PlayerKilledEvent event) {
        if (kits.hasAbility(event.getKilled().getPlayer(), "Miser"))
            event.getDrops().clear();
    }

}
