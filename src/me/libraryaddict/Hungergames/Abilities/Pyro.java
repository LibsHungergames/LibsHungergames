package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Pyro extends AbilityListener implements Disableable {
    public float fireballExplosiveMultiplier = 2;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction() == Action.RIGHT_CLICK_AIR && item != null && item.getType() == Material.FIREBALL
                && hasAbility(event.getPlayer())) {
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() == 0)
                event.getPlayer().setItemInHand(new ItemStack(0));
            Fireball ball = event.getPlayer().launchProjectile(Fireball.class);
            ball.setIsIncendiary(true);
            ball.setYield(ball.getYield() * fireballExplosiveMultiplier);
        }
    }

}
