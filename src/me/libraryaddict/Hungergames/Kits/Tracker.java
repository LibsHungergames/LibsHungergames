package me.libraryaddict.Hungergames.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class Tracker implements Listener {
    public static HashMap<Player, Player> tracking = new HashMap<Player, Player>();
    public static List<String> locked = new ArrayList<String>();

    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private KitManager kits = HungergamesApi.getKitManager();
    private Hungergames hg = HungergamesApi.getHungergames();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == Material.COMPASS && event.getAction() != Action.PHYSICAL) {
            track(event.getPlayer());
        }
    }

    private void track(Player p) {
        if (locked.contains(p.getName()))
            return;
        double distance = 10000;
        Player victim = null;
        for (Gamer game : pm.getAliveGamers()) {
            double distOfPlayerToVictim = p.getLocation().distance(game.getPlayer().getLocation());
            if (distOfPlayerToVictim < distance && distOfPlayerToVictim > 15) {
                distance = distOfPlayerToVictim;
                victim = game.getPlayer();
            }
        }
        if (victim != null) {
            p.setCompassTarget(victim.getLocation());
            if (kits.hasAbility(p, "Tracker")) {
                p.sendMessage(ChatColor.YELLOW + "Compass pointing at " + victim.getName() + " who is " + ((int) distance)
                        + " blocks away at location (" + victim.getLocation().getBlockX() + ", "
                        + victim.getLocation().getBlockY() + ", " + victim.getLocation().getBlockZ() + ")");
                tracking.put(p, victim);
            } else
                p.sendMessage(ChatColor.YELLOW + "Compass pointing at " + victim.getName());
        } else {
            p.setCompassTarget(p.getWorld().getSpawnLocation());
            p.sendMessage(ChatColor.YELLOW + "No players found, compass pointing at spawn");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        for (Player p : tracking.keySet())
            if (tracking.get(p) == event.getPlayer())
                p.setCompassTarget(event.getTo());
    }

    @EventHandler
    public void onDeath(PlayerKilledEvent event) {
        tracking.remove(event.getKilled().getPlayer());
        Iterator<Player> itel = tracking.keySet().iterator();
        while (itel.hasNext()) {
            Player p = itel.next();
            if (tracking.get(p) == event.getKilled().getPlayer()) {
                itel.remove();
                locked.remove(p.getName());
                final String name = p.getName();
                Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
                    public void run() {
                        Player p = Bukkit.getPlayerExact(name);
                        if (p != null) {
                            p.sendMessage(ChatColor.RED + "Lost target. Retargetting");
                            track(p);
                        }
                    }
                });
            }
        }
    }
}
