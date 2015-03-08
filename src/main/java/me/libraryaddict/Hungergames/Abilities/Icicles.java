package me.libraryaddict.Hungergames.Abilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Icicles extends AbilityListener implements Disableable {

    public int chanceInOneOfFreezing = 4;
    private HashMap<Player, Integer> cooldown = new HashMap<Player, Integer>();
    public int cooldownTime = 20;
    public String crackString = ChatColor.AQUA + "*crack*";
    public boolean fireAndLavaThaws = true;
    private HashMap<Player, Integer> frozen = new HashMap<Player, Integer>();
    public int frozenTime = 10;
    private Hungergames hg = HungergamesApi.getHungergames();
    public String thawedOut = ChatColor.RED + "You thawed out";

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player p = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            if (hasAbility(p)) {
                if (!cooldown.containsKey(p) || cooldown.get(p) <= hg.currentTime) {
                    if (!frozen.containsKey(victim) && new Random().nextInt(chanceInOneOfFreezing) == 0) {
                        victim.sendMessage(crackString);
                        p.sendMessage(crackString);
                        victim.getWorld().playEffect(victim.getEyeLocation(), Effect.STEP_SOUND, 20);
                        frozen.put(victim, hg.currentTime + frozenTime);
                        cooldown.put(p, hg.currentTime + cooldownTime);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (fireAndLavaThaws && frozen.containsKey(event.getEntity()))
            if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK
                    || event.getCause() == DamageCause.LAVA) {
                frozen.remove(event.getEntity());
                ((Player) event.getEntity()).sendMessage(thawedOut);
            }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (frozen.containsKey(event.getWhoClicked()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInvSwitch(PlayerItemHeldEvent event) {
        if (frozen.containsKey(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        if (frozen.containsKey(event.getKilled().getPlayer()))
            frozen.remove(event.getKilled());
        if (cooldown.containsKey(event.getKilled().getPlayer()))
            cooldown.remove(event.getKilled());
    }

    @EventHandler
    public void onTime(TimeSecondEvent event) {
        if (frozen.containsValue(hg.currentTime)) {
            Iterator<Player> itel = frozen.keySet().iterator();
            while (itel.hasNext()) {
                Player p = itel.next();
                if (frozen.get(p) <= hg.currentTime) {
                    itel.remove();
                    p.sendMessage(thawedOut);
                    p.playSound(p.getEyeLocation(), Sound.LAVA_POP, 1, 0);
                }
            }
        }
    }
}
