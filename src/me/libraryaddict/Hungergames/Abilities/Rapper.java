package me.libraryaddict.Hungergames.Abilities;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Rapper extends AbilityListener implements Disableable {
    public int nauseaLength = 5;
    public int hearingRange = 16;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (hasAbility(event.getPlayer())) {
            if (event.getAction().name().contains("RIGHT")) {
                if (event.getClickedBlock().getType() == Material.NOTE_BLOCK) {
                    for (Entity entity : event.getPlayer().getNearbyEntities(hearingRange, hearingRange, hearingRange)) {
                        if (!(entity instanceof Player) || HungergamesApi.getPlayerManager().getGamer(entity).isAlive()) {
                            ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,
                                    nauseaLength * 20, 0), true);
                        }
                    }
                    ((LivingEntity) event.getPlayer()).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,
                            nauseaLength * 20, 0), true);
                }
            }
        }
    }

}
