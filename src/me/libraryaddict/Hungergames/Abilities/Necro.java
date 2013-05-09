package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Necro extends AbilityListener implements Disableable {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if ((event.getEntity() instanceof Skeleton || event.getEntity() instanceof Zombie)
                && event.getDamager() instanceof Player && hasAbility((Player) event.getDamager()))
            event.setDamage(999);
    }
}
