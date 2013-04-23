package me.libraryaddict.Hungergames.Abilities;

import java.util.HashMap;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Jumper extends AbilityListener {
    private transient HashMap<Block, Integer> platformTaskIds = new HashMap<Block, Integer>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof EnderPearl
                && event.getEntity() == ((Projectile) event.getDamager()).getShooter()
                && hasThisAbility((Player) event.getEntity())) {
            event.setCancelled(true);
            for (int x = -2; x < 3; x++) {
                for (int z = -2; z < 3; z++) {
                    final Block b = event.getEntity().getLocation().clone().add(x, -1, z).getBlock();
                    if (platformTaskIds.containsKey(b))
                        Bukkit.getScheduler().cancelTask(platformTaskIds.remove(b));
                    if (b.getType() == Material.AIR)
                        platformTaskIds.put(b, Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                            public void run() {
                                b.setType(Material.AIR);
                                platformTaskIds.remove(platformTaskIds.get(b));
                            }
                        }, 5 * 20));
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (platformTaskIds.containsKey(event.getBlock()))
            Bukkit.getScheduler().cancelTask(platformTaskIds.remove(event.getBlock()));
    }
}
