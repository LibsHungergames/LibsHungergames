package me.libraryaddict.Hungergames.Kits;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.libraryaddict.Hungergames.Types.Extender;

public class Jumper extends Extender implements Listener {
    HashMap<Block, Integer> platforms = new HashMap<Block, Integer>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof EnderPearl
                && event.getEntity() == ((Projectile) event.getDamager()).getShooter()
                && kits.hasAbility((Player) event.getEntity(), "Jumper")) {
            event.setCancelled(true);
            for (int x = -2; x < 3; x++) {
                for (int z = -2; z < 3; z++) {
                    final Block b = event.getEntity().getLocation().clone().add(x, -1, z).getBlock();
                    if (platforms.containsKey(b))
                        Bukkit.getScheduler().cancelTask(platforms.remove(b));
                    if (b.getType() == Material.AIR)
                        platforms.put(b, Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
                            public void run() {
                                b.setType(Material.AIR);
                                platforms.remove(platforms.get(b));
                            }
                        }, 5 * 20));
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (platforms.containsKey(event.getBlock()))
            Bukkit.getScheduler().cancelTask(platforms.remove(event.getBlock()));
    }
}
