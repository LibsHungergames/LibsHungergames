package me.libraryaddict.Hungergames.Kits;


import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Berserker  implements Listener {
    private KitManager kits = HungergamesApi.getKitManager();

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null && kits.hasAbility(event.getEntity().getKiller(), "Berserker")) {
            Player p = event.getEntity().getKiller();
            if (event.getEntity() instanceof Creature) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, 0), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30 * 20, 0), true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerKilledEvent event) {
        if (event.getKillerPlayer() != null && kits.hasAbility(event.getKillerPlayer().getPlayer(), "Berserker")) {
            Player p = event.getKillerPlayer().getPlayer();
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, 1), true);
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30 * 20, 1), true);
        }
    }

}
