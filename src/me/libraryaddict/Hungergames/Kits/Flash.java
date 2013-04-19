package me.libraryaddict.Hungergames.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Flash {

    private HashMap<ItemStack, Integer> cooldown = new HashMap<ItemStack, Integer>();
    private Hungergames hg = HungergamesApi.getHungergames();
    private PlayerManager pm = HungergamesApi.getPlayerManager();

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        if (cooldown.containsValue(hg.currentTime)) {
            ItemStack item = null;
            for (ItemStack i : cooldown.keySet())
                if (cooldown.get(i) == hg.currentTime) {
                    item = i;
                    break;
                }
            if (item == null)
                return;
            for (Gamer gamer : pm.getAliveGamers()) {
                if (gamer.getPlayer().getInventory().contains(item)) {
                    item.setType(Material.REDSTONE_TORCH_ON);
                    cooldown.remove(item);
                    return;
                }
            }
            for (Item itemEntity : hg.world.getEntitiesByClass(Item.class)) {
                if (itemEntity.getItemStack().equals(item)) {
                    itemEntity.getItemStack().setType(Material.REDSTONE_TORCH_ON);
                    cooldown.remove(item);
                    return;
                }
            }
            cooldown.remove(item);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && event.getAction().name().contains("RIGHT")) {
            if (item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().startsWith("" + ChatColor.WHITE)
                    && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("Flash")) {
                event.setCancelled(true);
                if (item.getType() == Material.REDSTONE_TORCH_OFF) {
                    event.getPlayer().sendMessage(
                            ChatColor.BLUE + "You can use this again in " + (hg.currentTime - cooldown.get(item)) + " seconds!");
                } else if (item.getType() == Material.REDSTONE_TORCH_ON) {
                    List<Block> b = event.getPlayer().getLastTwoTargetBlocks(null, 200);
                    if (b.size() > 1 && b.get(1).getType() != Material.AIR) {
                        double dist = event.getPlayer().getLocation().distance(b.get(0).getLocation());
                        if (dist > 2) {
                            cooldown.put(item, 30 + hg.currentTime);
                            Location loc = b.get(0).getLocation().clone().add(0.5, 0.5, 0.5);
                            item.setType(Material.REDSTONE_TORCH_OFF);
                            Location pLoc = event.getPlayer().getLocation();
                            loc.setPitch(pLoc.getPitch());
                            loc.setYaw(pLoc.getYaw());
                            event.getPlayer().teleport(loc);
                            pLoc.getWorld().playSound(pLoc, Sound.ENDERMAN_TELEPORT, 1, 1.2F);
                            pLoc.getWorld().playSound(loc, Sound.ENDERMAN_TELEPORT, 1, 1.2F);
                            ((CraftWorld) pLoc.getWorld()).getHandle().addParticle("portal", pLoc.getX(), pLoc.getY(),
                                    pLoc.getZ(), loc.getX(), loc.getY(), loc.getZ());
                            ((CraftWorld) pLoc.getWorld()).getHandle().addParticle("portal", loc.getX(), loc.getY(), loc.getZ(),
                                    pLoc.getX(), pLoc.getY(), pLoc.getZ());
                        }
                    }
                }
            }
        }
    }
}
