package me.libraryaddict.Hungergames.Kits;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Monk implements Listener {

    private HashMap<ItemStack, Long> monkStaff = new HashMap<ItemStack, Long>();

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getItemInHand();
        if (event.getRightClicked() instanceof Player && item != null && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().startsWith("" + ChatColor.WHITE)
                && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("Monk Staff")) {
            long lastUsed = 0;
            if (monkStaff.containsKey(item))
                lastUsed = monkStaff.get(item);
            if (lastUsed + 15000 > System.currentTimeMillis()) {
                event.getPlayer().sendMessage(
                        ChatColor.BLUE + "You may monk them again in "
                                + (-((System.currentTimeMillis() - (lastUsed + 15000)) / 1000)) + " seconds!");
            } else {
                PlayerInventory inv = ((Player) event.getRightClicked()).getInventory();
                int slot = new Random().nextInt(36);
                ItemStack replaced = inv.getItemInHand();
                if (replaced == null)
                    replaced = new ItemStack(0);
                ItemStack replacer = inv.getItem(slot);
                if (replacer == null)
                    replacer = new ItemStack(0);
                inv.setItemInHand(replacer);
                inv.setItem(slot, replaced);
                event.getPlayer().sendMessage(ChatColor.BLUE + "Monked!");
            }
        }
    }

}
