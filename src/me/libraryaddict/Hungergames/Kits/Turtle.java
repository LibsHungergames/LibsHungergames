package me.libraryaddict.Hungergames.Kits;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Turtle implements Listener {

    private KitManager kits = HungergamesApi.getKitManager();

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && kits.hasAbility((Player) event.getEntity(), "Turtle")) {
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
        if (event.getDamager() instanceof Player && kits.hasAbility((Player) event.getDamager(), "Turtle")) {
            Player p = (Player) event.getDamager();
            if (p.isSneaking()) {
                event.setCancelled(true);
            }
        }
    }

}
