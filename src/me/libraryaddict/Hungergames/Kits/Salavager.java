package me.libraryaddict.Hungergames.Kits;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Salavager implements Listener {

    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private KitManager kits = HungergamesApi.getKitManager();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ANVIL
                && pm.getGamer(event.getPlayer()).isAlive() && kits.hasAbility(event.getPlayer(), "Salavager")) {
            ItemStack item = event.getItem();
            if (item != null && item.getType() != Material.AIR) {
                for (Recipe recipe : Bukkit.getRecipesFor(item)) {
                    if (recipe instanceof ShapelessRecipe) {
                        event.setCancelled(true);
                        item.setAmount(0);
                        item.setDurability((short) 0);
                        item.setType(Material.AIR);
                        for (ItemStack items : ((ShapelessRecipe) recipe).getIngredientList())
                            kits.addItem(event.getPlayer(), items.clone());
                        break;
                    }
                    if (recipe instanceof ShapedRecipe) {
                        event.setCancelled(true);
                        item.setAmount(0);
                        item.setDurability((short) 0);
                        item.setType(Material.AIR);
                        for (ItemStack items : ((ShapedRecipe) recipe).getIngredientMap().values())
                            kits.addItem(event.getPlayer(), items.clone());
                        break;
                    }
                }
            }
        }
    }

}
