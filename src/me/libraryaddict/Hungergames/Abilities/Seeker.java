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
    private transient HashMap<ItemStack, Long> canUseAgain = new HashMap<ItemStack, Long>();
    public int cooldown = 120;
    private String cooldownMessage = ChatColor.BLUE + "The ghost eye will be usable in %s seconds!";
    public boolean doCircle = true;
    public int seekerItemId = Material.EYE_OF_ENDER.getId();
    public String seekerItemName = ChatColor.WHITE + "Ghost Eye";
    private List<Material> transparent = Arrays.asList(new Material[] { Material.STONE, Material.LEAVES, Material.GRASS,
            Material.DIRT, Material.LOG, Material.SAND, Material.SANDSTONE, Material.ICE, Material.QUARTZ_BLOCK, Material.GRAVEL,
            Material.COBBLESTONE, Material.OBSIDIAN, Material.BEDROCK });
    private String usedSeekerEye = ChatColor.BLUE
            + "You body slam the ghost eye into your socket. Not gonna recover from that for a few minutes..";
    public int xrayRadius = 10;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (isSpecialItem(item, seekerItemName) && item.getTypeId() == seekerItemId) {
            event.setCancelled(true);
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
                return;
            long lastUsed = 0;
            if (canUseAgain.containsKey(item))
                lastUsed = canUseAgain.get(item);
            if (System.currentTimeMillis() > lastUsed) {
                canUseAgain.put(item, System.currentTimeMillis() + (cooldown * 1000));
                event.getPlayer().sendMessage(usedSeekerEye);
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

                event.getPlayer().sendMessage(String.format(cooldownMessage, -((System.currentTimeMillis() - lastUsed) / 1000)));
            }
        }
    }
}
