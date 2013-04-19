package me.libraryaddict.Hungergames.Kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Seeker implements Listener {
    private HashMap<ItemStack, Long> lastClicked = new HashMap<ItemStack, Long>();
    private List<Material> transparent = Arrays.asList(new Material[] { Material.STONE, Material.LEAVES, Material.GRASS,
            Material.DIRT, Material.LOG, Material.SAND, Material.SANDSTONE, Material.ICE, Material.QUARTZ_BLOCK, Material.GRAVEL,
            Material.COBBLESTONE, Material.OBSIDIAN, Material.BEDROCK });

    class Entry {
        ItemStack item;
        long used;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("Ghost Eye")) {
            event.setCancelled(true);
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
                return;
            long last = 0;
            if (lastClicked.containsKey(item))
                last = lastClicked.get(item);
            if (last < System.currentTimeMillis()) {
                lastClicked.put(item, System.currentTimeMillis() + 120000);
                event.getPlayer()
                        .sendMessage(
                                ChatColor.BLUE
                                        + "You body slam the ghost eye into your socket. Not gonna recover from that for a few minutes..");
                // Turn into glass
                for (int x = -15; x <= 15; x++) {
                    for (int y = -15; y <= 15; y++) {
                        for (int z = -15; z <= 15; z++) {
                            Block b = event.getClickedBlock().getLocation().clone().add(x, y, z).getBlock();
                            if (transparent.contains(b.getType()))
                                event.getPlayer().sendBlockChange(b.getLocation(), Material.GLASS, (byte) 0);
                        }
                    }
                }
            } else {
                event.getPlayer().sendMessage(
                        ChatColor.BLUE + "The ghost eye will be usable in " + (-(System.currentTimeMillis() - last) / 1500)
                                + " seconds");
            }
        }
    }
}
