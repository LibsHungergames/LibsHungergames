package me.libraryaddict.Hungergames.Abilities;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;

public class Butcher extends AbilityListener implements Disableable {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && hasAbility((Player) event.getDamager())) {
            if (event.getEntity() instanceof Creature) {
                Creature entity = (Creature) event.getEntity();
                if (entity.getHealth() > 1) {
                    event.setDamage(0);
                    entity.setHealth(1);
                }

            }
        }
    }

}
