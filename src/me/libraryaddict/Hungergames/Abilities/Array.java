package me.libraryaddict.Hungergames.Abilities;

import java.util.HashMap;
import java.util.Iterator;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Managers.EnchantmentManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Array extends AbilityListener {

    private transient HashMap<HealArray, Player> beacons = new HashMap<HealArray, Player>();
    private transient PlayerManager pm = HungergamesApi.getPlayerManager();
    public int arrayExpireTime = 30;
    public String arrayItemName = ChatColor.WHITE + "Array";
    public int arrayBeaconId = Material.BEACON.getId();

    class HealArray {
        Block[] blocks;
        long expires;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (isSpecialItem(event.getItemInHand(), arrayItemName) && event.getItemInHand().getTypeId() == arrayBeaconId) {
            // Create beacon
            Block b = event.getBlock();
            HealArray heal = new HealArray();
            heal.expires = System.currentTimeMillis() + (arrayExpireTime * 1000);
            heal.blocks = new Block[3];
            for (int i = 0; i < 3; i++) {
                heal.blocks[i] = b;
                if (i != 2)
                    b.setType(Material.FENCE);
                else
                    b.setType(Material.GLOWSTONE);
                b = b.getRelative(BlockFace.UP);
            }
            beacons.put(heal, event.getPlayer());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        for (HealArray heal : beacons.keySet()) {
            for (Block b : heal.blocks)
                if (b.equals(event.getBlock())) {
                    event.setCancelled(true);
                    return;
                }
        }
    }

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        Iterator<HealArray> itel = beacons.keySet().iterator();
        while (itel.hasNext()) {
            HealArray heal = itel.next();
            Player player = beacons.get(heal);
            if (heal.expires < System.currentTimeMillis()) {
                ItemStack item = new ItemStack(arrayBeaconId);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(arrayItemName);
                item.setItemMeta(meta);
                item.addEnchantment(EnchantmentManager.UNLOOTABLE, 1);
                EnchantmentManager.updateEnchants(item);
                HungergamesApi.getKitManager().addItem(player, item);
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
        if (beacons.containsValue(event.getKilled().getPlayer())) {
            for (HealArray array : beacons.keySet()) {
                if (beacons.get(array).equals(event.getKilled().getPlayer())) {
                    for (Block b : array.blocks)
                        b.setType(Material.AIR);
                }
            }
        }
    }

}
