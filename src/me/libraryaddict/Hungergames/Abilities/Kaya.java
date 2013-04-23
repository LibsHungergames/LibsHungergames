package me.libraryaddict.Hungergames.Abilities;

import java.util.HashMap;
import java.util.Iterator;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

public class Kaya extends AbilityListener {

    private transient HashMap<Block, Player> kayaBlocks = new HashMap<Block, Player>();

    public Kaya() {
        ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.GRASS));
        recipe.addIngredient(Material.DIRT);
        recipe.addIngredient(Material.SEEDS);
        Bukkit.addRecipe(recipe);
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe().getResult() != null && event.getRecipe().getResult().getType() == Material.GRASS) {
            for (HumanEntity entity : event.getViewers())
                if (hasThisAbility((Player) entity))
                    return;
            event.getInventory().setItem(0, new ItemStack(0, 0));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!event.isCancelled())
            kayaBlocks.remove(event.getBlock());
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        for (Block b : event.blockList())
            kayaBlocks.remove(b);
    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
        for (Block b : event.getBlocks())
            kayaBlocks.remove(b);
    }

    @EventHandler
    public void onDeath(PlayerKilledEvent event) {
        if (!hasThisAbility(event.getKilled().getPlayer()))
            return;
        Iterator<Block> itel = kayaBlocks.keySet().iterator();
        while (itel.hasNext()) {
            Block b = itel.next();
            if (kayaBlocks.get(b) == event.getKilled().getPlayer())
                itel.remove();
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!event.isCancelled())
            if (event.getBlock().getType() == Material.GRASS && hasThisAbility(event.getPlayer())) {
                kayaBlocks.put(event.getBlock(), event.getPlayer());
            }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Gamer gamer = HungergamesApi.getPlayerManager().getGamer(event.getPlayer());
        if (gamer.isAlive()) {
            Location loc = event.getPlayer().getLocation();
            for (int z = -3; z <= 3; z++) {
                for (int x = -3; x <= 3; x++) {
                    for (int y = -2; y <= 1; y++) {
                        Block block = loc.clone().add(x, y, z).getBlock();
                        if (kayaBlocks.containsKey(block) && block.getType() == Material.GRASS) {
                            if (kayaBlocks.get(block) != event.getPlayer()) {
                                block.setType(Material.AIR);
                                kayaBlocks.remove(block);
                            }
                        }
                    }
                }
            }
        }
    }
}
