package me.libraryaddict.Hungergames.Kits;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.Enchants;
import me.libraryaddict.Hungergames.Types.Extender;

public class Gravedigger extends Extender implements Listener {
    BlockFace[] faces = new BlockFace[] { BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST };

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        if (event.getKillerPlayer() != null && kits.hasAbility(event.getKillerPlayer().getPlayer(), "Gravedigger")) {
            Block center = event.getDropsLocation().getBlock();
            for (BlockFace face : faces)
                if (center.getRelative(face).getType() == Material.AIR) {
                    center.getRelative(face).setType(Material.CHEST);
                    break;
                }
            center.setType(Material.CHEST);
            Inventory inv = ((InventoryHolder) center.getState()).getInventory();
            Iterator<ItemStack> itel = event.getDrops().iterator();
            while (itel.hasNext()) {
                ItemStack item = itel.next();
                if (item == null || item.getType() == Material.AIR || item.containsEnchantment(Enchants.UNLOOTABLE))
                    continue;
                if (kits.canFit(inv, new ItemStack[] { item }))
                    inv.addItem(item);
                else {
                    if (item.hasItemMeta())
                        event.getDropsLocation().getWorld().dropItemNaturally(event.getDropsLocation(), item.clone())
                                .getItemStack().setItemMeta(item.getItemMeta());
                    else
                        event.getDropsLocation().getWorld().dropItemNaturally(event.getDropsLocation(), item);
                }
            }
            event.getDrops().clear();
            center.getState().update();
        }
    }
}
