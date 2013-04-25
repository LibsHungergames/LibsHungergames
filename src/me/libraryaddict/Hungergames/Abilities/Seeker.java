package me.libraryaddict.Hungergames.Abilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Seeker extends AbilityListener {
    private transient HashMap<ItemStack, Long> lastClicked = new HashMap<ItemStack, Long>();
    private List<Material> transparent = Arrays.asList(new Material[] { Material.STONE, Material.LEAVES, Material.GRASS,
            Material.DIRT, Material.LOG, Material.SAND, Material.SANDSTONE, Material.ICE, Material.QUARTZ_BLOCK, Material.GRAVEL,
            Material.COBBLESTONE, Material.OBSIDIAN, Material.BEDROCK });
    public int xrayRadius = 10;
    public boolean doCircle = true;
    public int cooldown = 120;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().startsWith("" + ChatColor.WHITE)
                && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("Ghost Eye")) {
            event.setCancelled(true);
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
                return;
            long last = 0;
            if (lastClicked.containsKey(item))
                last = lastClicked.get(item);
            if ((cooldown * 1000) - System.currentTimeMillis() > last) {
                lastClicked.put(item, System.currentTimeMillis());
                event.getPlayer()
                        .sendMessage(
                                ChatColor.BLUE
                                        + "You body slam the ghost eye into your socket. Not gonna recover from that for a few minutes..");
                // Turn into glass
                Location beginning = event.getClickedBlock().getLocation().clone().add(0.5, 0.5, 0.5);
                int dist = (doCircle ? xrayRadius * 2 : xrayRadius);
                for (int x = -dist; x <= dist; x++) {
                    for (int y = -dist; y <= dist; y++) {
                        for (int z = -dist; z <= dist; z++) {
                            Location loc = event.getClickedBlock().getLocation().clone().add(x, y, z).add(0.5, 0.5, 0.5);
                            if ((!doCircle || beginning.distance(loc) <= 10) && transparent.contains(loc.getBlock().getType()))
                                event.getPlayer().sendBlockChange(loc, Material.GLASS, (byte) 0);
                        }
                    }
                }
            } else {
                event.getPlayer().sendMessage(
                        ChatColor.BLUE + "The ghost eye will be usable in "
                                + (((cooldown * 1000) - System.currentTimeMillis() - last) / 1000) + " seconds");
            }
        }
    }
}
