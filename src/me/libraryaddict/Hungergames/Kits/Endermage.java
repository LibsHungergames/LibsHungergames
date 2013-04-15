package me.libraryaddict.Hungergames.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.libraryaddict.Hungergames.Types.Enchants;
import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Endermage extends Extender implements Listener {

    HashMap<Player, List<Player>> angryMaged = new HashMap<Player, List<Player>>();

    @EventHandler
    public void onPlace(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && item.getType() == Material.ENDER_PORTAL
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Endermage Portal")) {
            event.setCancelled(true);
            final Block b = event.getClickedBlock();
            if (b.getType() == Material.ENDER_PORTAL)
                return;
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() == 0)
                event.getPlayer().setItemInHand(new ItemStack(0));
            final List<Gamer> maged = new ArrayList<Gamer>();
            List<Player> victims = new ArrayList<Player>();
            final Location portal = b.getLocation().clone().add(0.5, 0.5, 0.5);
            final Material material = b.getType();
            final byte dataValue = b.getData();
            portal.getBlock().setType(Material.ENDER_PORTAL);
            for (Gamer gamer : pm.getAliveGamers()) {
                Player p = gamer.getPlayer();
                if (p != event.getPlayer() && isEnderable(portal, p.getLocation())) {
                    maged.add(gamer);
                    victims.add(p);
                    p.setNoDamageTicks(5 * 20);
                }
            }
            final Gamer mager = pm.getGamer(event.getPlayer());
            for (int i = 0; i <= 5; i++) {
                final int no = i;
                Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
                    public void run() {
                        if (mager.isAlive()) {
                            for (Gamer gamer : maged) {
                                if (gamer.isAlive()) {
                                    if (gamer.getPlayer().getLocation().distance(portal) > 4) {
                                        gamer.getPlayer().playEffect(gamer.getPlayer().getLocation(), Effect.ENDER_SIGNAL, 9);
                                        gamer.getPlayer().playEffect(portal, Effect.ENDER_SIGNAL, 9);
                                    }
                                    gamer.getPlayer().teleport(portal);
                                }
                            }
                        }
                        if (no == 5) {
                            portal.getBlock().setTypeIdAndData(material.getId(), dataValue, true);
                            angryMaged.remove(mager.getPlayer());
                            if (mager.isAlive()) {
                                ItemStack item = new ItemStack(Material.ENDER_PORTAL);
                                ItemMeta meta = item.getItemMeta();
                                meta.setDisplayName(ChatColor.WHITE + "Endermage Portal");
                                item.setItemMeta(meta);
                                item.addEnchantment(Enchants.UNLOOTABLE, 1);
                                kits.addItem(mager.getPlayer(), item);
                            }
                        }
                    }
                }, i * 20);
            }
        }
    }

    private boolean isEnderable(Location portal, Location player) {
        return Math.abs(portal.getX() - player.getX()) < 2 && Math.abs(portal.getZ() - player.getZ()) < 2
                && Math.abs(portal.getY() - player.getY()) > 4;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (angryMaged.containsKey(event.getEntity()) && angryMaged.get(event.getEntity()).contains(event.getDamager()))
            event.setDamage(9999);
    }

}
