package me.libraryaddict.Hungergames.Abilities;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Herobrine extends AbilityListener implements Disableable {
    private transient HashMap<Player, Long> cooldown = new HashMap<Player, Long>();
    public String cooldownMessage = ChatColor.BLUE + "You may not use that yet! Wait %s seconds!";
    public int cooldownTime = 120;
    private transient HashMap<Player, Long> damagers = new HashMap<Player, Long>();
    public String itemName = "Herobrines Escape";
    public String noOneToRunFrom = ChatColor.BLUE + "You have no one to run from!";
    public int secondsToRun = 3;

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Player damager = null;
        if (event.getEntity() instanceof Player && hasAbility((Player) event.getEntity())) {
            if (event.getDamager() instanceof Player)
                damager = (Player) event.getDamager();
            else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() != null
                    && ((Projectile) event.getDamager()).getShooter() instanceof Player)
                damager = (Player) ((Projectile) event.getDamager()).getShooter();
            if (damager != null) {
                damagers.put((Player) event.getEntity(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player p = event.getPlayer();
        if (hasAbility(p) && isSpecialItem(item, itemName) && damagers.containsKey(p)) {
            long lastUsed = 0;
            if (cooldown.containsKey(p))
                lastUsed = cooldown.get(p);
            if (lastUsed + (120000) > System.currentTimeMillis()) {
                p.sendMessage(String.format(cooldownMessage,
                        (((lastUsed + (cooldownTime * 1000)) - System.currentTimeMillis()) / 1000)));
            } else {
                if (damagers.get(p) + 30000 > System.currentTimeMillis()) {
                    cooldown.put(p, System.currentTimeMillis());
                    final Location begin = p.getLocation().clone();
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 4), true);
                    p.getWorld().playSound(begin, Sound.WITHER_SPAWN, 1, 0);
                    final String name = p.getName();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                        public void run() {
                            Gamer gamer = HungergamesApi.getPlayerManager().getGamer(name);
                            if (gamer != null && gamer.isAlive()) {
                                gamer.getPlayer().addPotionEffect(
                                        new PotionEffect(PotionEffectType.WEAKNESS, (int) (gamer.getPlayer().getLocation()
                                                .distance(begin) * 20), 1), true);
                            }
                        }
                    }, secondsToRun * 20);
                }
            }
        }
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        Player p = event.getKilled().getPlayer();
        cooldown.remove(p);
        damagers.remove(p);
    }
}
