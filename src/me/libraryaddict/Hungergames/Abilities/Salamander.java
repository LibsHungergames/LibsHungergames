package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Salamander extends AbilityListener implements Disableable {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (getMyPlayers().contains(event.getEntity())) {
            if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK
                    || event.getCause() == DamageCause.LAVA) {
                event.setCancelled(true);
                event.getEntity().setFireTicks(0);
            }
        }
    }

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        if (HungergamesApi.getHungergames().currentTime >= HungergamesApi.getConfigManager().getMainConfig()
                .getTimeForInvincibility())
            for (Player p : getMyPlayers()) {
                Material type = p.getLocation().getBlock().getType();
                if (type == Material.WATER || type == Material.STATIONARY_WATER) {
                    p.damage(1);
                }
            }
    }

}
