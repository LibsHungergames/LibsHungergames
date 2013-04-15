package me.libraryaddict.Hungergames.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Endermage extends Extender implements Listener {

    HashMap<Player, List<Player>> angryMaged = new HashMap<Player, List<Player>>();

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() == Material.ENDER_PORTAL && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Endermage Portal")) {
            final List<Gamer> maged = new ArrayList<Gamer>();
            List<Player> victims = new ArrayList<Player>();
            final Location portal = event.getBlock().getLocation().clone().add(0.5, 0.5, 0.5);
            for (Gamer gamer : pm.getAliveGamers()) {
                Player p = event.getPlayer();
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
                        if (no == 5)
                            angryMaged.remove(mager.getPlayer());
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
