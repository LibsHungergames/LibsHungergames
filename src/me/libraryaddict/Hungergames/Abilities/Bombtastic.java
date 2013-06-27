package me.libraryaddict.Hungergames.Abilities;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;

public class Bombtastic extends AbilityListener implements Disableable {
    public int maxTntDrop = 3;
    public int minTntDrop = 1;
    public int oneChanceInWhatOfDropping = 2;

    @EventHandler
    public void onKilled(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.CREEPER && event.getEntity().getKiller() != null
                && hasAbility(event.getEntity().getKiller())) {
            if (new Random().nextInt(oneChanceInWhatOfDropping) == 0)
                event.getDrops().add(
                        new ItemStack(Material.TNT, new Random().nextInt((maxTntDrop - minTntDrop) + 1) + minTntDrop));
        }
    }
}
