package me.libraryaddict.Hungergames.Kits;

import java.util.Random;

import me.libraryaddict.Hungergames.Types.Extender;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Snail extends Extender implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            Player p = (Player) event.getDamager();
            if (p.getItemInHand() != null && p.getItemInHand().getType().name().contains("SWORD")
                    && kits.hasAbility(p, "Snail") && new Random().nextInt(3) == 1) {
                /*
                 * int ticks =
                 * p.getItemInHand().getEnchantmentLevel(Enchants.POISON) * 60;
                 * for (PotionEffect effect : entity.getActivePotionEffects()) {
                 * if (effect.getType().equals(PotionEffectType.POISON)) { ticks
                 * += effect.getDuration();
                 * p.removePotionEffect(PotionEffectType.POISON); break; } }
                 */
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1), true);
            }
        }
    }

}
