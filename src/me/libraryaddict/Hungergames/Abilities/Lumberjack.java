package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class Lumberjack extends AbilityListener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.LOG && hasAbility(event.getPlayer())) {
            Block b = event.getBlock().getRelative(BlockFace.UP);
            while (b.getType() == Material.LOG) {
                b.breakNaturally();
                b = b.getRelative(BlockFace.UP);
            }
        }
    }

}
