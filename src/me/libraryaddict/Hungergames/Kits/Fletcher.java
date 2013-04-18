package me.libraryaddict.Hungergames.Kits;

import java.util.Iterator;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class Fletcher implements Listener {
    private KitManager kits = HungergamesApi.getKitManager();

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Chicken && event.getEntity().getKiller() != null
                && kits.hasAbility(event.getEntity().getKiller(), "Fletcher")) {
            Iterator<ItemStack> itel = event.getDrops().iterator();
            while (itel.hasNext()) {
                ItemStack item = itel.next();
                if (item == null || item.getType() != Material.FEATHER)
                    continue;
                itel.remove();
            }
            event.getDrops().add(new ItemStack(Material.FEATHER, 2));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (kits.hasAbility(event.getPlayer(), "Fletcher") && event.getBlock().getType() == Material.GRAVEL) {
            event.getBlock().setType(Material.AIR);
            event.getBlock().getWorld()
                    .dropItemNaturally(event.getBlock().getLocation().add(0.5, 0, 0.5), new ItemStack(Material.FLINT));
        }
    }
}
