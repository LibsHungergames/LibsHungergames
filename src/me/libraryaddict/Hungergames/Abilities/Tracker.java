package me.libraryaddict.Hungergames.Abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class Tracker extends AbilityListener implements CommandExecutor {
    private transient HashMap<Player, Player> tracking = new HashMap<Player, Player>();
    private transient List<String> locked = new ArrayList<String>();

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
        for (Gamer game : HungergamesApi.getPlayerManager().getAliveGamers()) {
            double distOfPlayerToVictim = p.getLocation().distance(game.getPlayer().getLocation());
            if (distOfPlayerToVictim < distance && distOfPlayerToVictim > 15) {
                distance = distOfPlayerToVictim;
                victim = game.getPlayer();
            }
        }
        if (victim != null) {
            p.setCompassTarget(victim.getLocation());
            if (hasAbility(p)) {
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
                Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
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

    @Override
    public String getCommand() {
        return "track";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = HungergamesApi.getPlayerManager().getGamer(sender.getName());
        if (hasAbility(gamer.getPlayer())) {
            if (args.length > 0) {
                Gamer victim = HungergamesApi.getPlayerManager().getGamer(Bukkit.getPlayer(args[0]));
                if (victim.isAlive()) {
                    locked.remove(sender.getName());
                    gamer.getPlayer().setCompassTarget(victim.getPlayer().getPlayer().getLocation());
                    tracking.put(gamer.getPlayer(), victim.getPlayer());
                    locked.add(sender.getName());
                    sender.sendMessage(ChatColor.YELLOW + "Compass pointing at " + victim.getPlayer().getName() + " who is "
                            + ((int) victim.getPlayer().getLocation().distance(gamer.getPlayer().getLocation()))
                            + " blocks away at location (" + victim.getPlayer().getLocation().getBlockX() + ", "
                            + victim.getPlayer().getLocation().getBlockY() + ", " + victim.getPlayer().getLocation().getBlockZ()
                            + ")");
                }
            } else {
                if (locked.contains(sender.getName())) {
                    sender.sendMessage(ChatColor.YELLOW + "No longer locked on target");
                    locked.remove(sender.getName());
                } else
                    sender.sendMessage(ChatColor.YELLOW + "Use /lock <Target Player>");
            }
        } else
            sender.sendMessage(ChatColor.YELLOW + "You are not kit Tracker!");
        return true;
    }
}
