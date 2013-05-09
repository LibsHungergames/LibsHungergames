package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Boxer extends AbilityListener implements Disableable {
    public int boxerDamage = 4;
    public boolean reduceDamage = true;

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && hasAbility(((Player) event.getDamager()).getName()) && event.getDamage() == 1)
            event.setDamage(boxerDamage);
        if (reduceDamage && event.getEntity() instanceof Player && hasAbility(((Player) event.getEntity()).getName())
                && event.getDamage() > 0)
            event.setDamage(event.getDamage() - 1);
    }
}
