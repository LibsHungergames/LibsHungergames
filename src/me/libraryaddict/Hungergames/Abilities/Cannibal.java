package me.libraryaddict.Hungergames.Abilities;

import java.util.Random;

import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Cannibal extends AbilityListener {
    public int chance = 3;
    public int addHunger = 2;
    public int potionLength = 5;
    public int multiplier = 0;

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            Player p = (Player) event.getDamager();
            if (hasAbility(p) && new Random().nextInt(chance) == 0) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, potionLength * 20, multiplier), true);
                int hunger = p.getFoodLevel();
                hunger += addHunger;
                if (hunger > 20)
                    hunger = 20;
                p.setFoodLevel(hunger);
            }
        }
    }
}
