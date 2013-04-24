package me.libraryaddict.Hungergames.Abilities;


import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;

public class Berserker  extends AbilityListener {

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null && hasAbility(event.getEntity().getKiller())) {
            Player p = event.getEntity().getKiller();
            if (event.getEntity() instanceof Creature) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, 0), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30 * 20, 0), true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerKilledEvent event) {
        if (event.getKillerPlayer() != null && hasAbility(event.getKillerPlayer().getPlayer())) {
            Player p = event.getKillerPlayer().getPlayer();
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, 1), true);
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30 * 20, 1), true);
        }
    }

}
