package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;

public class Poseidon extends AbilityListener implements Disableable {
    public int potionMultiplier = 1;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (getMyPlayers().contains(event.getPlayer()) && event.getPlayer().getLocation().getBlock().isLiquid())
            event.getPlayer().setRemainingAir(200);
    }

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        for (Player p : getMyPlayers()) {
            if (p.getLocation().getBlock().isLiquid()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, potionMultiplier), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, potionMultiplier), true);
            }
        }
    }

}
