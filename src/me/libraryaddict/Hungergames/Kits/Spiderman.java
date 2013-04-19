package me.libraryaddict.Hungergames.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Spiderman implements Listener {
    private KitManager kits = HungergamesApi.getKitManager();
    private Hungergames hg = HungergamesApi.getHungergames();
    private HashMap<String, ArrayList<Long>> cooldown = new HashMap<String, ArrayList<Long>>();

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntityType() == EntityType.SNOWBALL) {
            if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player) {
                Player p = (Player) event.getEntity().getShooter();
                if (!kits.hasAbility(p, "Spiderman"))
                    return;
                ArrayList<Long> cooldowns;
                if (cooldown.containsKey(p.getName()))
                    cooldowns = cooldown.get(p.getName());
                else {
                    cooldowns = new ArrayList<Long>();
                    cooldown.put(p.getName(), cooldowns);
                }
                if (cooldowns.size() == 3) {
                    if (cooldowns.get(0) >= System.currentTimeMillis()) {
                        event.setCancelled(true);
                        p.updateInventory();
                        p.sendMessage(ChatColor.BLUE + "Your web shooters havn't refilled yet! Wait "
                                + (((cooldowns.get(0) - System.currentTimeMillis()) / 1000) + 1) + " seconds!");
                        kits.addItem(p, new ItemStack(Material.SNOW_BALL));
                        return;
                    }
                    cooldowns.remove(0);
                }
                event.getEntity().setMetadata("Spiderball", new FixedMetadataValue(hg, "Spiderball"));
                cooldowns.add(System.currentTimeMillis() + 30000);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().hasMetadata("Spiderball")) {
            Location loc = event.getEntity().getLocation();
            int x = new Random().nextInt(2) - 1;
            int z = new Random().nextInt(2) - 1;
            for (int y = 0; y < 2; y++)
                for (int xx = 0; xx < 2; xx++)
                    for (int zz = 0; zz < 2; zz++) {
                        Block b = loc.clone().add(x + xx, y, z + zz).getBlock();
                        if (b.getType() == Material.AIR)
                            b.setType(Material.WEB);
                    }

            event.getEntity().remove();
        }
    }

    private boolean isWeb(Location loc) {
        if (loc.getBlock().getType() == Material.WEB)
            return true;
        return loc.clone().add(0, 1, 0).getBlock().getType() == Material.WEB;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (kits.hasAbility(event.getPlayer(), "Spiderman")) {
            if (isWeb(event.getFrom()) || isWeb(event.getTo())) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1), true);
            }
        }
    }
}
