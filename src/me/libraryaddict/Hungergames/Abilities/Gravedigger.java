package me.libraryaddict.Hungergames.Abilities;

import java.util.Iterator;

import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Managers.EnchantmentManager;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Gravedigger extends AbilityListener implements Disableable {
    private transient BlockFace[] faces = new BlockFace[] { BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST };

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        if (event.getKillerPlayer() != null && hasAbility(event.getKillerPlayer().getPlayer())) {
            Block center = event.getDropsLocation().getBlock();
            if (event.getDrops().size() > 54)
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
                if (item == null || item.getType() == Material.AIR || item.containsEnchantment(EnchantmentManager.UNLOOTABLE))
                    continue;
                if (HungergamesApi.getKitManager().canFit(inv, new ItemStack[] { item }))
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
