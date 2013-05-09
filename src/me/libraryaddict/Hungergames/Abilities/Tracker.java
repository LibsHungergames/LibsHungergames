package me.libraryaddict.Hungergames.Abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Events.PlayerTrackEvent;
import me.libraryaddict.Hungergames.Managers.TranslationManager;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class Tracker extends AbilityListener implements CommandExecutor {
    private TranslationManager cm = HungergamesApi.getTranslationManager();
    public String howToUseMessage = ChatColor.RED + "Use /lock <Target Player>";
    private transient List<String> locked = new ArrayList<String>();
    public String lostTargetMessage = ChatColor.GOLD + "Lost target, Retargetting";
    public String noLongerLockedMessage = ChatColor.GOLD + "No longer locked on target";
    public String notATracker = ChatColor.RED + "You are not kit Tracker!";
    public String trackerTrackingMessage = ChatColor.GOLD
            + "Compass pointing at %1$2s who is %2$2s blocks away at location (%3$2s, %4$2s, %5$2s)";
    private transient HashMap<Player, Player> tracking = new HashMap<Player, Player>();

    @Override
    public String getCommand() {
        return "track";
    }

    @EventHandler
    public void handleTrack(PlayerTrackEvent event) {
        Player p = event.getTracker().getPlayer();
        Player victim = event.getVictim();
        Location loc = event.getLocation();
        if (hasAbility(p)) {
            event.setMessage(String.format(trackerTrackingMessage, victim.getName(), ((int) p.getLocation().distance(loc)),
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            tracking.put(p, victim);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = HungergamesApi.getPlayerManager().getGamer(sender.getName());
        if (hasAbility(gamer.getPlayer())) {
            if (args.length > 0) {
                Gamer victimGamer = HungergamesApi.getPlayerManager().getGamer(Bukkit.getPlayer(args[0]));
                if (victimGamer.isAlive()) {
                    Player victim = victimGamer.getPlayer();
                    locked.remove(sender.getName());
                    tracking.put(gamer.getPlayer(), victimGamer.getPlayer());
                    locked.add(sender.getName());
                    PlayerTrackEvent trackEvent = new PlayerTrackEvent(gamer, victim,
                            (victim == null ? cm.getMessagePlayerTrackNoVictim() : String.format(cm.getMessagePlayerTrack(),
                                    victim.getName())));
                    Bukkit.getPluginManager().callEvent(trackEvent);
                    if (!trackEvent.isCancelled()) {
                        sender.sendMessage(trackEvent.getMessage());
                        gamer.getPlayer().setCompassTarget(trackEvent.getLocation());
                    }
                    handleTrack(trackEvent);
                }
            } else {
                if (locked.contains(sender.getName())) {
                    sender.sendMessage(noLongerLockedMessage);
                    locked.remove(sender.getName());
                } else
                    sender.sendMessage(howToUseMessage);
            }
        } else
            sender.sendMessage(notATracker);
        return true;
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
                            p.sendMessage(lostTargetMessage);
                            track(p);
                        }
                    }
                });
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        for (Player p : tracking.keySet())
            if (tracking.get(p) == event.getPlayer())
                p.setCompassTarget(event.getTo());
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
        PlayerTrackEvent trackEvent = new PlayerTrackEvent(HungergamesApi.getPlayerManager().getGamer(p), victim,
                (victim == null ? cm.getMessagePlayerTrackNoVictim()
                        : String.format(cm.getMessagePlayerTrack(), victim.getName())));
        Bukkit.getPluginManager().callEvent(trackEvent);
        if (!trackEvent.isCancelled()) {
            p.sendMessage(trackEvent.getMessage());
            p.setCompassTarget(trackEvent.getLocation());
        }
    }
}
