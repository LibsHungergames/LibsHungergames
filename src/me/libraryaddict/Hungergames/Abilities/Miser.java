package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class Miser extends AbilityListener implements Disableable {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKilled(PlayerKilledEvent event) {
        if (hasAbility(event.getKilled().getPlayer().getName()))
            event.getDrops().clear();
    }

}
