package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class Turtle extends AbilityListener {



    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && hasThisAbility((Player) event.getEntity())) {
            Player p = (Player) event.getEntity();
            if (p.isSneaking() && p.isBlocking() && p.getHealth() > 1) {
                event.setCancelled(true);
                p.damage(0);
                p.setHealth(p.getHealth() - 1);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && hasThisAbility((Player) event.getDamager())) {
            Player p = (Player) event.getDamager();
            if (p.isSneaking()) {
                event.setCancelled(true);
            }
        }
    }

}
