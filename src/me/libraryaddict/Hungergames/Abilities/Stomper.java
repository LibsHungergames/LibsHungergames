package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.Vector;

public class Stomper extends AbilityListener implements Disableable {
    public boolean changeStomperFallDamage = true;
    public boolean reduceStompDamageByDistance = true;
    public String stompedMessage = "%1$2s was stomped by %2$2s";
    public int stomperFallDamage = 4;
    public boolean stomperBlocksFall = true;

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled())
            return;
        if (event.getCause() == DamageCause.FALL && event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (hasAbility(p)) {
                int dmg = event.getDamage();
                int area = dmg / 3;
                if (area > 3)
                    area = 3;
                event.setCancelled(true);
                if (changeStomperFallDamage && stomperFallDamage > 0)
                    p.damage(stomperFallDamage);
                Location center = p.getLocation();
                if (stomperBlocksFall)
                    for (int x = -area; x <= area; x++) {
                        for (int z = -area; z <= area; z++) {
                            Block b = new Location(center.getWorld(), center.getX() + x, center.getBlockY() - 1, center.getZ()
                                    + z).getBlock();
                            if (b.getType() == Material.AIR || b.isLiquid() || b.getLocation().distance(center) > area
                                    || b.getState() instanceof InventoryHolder)
                                continue;
                            int id = b.getTypeId();
                            int data = b.getData();
                            b.setTypeIdAndData(0, (byte) 0, false);
                            FallingBlock falling = b.getWorld().spawnFallingBlock(b.getLocation(), id, (byte) data);
                            falling.setVelocity(new Vector(0, 0.2, 0));
                            falling.setDropItem(false);
                        }
                    }
                for (Entity entity : p.getNearbyEntities(area * 2, area, area * 2))
                    if (entity instanceof LivingEntity) {
                        int hisDmg = dmg;
                        if (reduceStompDamageByDistance)
                            hisDmg = (int) (dmg / (entity.getLocation().distance(center) + 1));
                        if (entity.getLocation().getBlockY() - 1 > center.getBlockY())
                            continue;
                        if (entity instanceof Player) {
                            if (((Player) entity).isSneaking())
                                hisDmg /= 2;
                            Gamer gamer = HungergamesApi.getPlayerManager().getGamer(entity);
                            if (!gamer.isAlive())
                                continue;
                        }
                        Vector unitVector = entity.getLocation().toVector().subtract(center.toVector()).normalize();
                        if (stomperBlocksFall)
                            entity.setVelocity(unitVector.multiply(0.4).add(new Vector(0, 0.4, 0)));
                        if (hisDmg >= ((LivingEntity) entity).getHealth() && entity instanceof Player) {
                            Gamer gamer = HungergamesApi.getPlayerManager().getGamer(entity);
                            if (gamer.isAlive())
                                HungergamesApi.getPlayerManager().killPlayer(gamer, p, entity.getLocation(),
                                        gamer.getInventory(), String.format(stompedMessage, gamer.getName(), p.getName()));
                        } else
                            ((LivingEntity) entity).damage(hisDmg);
                    }
                if (stomperBlocksFall)
                    p.setVelocity(p.getVelocity().add(new Vector(0, 0.4, 0)));
            }
        }
    }
}
