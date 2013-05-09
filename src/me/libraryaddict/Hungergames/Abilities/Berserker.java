package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;

public class Berserker extends AbilityListener implements Disableable {
    public int berserkerLength = 30;
    public boolean giveConfusion = true;
    public int killAnimalMultiplier = 0;
    public int killPlayerMultiplier = 1;

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null && hasAbility(event.getEntity().getKiller())) {
            Player p = event.getEntity().getKiller();
            if (event.getEntity() instanceof Creature) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, berserkerLength * 20, killAnimalMultiplier),
                        true);
                if (giveConfusion)
                    p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, berserkerLength * 20, 0), true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerKilledEvent event) {
        if (event.getKillerPlayer() != null && hasAbility(event.getKillerPlayer().getPlayer())) {
            Player p = event.getKillerPlayer().getPlayer();
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, berserkerLength * 20, killPlayerMultiplier),
                    true);
            if (giveConfusion)
                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, berserkerLength * 20, 1), true);
        }
    }

}
