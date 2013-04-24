package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Boxer extends AbilityListener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && hasAbility(((Player) event.getDamager()).getName())
                && event.getDamage() == 1)
            event.setDamage(4);
        if (event.getEntity() instanceof Player && hasAbility(((Player) event.getEntity()).getName()))
            if (event.getDamage() > 0)
                event.setDamage(event.getDamage() - 1);
    }
}
