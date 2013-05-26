package me.libraryaddict.Hungergames.Abilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class libraryaddict extends AbilityListener {
    public String bookName = "Explosive Reading";

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction().name().contains("RIGHT") && isSpecialItem(item, bookName) && item.getType() == Material.BOOK) {
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() == 0)
                event.getPlayer().setItemInHand(new ItemStack(0));
            final Item explodingBook = event.getPlayer().getWorld()
                    .dropItem(event.getPlayer().getEyeLocation(), new ItemStack(Material.BOOK));
            explodingBook.setVelocity(event.getPlayer().getEyeLocation().getDirection().multiply(2));
            explodingBook.setPickupDelay(99999);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                public void run() {
                    explodingBook.getWorld().createExplosion(explodingBook.getLocation(), 3);
                    explodingBook.remove();
                }
            }, 20 * 3);
        }
    }

}
