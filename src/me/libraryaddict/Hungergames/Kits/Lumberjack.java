package me.libraryaddict.Hungergames.Kits;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Lumberjack implements Listener {

    private KitManager kits = HungergamesApi.getKitManager();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.LOG && kits.hasAbility(event.getPlayer(), "Lumberjack")) {
            Block b = event.getBlock().getRelative(BlockFace.UP);
            while (b.getType() == Material.LOG) {
                b.breakNaturally();
                b = b.getRelative(BlockFace.UP);
            }
        }
    }

}
