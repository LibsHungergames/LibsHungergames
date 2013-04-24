package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Summoner extends AbilityListener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (damager instanceof Egg && ((Egg) damager).getShooter() instanceof Player && entity instanceof Creature
                && hasAbility((Player) ((Egg) damager).getShooter())) {
            entity.getWorld().dropItemNaturally(entity.getLocation().clone().add(0, 0.5, 0),
                    new ItemStack(Material.MONSTER_EGG, 1, entity.getType().getTypeId()));
            entity.remove();
            damager.remove();
            event.setCancelled(true);
        }
    }
}
