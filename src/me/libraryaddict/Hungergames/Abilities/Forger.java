package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class Forger extends AbilityListener {



    @EventHandler
    public void onIntentoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR
                && hasThisAbility((Player) event.getWhoClicked())) {
            int coal = 0;
            int remove = 0;
            for (ItemStack item : event.getInventory().getContents()) {
                if (item != null && item.getType() == Material.COAL)
                    coal += item.getAmount();
            }
            if (event.getCurrentItem().getType() == Material.COAL) {
                for (ItemStack item : event.getInventory().getContents()) {
                    if (item != null && item.getType().name().contains("ORE")) {
                        while (item.getAmount() > 0 && remove < coal) {
                            item.setAmount(item.getAmount() - 1);
                            remove++;
                            if (item.getType() == Material.IRON_ORE)
                                HungergamesApi.getKitManager().addItem((Player) event.getWhoClicked(), new ItemStack(Material.IRON_INGOT));
                            else if (item.getType() == Material.GOLD_ORE)
                                HungergamesApi.getKitManager().addItem((Player) event.getWhoClicked(), new ItemStack(Material.GOLD_INGOT));
                        }
                        if (item.getAmount() == 0)
                            item.setType(Material.AIR);
                    }
                }
            } else if (event.getCurrentItem().getType().name().contains("ORE")) {
                ItemStack item = event.getCurrentItem();
                while (item.getAmount() > 0 && remove < coal) {
                    item.setAmount(item.getAmount() - 1);
                    remove++;
                    if (item.getType() == Material.IRON_ORE)
                        HungergamesApi.getKitManager().addItem((Player) event.getWhoClicked(), new ItemStack(Material.IRON_INGOT));
                    else if (item.getType() == Material.GOLD_ORE)
                        HungergamesApi.getKitManager().addItem((Player) event.getWhoClicked(), new ItemStack(Material.GOLD_INGOT));
                }
                if (item.getAmount() == 0)
                    item.setType(Material.AIR);
            }
            for (ItemStack item : event.getInventory().getContents()) {
                if (item != null && item.getType() == Material.COAL) {
                    while (remove > 0 && item.getAmount() > 0) {
                        item.setAmount(item.getAmount() - 1);
                        remove--;
                    }
                    if (item.getAmount() == 0)
                        item.setType(Material.AIR);
                }
            }
        }
    }
}
