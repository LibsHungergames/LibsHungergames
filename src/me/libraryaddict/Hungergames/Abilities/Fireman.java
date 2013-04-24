package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Fireman extends AbilityListener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player
                && hasAbility((Player) event.getEntity())
                && (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK
                        || event.getCause() == DamageCause.LIGHTNING || event.getCause() == DamageCause.LAVA)) {
            event.setCancelled(true);
            event.getEntity().setFireTicks(0);
        }
    }

}
