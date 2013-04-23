package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class Demoman extends AbilityListener {


    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.STONE_PLATE && hasThisAbility(event.getPlayer())) {
            event.getBlock().removeMetadata("Placer", HungergamesApi.getHungergames());
            event.getBlock().setMetadata("Placer", new FixedMetadataValue(HungergamesApi.getHungergames(), event.getPlayer().getName()));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.isCancelled())
            return;
        Block b = event.getClickedBlock();
        if (event.getAction() == Action.PHYSICAL && b != null && b.hasMetadata("Placer") && b.getType() == Material.STONE_PLATE
                && b.getRelative(BlockFace.DOWN).getType() == Material.GRAVEL) {
            b.removeMetadata("Placer", HungergamesApi.getHungergames());
            b.setType(Material.AIR);
            b.getWorld().createExplosion(b.getLocation().clone().add(0.5, 0.5, 0.5), 4F);
        }
    }
}
