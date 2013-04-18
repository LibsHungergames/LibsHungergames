package me.libraryaddict.Hungergames.Kits;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

public class Werewolf implements Listener {

    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private KitManager kits = HungergamesApi.getKitManager();
    private Hungergames hg = HungergamesApi.getHungergames();

    public Werewolf() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(hg, new Runnable() {
            public void run() {
                for (Gamer gamer : pm.getAliveGamers()) {
                    Player p = gamer.getPlayer();
                    if (kits.hasAbility(p, "Werewolf")) {
                        if (hg.world.getTime() > 0 && hg.world.getTime() <= 12000) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 12000, 0), true);
                        } else {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 12000, 0), true);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12000, 0), true);
                        }
                    }
                }
            }
        }, 0, 12000);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && kits.hasAbility((Player) event.getTarget(), "Werewolf")
                && event.getEntityType() == EntityType.WOLF)
            event.setCancelled(true);
    }

}
