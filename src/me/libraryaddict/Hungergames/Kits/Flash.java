package me.libraryaddict.Hungergames.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Flash implements Listener {

    private HashMap<ItemStack, Integer> cooldown = new HashMap<ItemStack, Integer>();
    private Hungergames hg = HungergamesApi.getHungergames();
    private PlayerManager pm = HungergamesApi.getPlayerManager();
    HashSet<Byte> set = new HashSet<Byte>();

    public Flash() {
        set.add((byte) 0);
        for (byte b = 8; b < 12; b++)
            set.add(b);
        set.add((byte) Material.SNOW.getId());
        set.add((byte) Material.LONG_GRASS.getId());
        set.add((byte) Material.RED_MUSHROOM.getId());
        set.add((byte) Material.RED_ROSE.getId());
        set.add((byte) Material.YELLOW_FLOWER.getId());
        set.add((byte) Material.BROWN_MUSHROOM.getId());
        set.add((byte) Material.SIGN_POST.getId());
        set.add((byte) Material.WALL_SIGN.getId());
        set.add((byte) Material.FIRE.getId());
        set.add((byte) Material.TORCH.getId());
        set.add((byte) Material.REDSTONE_WIRE.getId());
        set.add((byte) Material.REDSTONE_TORCH_OFF.getId());
        set.add((byte) Material.REDSTONE_TORCH_ON.getId());
        set.add((byte) Material.VINE.getId());
    }

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
                    cooldown.remove(item);
                    for (ItemStack i : gamer.getPlayer().getInventory().getContents()) {
                        if (i.equals(item)) {
                            i.setType(Material.REDSTONE_TORCH_ON);
                            return;
                        }
                    }
                }
                if (gamer.getPlayer().getItemOnCursor() != null && gamer.getPlayer().getItemOnCursor().equals(item)) {
                    gamer.getPlayer().getItemOnCursor().setType(Material.REDSTONE_TORCH_ON);
                    return;
                }
            }
            for (Item itemEntity : hg.world.getEntitiesByClass(Item.class)) {
                if (itemEntity.getItemStack().equals(item)) {
                    cooldown.remove(item);
                    itemEntity.getItemStack().setType(Material.REDSTONE_TORCH_ON);
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
                event.getPlayer().updateInventory();
                if (item.getType() == Material.REDSTONE_TORCH_OFF) {
                    event.getPlayer().sendMessage(
                            ChatColor.BLUE + "You can use this again in " + (-(hg.currentTime - cooldown.get(item)))
                                    + " seconds!");
                } else if (item.getType() == Material.REDSTONE_TORCH_ON) {
                    List<Block> b = event.getPlayer().getLastTwoTargetBlocks(set, 200);
                    if (b.size() > 1 && b.get(1).getType() != Material.AIR) {
                        double dist = event.getPlayer().getLocation().distance(b.get(0).getLocation());
                        if (dist > 2) {
                            Location loc = b.get(0).getLocation().clone().add(0.5, 0.5, 0.5);
                            item.setType(Material.REDSTONE_TORCH_OFF);
                            int cool = 30;
                            if ((dist / 2) > 30)
                                cool = (int) (dist / 2);
                            cooldown.put(item, cool + hg.currentTime);
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
                            event.getPlayer().addPotionEffect(
                                    new PotionEffect(PotionEffectType.WEAKNESS, (int) ((dist / 2) * 20), 1), true);
                            pLoc.getWorld().strikeLightningEffect(loc);
                        }
                    }
                }
            }
        }
    }
}
