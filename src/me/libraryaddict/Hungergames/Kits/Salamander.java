package me.libraryaddict.Hungergames.Kits;

import java.util.ArrayList;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Types.Extender;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Salamander extends Extender implements Listener {

    ArrayList<Player> salamanders = new ArrayList<Player>();

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (salamanders.contains(event.getEntity())) {
            if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK
                    || event.getCause() == DamageCause.LAVA) {
                event.setCancelled(true);
                event.getEntity().setFireTicks(0);
                /*
                 * Player p = (Player) event.getEntity(); p.damage(0); int hp =
                 * p.getHealth(); if (hp < 20) { Location l =
                 * event.getEntity().getLocation(); Block b = l.getBlock(); if
                 * (b.getType() == Material.STATIONARY_LAVA) hp += 4; else if
                 * (b.getType() == Material.LAVA) hp += 1; else return;
                 * b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND,
                 * Material.STATIONARY_LAVA.getId()); b.setType(Material.AIR);
                 * if (hp > 20) hp = 20; p.setHealth(hp); }
                 */
            }
        }
    }

    public Salamander() {
        for (Player p : Bukkit.getOnlinePlayers())
            if (kits.hasAbility(p, "Salamander")) {
                salamanders.add(p);
            }
    }

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        if (hg.doSeconds && hg.currentTime >= 120)
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
