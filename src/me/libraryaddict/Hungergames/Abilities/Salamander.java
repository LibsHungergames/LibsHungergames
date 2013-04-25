package me.libraryaddict.Hungergames.Abilities;

import java.util.ArrayList;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Salamander extends AbilityListener {

    private transient ArrayList<Player> salamanders = new ArrayList<Player>();

    public void registerPlayer(String name) {
        Player p = Bukkit.getPlayerExact(name);
        if (p != null)
            salamanders.add(p);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (salamanders.contains(event.getEntity())) {
            if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK
                    || event.getCause() == DamageCause.LAVA) {
                event.setCancelled(true);
                event.getEntity().setFireTicks(0);
            }
        }
    }

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        if (HungergamesApi.getHungergames().doSeconds && HungergamesApi.getHungergames().currentTime >= 120)
            for (Player p : salamanders) {
                if (p.getLocation().getBlock().getType() == Material.STATIONARY_WATER
                        || p.getLocation().getBlock().getType() == Material.WATER) {
                    p.damage(1);
                }
            }
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        salamanders.remove(event.getKilled().getPlayer());
    }

}
