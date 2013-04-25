package me.libraryaddict.Hungergames.Abilities;

import java.util.Collection;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class Doctor extends AbilityListener {
    public boolean heal = false;
    public int toHeal = 5;

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getItemInHand();
        if (event.getRightClicked() instanceof LivingEntity && item != null && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Pair Of Forceps")) {
            LivingEntity lEntity = (LivingEntity) event.getRightClicked();
            Collection<PotionEffect> effects = lEntity.getActivePotionEffects();
            for (PotionEffect effect : effects)
                lEntity.removePotionEffect(effect.getType());
            if (heal) {
                int health = lEntity.getHealth();
                health += toHeal;
                if (health > lEntity.getMaxHealth())
                    health = lEntity.getMaxHealth();
                lEntity.setHealth(health);
            }
        }
    }

}
