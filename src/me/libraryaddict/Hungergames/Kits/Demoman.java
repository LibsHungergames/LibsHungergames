package me.libraryaddict.Hungergames.Kits;

import me.libraryaddict.Hungergames.Types.Extender;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class Demoman extends Extender implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.STONE_PLATE && kits.hasAbility(event.getPlayer(), "Demoman")) {
            event.getBlock().removeMetadata("Placer", hg);
            event.getBlock().setMetadata("Placer", new FixedMetadataValue(hg, event.getPlayer().getName()));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.isCancelled())
            return;
        Block b = event.getClickedBlock();
        if (event.getAction() == Action.PHYSICAL && b != null && b.hasMetadata("Placer") && b.getType() == Material.STONE_PLATE
                && b.getRelative(BlockFace.DOWN).getType() == Material.GRAVEL) {
            b.removeMetadata("Placer", hg);
            b.setType(Material.AIR);
            b.getWorld().createExplosion(b.getLocation().clone().add(0.5, 0.5, 0.5), 4F);
        }
    }
}
