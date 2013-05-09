package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Beastmaster extends AbilityListener implements Disableable {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = p.getItemInHand();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && item != null && item.getType() == Material.MONSTER_EGG) {
            if (hasAbility(event.getPlayer()) && item.getDurability() == (byte) 95) {
                event.setCancelled(true);
                ((Wolf) p.getWorld().spawnEntity(event.getClickedBlock().getRelative(event.getBlockFace()).getLocation(),
                        EntityType.WOLF)).setOwner(p);
                if (item.getAmount() > 1)
                    item.setAmount(item.getAmount() - 1);
                else
                    item = null;
                p.setItemInHand(item);
            }
        }
    }

    @EventHandler
    public void onMobInteract(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        ItemStack item = p.getItemInHand();
        if (event.getRightClicked() instanceof Wolf && item != null && item.getType() == Material.BONE) {
            if (hasAbility(event.getPlayer()) && !((Wolf) event.getRightClicked()).isTamed()) {
                event.setCancelled(true);
                ((Wolf) event.getRightClicked()).setOwner(p);
                if (item.getAmount() > 1)
                    item.setAmount(item.getAmount() - 1);
                else
                    item = null;
                p.setItemInHand(item);
            }
        }
    }

}
