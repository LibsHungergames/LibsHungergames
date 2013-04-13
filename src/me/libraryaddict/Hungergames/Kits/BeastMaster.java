package me.libraryaddict.Hungergames.Kits;

import me.libraryaddict.Hungergames.Types.Extender;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BeastMaster extends Extender implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = p.getItemInHand();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && item != null && item.getType() == Material.MONSTER_EGG) {
            if (kits.hasAbility(event.getPlayer(), "BeastMaster") && item.getDurability() == (byte) 95) {
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
            if (kits.hasAbility(event.getPlayer(), "BeastMaster") && !((Wolf) event.getRightClicked()).isTamed()) {
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
