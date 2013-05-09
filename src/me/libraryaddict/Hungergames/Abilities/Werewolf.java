package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

public class Werewolf extends AbilityListener implements Disableable {
    public boolean giveWeakness = true;
    public int speedMultiplier = 0;
    public int strengthMultiplier = 0;

    @EventHandler
    public void gameStartEvent(GameStartEvent event) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HungergamesApi.getHungergames(), new Runnable() {
            public void run() {
                for (Gamer gamer : HungergamesApi.getPlayerManager().getAliveGamers()) {
                    Player p = gamer.getPlayer();
                    if (hasAbility(p)) {
                        if (HungergamesApi.getHungergames().world.getTime() > 0
                                && HungergamesApi.getHungergames().world.getTime() <= 12000) {
                            if (giveWeakness)
                                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 12000, 0), true);
                        } else {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 12000, strengthMultiplier), true);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12000, speedMultiplier), true);
                        }
                    }
                }
            }
        }, 0, 12000);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && hasAbility((Player) event.getTarget())
                && event.getEntityType() == EntityType.WOLF)
            event.setCancelled(true);
    }

}
