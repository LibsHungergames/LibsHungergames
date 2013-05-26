package me.libraryaddict.Hungergames.Abilities;

import java.util.HashMap;

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
import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Icicles extends AbilityListener {

    // TODO Rewrite this so the thawing does the message after 10 secs

    private HashMap<Player, Long> frozen = new HashMap<Player, Long>();
    private HashMap<Player, Integer> cooldown = new HashMap<Player, Integer>();
    public String crackString = ChatColor.AQUA + "*crack*";
    public String thawedOut = ChatColor.RED + "You thawed out";
    public int cooldownTime = 20;
    public int frozenTime = 10;
    public boolean fireAndLavaThaws = true;

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        if (frozen.containsKey(event.getKilled()))
            frozen.remove(event.getKilled());
        if (cooldown.containsKey(event.getKilled()))
            cooldown.remove(event.getKilled());
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (checkFrozen(event.getWhoClicked()))
            event.setCancelled(true);
    }

    private boolean checkFrozen(Object object) {
        if (frozen.containsKey(object)) {
            if (frozen.get(object) > System.currentTimeMillis()) {
                return true;
            }
            frozen.remove(object);
            ((Player) object).sendMessage(thawedOut);
            ((Player) object).playSound(((Player) object).getEyeLocation(), Sound.LAVA_POP, 1, 0);
        }
        return false;
    }

    @EventHandler
    public void onInvSwitch(PlayerItemHeldEvent event) {
        if (checkFrozen(event.getPlayer()))
            event.setCancelled(true);
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
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player p = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            if (hasAbility(p)) {
                if (!cooldown.containsKey(p) || cooldown.get(p) <= HungergamesApi.getHungergames().currentTime) {
                    if (!frozen.containsKey(victim) || frozen.get(victim) <= System.currentTimeMillis()) {
                        victim.sendMessage(crackString);
                        p.sendMessage(crackString);
                        victim.getWorld().playEffect(victim.getEyeLocation(), Effect.STEP_SOUND, 20);
                        frozen.put(victim, System.currentTimeMillis() + (frozenTime * 1000));
                        cooldown.put(p, HungergamesApi.getHungergames().currentTime + cooldownTime);
                    }
                }
            }
        }
    }
}
