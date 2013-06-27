package me.libraryaddict.Hungergames.Abilities;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Herobrine extends AbilityListener implements Disableable {
    private transient HashMap<String, Long> cooldown = new HashMap<String, Long>();
    public int cooldownTime = 120;
    public String cooldownMessage = ChatColor.BLUE + "You may not use that yet! Wait %s seconds!";
    private transient HashMap<String, Long> damagers = new HashMap<String, Long>();

    private int getArmorValue(ItemStack armor) {
        if (armor == null || armor.getType() == Material.AIR)
            return 0;
        Material mat = armor.getType();
        if (mat == Material.LEATHER_HELMET || mat == Material.LEATHER_BOOTS || mat == Material.GOLD_BOOTS
                || mat == Material.CHAINMAIL_BOOTS)
            return 1;
        if (mat == Material.LEATHER_LEGGINGS || mat == Material.GOLD_HELMET || mat == Material.CHAINMAIL_HELMET
                || mat == Material.IRON_HELMET || mat == Material.IRON_BOOTS)
            return 2;
        if (mat == Material.LEATHER_CHESTPLATE || mat == Material.GOLD_LEGGINGS || mat == Material.DIAMOND_BOOTS
                || mat == Material.DIAMOND_HELMET)
            return 3;
        if (mat == Material.CHAINMAIL_LEGGINGS)
            return 4;
        if (mat == Material.GOLD_CHESTPLATE || mat == Material.CHAINMAIL_CHESTPLATE || mat == Material.IRON_LEGGINGS)
            return 5;
        if (mat == Material.IRON_LEGGINGS || mat == Material.DIAMOND_LEGGINGS)
            return 6;
        if (mat == Material.DIAMOND_CHESTPLATE)
            return 8;
        return 0;
    }

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
                PlayerInventory dInv = damager.getInventory();
                int armorValue1 = 0;
                for (ItemStack item : dInv.getArmorContents())
                    armorValue1 += getArmorValue(item);
                PlayerInventory inv = ((Player) event.getEntity()).getInventory();
                int armorValue2 = 0;
                for (ItemStack item : inv.getArmorContents())
                    armorValue2 += getArmorValue(item);
                if (armorValue1 > armorValue2)
                    damagers.put(((Player) event.getEntity()).getName(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player p = event.getPlayer();
        if (hasAbility(p) && item != null && item.getType() == Material.NETHER_STAR && damagers.containsKey(p.getName())) {
            long lastUsed = 0;
            if (cooldown.containsKey(p.getName()))
                lastUsed = cooldown.get(p.getName());
            if (lastUsed + (120000) > System.currentTimeMillis()) {
                p.sendMessage(String.format(cooldownMessage,
                        (((lastUsed + (cooldownTime * 1000)) - System.currentTimeMillis()) / 1000)));
            } else {
                if (damagers.get(p.getName()) + 30000 > System.currentTimeMillis()) {
                    cooldown.put(p.getName(), System.currentTimeMillis());
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
                    }, 60);
                }
            }
        }
    }
}
