package me.libraryaddict.Hungergames.Kits;

import java.util.HashMap;
import java.util.Iterator;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Array implements Listener {

    HashMap<Player, HealArray> beacons = new HashMap<Player, HealArray>();
    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private KitManager kits = HungergamesApi.getKitManager();

    class HealArray {
        Block[] blocks;
        long expires;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.BEACON && event.getItemInHand().getItemMeta().hasDisplayName()
                && ChatColor.stripColor(event.getItemInHand().getItemMeta().getDisplayName()).equals("Array")) {
            // Create beacon
            Block b = event.getBlock();
            HealArray heal = new HealArray();
            heal.expires = System.currentTimeMillis() + 30000;
            heal.blocks = new Block[] { b, b.getRelative(BlockFace.UP), b.getRelative(BlockFace.UP).getRelative(BlockFace.UP) };
            beacons.put(event.getPlayer(), heal);
            b.setType(Material.FENCE);
            b.getRelative(BlockFace.UP).setType(Material.FENCE);
            b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.GLOWSTONE);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        for (HealArray heal : beacons.values()) {
            for (Block b : heal.blocks)
                if (b == event.getBlock()) {
                    event.setCancelled(true);
                    break;
                }
        }
    }

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        Iterator<Player> itel = beacons.keySet().iterator();
        while (itel.hasNext()) {
            Player player = itel.next();
            HealArray heal = beacons.get(player);
            if (heal.expires < System.currentTimeMillis()) {
                ItemStack item = new ItemStack(Material.BEACON);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.WHITE + "Array");
                kits.addItem(player, item);
                for (Block b : heal.blocks)
                    b.setType(Material.AIR);
                itel.remove();
                continue;
            }
            for (Gamer gamer : pm.getAliveGamers()) {
                Player p = gamer.getPlayer();
                if (p.getLocation().distance(heal.blocks[0].getLocation()) < 6)
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1), true);
            }
        }
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        if (beacons.containsKey(event.getKilled().getPlayer()))
            for (Block b : beacons.remove(event.getKilled().getPlayer()).blocks)
                b.setType(Material.AIR);
    }

}
