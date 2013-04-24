package me.libraryaddict.Hungergames.Abilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class Hunter extends AbilityListener {

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if ((event.getEntity() instanceof Chicken || event.getEntity() instanceof Cow || event.getEntity() instanceof Pig)
                && event.getEntity().getKiller() != null && hasAbility(event.getEntity().getKiller())) {
            Iterator<ItemStack> itel = event.getDrops().iterator();
            List<ItemStack> toAdd = new ArrayList<ItemStack>();
            while (itel.hasNext()) {
                ItemStack item = itel.next();
                if (item == null
                        || (item.getType() != Material.RAW_BEEF && item.getType() != Material.RAW_CHICKEN && item.getType() != Material.PORK))
                    continue;
                if (item.getType() == Material.RAW_CHICKEN)
                    toAdd.add(new ItemStack(Material.COOKED_CHICKEN, item.getAmount()));
                else if (item.getType() == Material.RAW_BEEF)
                    toAdd.add(new ItemStack(Material.COOKED_BEEF, item.getAmount()));
                else if (item.getType() == Material.PORK)
                    toAdd.add(new ItemStack(Material.GRILLED_PORK, item.getAmount()));
                itel.remove();
            }
            for (ItemStack item : toAdd)
                event.getDrops().add(item);
        }
    }
}
