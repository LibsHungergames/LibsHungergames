package me.libraryaddict.Hungergames.Kits;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Seeker implements Listener {
    private KitManager kits = HungergamesApi.getKitManager();
    private HashMap<ItemStack, Long> lastUsed = new HashMap<ItemStack, Long>();
    private List<Material> transparent = Arrays.asList(new Material[] { Material.STONE, Material.LEAVES, Material.GRASS,
            Material.DIRT, Material.LONG_GRASS, Material.LOG, Material.SAND, Material.SANDSTONE, Material.SNOW, Material.ICE,
            Material.QUARTZ_BLOCK, Material.GRAVEL, Material.COBBLESTONE, Material.OBSIDIAN, Material.BEDROCK });

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Ghost Eye")) {
            event.setCancelled(true);
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
                return;
            long last = 0;
            if (lastUsed.containsKey(item))
                last = lastUsed.get(item);
            if (last < System.currentTimeMillis()) {
                lastUsed.put(item, System.currentTimeMillis() + 120000);
                // Turn into glass
                for (int x = -30; x <= 30; x++) {
                    for (int y = -30; y <= 30; y++) {
                        for (int z = -30; z <= 30; z++) {
                            Block b = event.getClickedBlock().getLocation().clone().add(x, y, z).getBlock();
                            if (transparent.contains(b.getType()))
                                event.getPlayer().sendBlockChange(b.getLocation(), Material.GLASS, (byte) 0);
                        }
                    }
                }
            } else {
                event.getPlayer().sendMessage(
                        ChatColor.BLUE + "The ghost eye will be usable in " + ((System.currentTimeMillis() - last) / 1000)
                                + " seconds");
            }
        }
    }
}
