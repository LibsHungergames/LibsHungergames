package me.libraryaddict.Hungergames.Kits;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.server.v1_5_R2.EntityLiving;
import net.minecraft.server.v1_5_R2.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_5_R2.PathfinderGoalSelector;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.FollowOwner;
import me.libraryaddict.Hungergames.Types.OwnerAttacked;
import me.libraryaddict.Hungergames.Types.OwnerAttacks;

public class Hades implements Listener {
    HashMap<Zombie, Player> tamed = new HashMap<Zombie, Player>();

    private KitManager kits = HungergamesApi.getKitManager();

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        tamed.remove(event.getEntity());
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        Iterator<Zombie> itel = tamed.keySet().iterator();
        while (itel.hasNext()) {
            LivingEntity entity = tamed.get(itel.next());
            if (tamed.get(entity) == event.getKilled().getPlayer()) {
                itel.remove();
                entity.remove();
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (tamed.containsKey(event.getEntity()) && tamed.get(event.getEntity()) == event.getTarget()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (kits.hasAbility(event.getPlayer(), "Hades") && event.getRightClicked() instanceof Zombie) {
            ItemStack item = event.getPlayer().getItemInHand();
            if (item != null && item.getType() == Material.ROTTEN_FLESH && !tamed.containsKey(event.getRightClicked())) {
                item.setAmount(item.getAmount() - 1);
                if (item.getAmount() == 0)
                    item.setType(Material.AIR);
                Zombie old = (Zombie) event.getRightClicked();
                Zombie monster = (Zombie) old.getWorld().spawnEntity(old.getLocation(), old.getType());
                monster.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                monster.getEquipment().setHelmetDropChance(0F);
                monster.setHealth(old.getHealth());
                monster.setFireTicks(old.getFireTicks());
                monster.setFallDistance(old.getFallDistance());
                monster.setVelocity(old.getVelocity());
                for (PotionEffect effect : old.getActivePotionEffects())
                    monster.addPotionEffect(effect);
                old.remove();
                old.setTicksLived(Integer.MAX_VALUE);
                old.setHealth(0);
                tamed.put(monster, event.getPlayer());
                makeSlave(monster, event.getPlayer());
                monster.setTarget(null);
            }
        }
    }

    private void makeSlave(LivingEntity entity, Player tamer) {
        EntityLiving eIG = ((CraftLivingEntity) entity).getHandle();
        FollowOwner navig = new FollowOwner(eIG, 0.3F, 10.0F, 2.0F, ((CraftPlayer) tamer).getHandle());
        try {
            Field field = EntityLiving.class.getDeclaredField("targetSelector");
            field.setAccessible(true);
            PathfinderGoalSelector targetSelector = (PathfinderGoalSelector) field.get(eIG);
            Field targeta = PathfinderGoalSelector.class.getDeclaredField("a");
            targeta.setAccessible(true);
            ((List) targeta.get(targetSelector)).clear();
            targetSelector.a(4, new PathfinderGoalMeleeAttack(eIG, 0.3F, true));
            targetSelector.a(5, navig);
            field = EntityLiving.class.getDeclaredField("goalSelector");
            field.setAccessible(true);
            targetSelector = (PathfinderGoalSelector) field.get(eIG);
            targeta = PathfinderGoalSelector.class.getDeclaredField("a");
            targeta.setAccessible(true);
            ((List) targeta.get(targetSelector)).clear();
            targetSelector.a(3, new OwnerAttacks(eIG, ((CraftPlayer) tamer).getHandle()));
            targetSelector.a(3, new OwnerAttacked(eIG, ((CraftPlayer) tamer).getHandle()));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

}
