package me.libraryaddict.Hungergames.Kits;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Pyro implements Listener {
    private KitManager kits = HungergamesApi.getKitManager();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction() == Action.RIGHT_CLICK_AIR && item != null && item.getType() == Material.FIREBALL
                && kits.hasAbility(event.getPlayer(), "Pyro")) {
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() == 0)
                item.setType(Material.AIR);
            Fireball ball = event.getPlayer().launchProjectile(Fireball.class);
            ball.setIsIncendiary(true);
            ball.setYield(ball.getYield() * 2);
        }
    }

}
