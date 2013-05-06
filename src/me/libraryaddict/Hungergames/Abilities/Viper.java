package me.libraryaddict.Hungergames.Abilities;

import java.util.Random;

import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Viper extends AbilityListener {

    public int chance = 3;
    public int length = 5;
    public int multiplier = 1;

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            Player p = (Player) event.getDamager();
            if (hasAbility(p) && new Random().nextInt(chance) == 1) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, length * 20, multiplier), true);
            }
        }
    }

}
