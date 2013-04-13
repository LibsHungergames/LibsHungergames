package me.libraryaddict.Hungergames.Kits;

import me.libraryaddict.Hungergames.Types.Extender;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class Cultivator extends Extender implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (kits.hasAbility(event.getPlayer(), "Cultivator")) {
            Block b = event.getBlock();
            if (b.getType() == Material.SAPLING) {
                int data = (int) b.getData();
                b.setType(Material.AIR);
                boolean success;
                if (data == 1)
                    success = b.getWorld().generateTree(b.getLocation(), TreeType.REDWOOD);
                else if (data == 2)
                    success = b.getWorld().generateTree(b.getLocation(), TreeType.BIRCH);
                else if (data == 3)
                    success = b.getWorld().generateTree(b.getLocation(), TreeType.SMALL_JUNGLE);
                else
                    success = b.getWorld().generateTree(b.getLocation(), TreeType.TREE);
                if (!success)
                    b.setTypeIdAndData(Material.SAPLING.getId(), (byte) data, false);
            } else if (b.getType() == Material.CROPS)
                b.setData((byte) 7);
        }
    }
}
