package me.libraryaddict.Hungergames.Abilities;

import java.util.Collection;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class Doctor  extends AbilityListener {

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getItemInHand();
        if (event.getRightClicked() instanceof LivingEntity && item != null && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Pair Of Forceps")) {
            Collection<PotionEffect> effects = ((LivingEntity) event.getRightClicked()).getActivePotionEffects();
            for (PotionEffect effect : effects)
                ((LivingEntity) event.getRightClicked()).removePotionEffect(effect.getType());
        }
    }

}
