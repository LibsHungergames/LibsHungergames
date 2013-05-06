package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.robingrether.idisguise.api.Disguise;
import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.api.MobDisguise;

public class Chameleon extends AbilityListener {

    public boolean breakDisguiseOnAttacked = true;
    public boolean breakDisguiseOnAttackPlayer = true;
    public String chameleonBreakDisguise = ChatColor.GREEN + "You broke out of your disguise!";
    public String chameleonDisguiseBroken = ChatColor.GREEN + "Your disguise was broken!";
    public String chameleonNowDisguised = ChatColor.GREEN + "Now disguised as a %s!";
    public boolean disguiseAsAnimal = true;
    public boolean disguiseAsMonster = true;

    public Chameleon() throws Exception {
        if (Bukkit.getPluginManager().getPlugin("iDisguise") == null)
            throw new Exception(String.format(HungergamesApi.getChatManager().getLoggerDependencyNotFound(), "Plugin iDiguise"));
    }

    private void disguise(Entity entity, Player p) {
        if ((entity instanceof Animals && disguiseAsAnimal) || (entity instanceof Monster && disguiseAsMonster)) {
            if (hasAbility(p)) {
                if (!DisguiseAPI.isDisguised(p))
                    DisguiseAPI.disguiseToAll(p, new MobDisguise(entity.getType(), true));
                else {
                    Disguise disguise = DisguiseAPI.getDisguise(p);
                    if (disguise.getType() == entity.getType())
                        return;
                    DisguiseAPI.disguiseToAll(p, new MobDisguise(entity.getType(), true));
                }
                p.sendMessage(String.format(chameleonNowDisguised,
                        HungergamesApi.getKitManager().toReadable(entity.getType().getName())));
            }
        }
    }

    private boolean isChameleon(Entity entity) {
        return (entity instanceof Player && hasAbility((Player) entity));
    }

    @EventHandler
    public void onDeath(PlayerKilledEvent event) {
        if (DisguiseAPI.isDisguised(event.getKilled().getPlayer()))
            DisguiseAPI.undisguiseToAll(event.getKilled().getPlayer());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        Entity damager = event.getDamager();
        if (event.getDamager() instanceof Projectile && ((Projectile) damager).getShooter() != null)
            damager = ((Projectile) damager).getShooter();
        if (isChameleon(event.getEntity()) && breakDisguiseOnAttacked) {
            Player p = (Player) event.getEntity();
            if (DisguiseAPI.isDisguised(p)) {
                DisguiseAPI.undisguiseToAll(p);
                p.sendMessage(chameleonDisguiseBroken);
            }
        }
        if (isChameleon(damager)) {
            Player p = (Player) damager;
            if (event.getEntity() instanceof Player && DisguiseAPI.isDisguised(p)) {
                if (breakDisguiseOnAttackPlayer) {
                    DisguiseAPI.undisguiseToAll(p);
                    p.sendMessage(chameleonBreakDisguise);
                }
            } else
                disguise(event.getEntity(), p);
        }
    }

}
