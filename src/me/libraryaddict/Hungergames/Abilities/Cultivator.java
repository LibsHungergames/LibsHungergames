package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class Cultivator extends AbilityListener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (hasAbility(event.getPlayer())) {
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
