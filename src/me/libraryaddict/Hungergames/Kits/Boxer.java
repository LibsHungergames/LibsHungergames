package me.libraryaddict.Hungergames.Kits;

import me.libraryaddict.Hungergames.Types.Extender;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Boxer extends Extender implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player
                && kits.hasAbility((Player) event.getDamager(), "Boxer")
                && (((Player) event.getDamager()).getItemInHand() == null || ((Player) event.getDamager()).getItemInHand()
                        .getType() == Material.AIR))
            event.setDamage(4);
        if (event.getEntity() instanceof Player && kits.hasAbility((Player) event.getEntity(), "Boxer"))
            if (event.getDamage() > 0)
                event.setDamage(event.getDamage() - 1);
    }
}
