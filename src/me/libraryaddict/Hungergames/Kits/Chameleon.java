package me.libraryaddict.Hungergames.Kits;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.robingrether.idisguise.api.Disguise;
import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.api.MobDisguise;

public class Chameleon implements Listener {
    // DisguiseCraftAPI dcAPI = DisguiseCraft.getAPI();

    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private KitManager kits = HungergamesApi.getKitManager();

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (isChameleon(event.getEntity())) {
            Player p = (Player) event.getEntity();
            if (DisguiseAPI.isDisguised(p)) {
                DisguiseAPI.undisguiseToAll(p);
                p.sendMessage(ChatColor.GREEN + "Your disguise was broken!");
            }
        }
        if (isChameleon(event.getDamager())) {
            Player p = (Player) event.getDamager();
            if (event.getEntity() instanceof Player && DisguiseAPI.isDisguised(p)) {
                DisguiseAPI.undisguiseToAll(p);
                p.sendMessage(ChatColor.GREEN + "You broke out of your disguise!");
            } else
                disguise(event.getEntity(), p);
        }
    }

    @EventHandler
    public void onDeath(PlayerKilledEvent event) {
        if (DisguiseAPI.isDisguised(event.getKilled().getPlayer()))
            DisguiseAPI.undisguiseToAll(event.getKilled().getPlayer());
    }

    private void disguise(Entity entity, Player p) {
        if (entity instanceof Creature) {
            if (pm.getGamer(p).isAlive() && kits.hasAbility(p, "Chameleon")) {
                if (!DisguiseAPI.isDisguised(p))
                    DisguiseAPI.disguiseToAll(p, new MobDisguise(entity.getType(), true));
                else {
                    Disguise disguise = DisguiseAPI.getDisguise(p);
                    if (disguise.getType() == entity.getType())
                        return;
                    DisguiseAPI.disguiseToAll(p, new MobDisguise(entity.getType(), true));
                }
                p.sendMessage(ChatColor.GREEN + "Now disguised as a " + kits.toReadable(entity.getType().getName()) + "!");
            }
        }
    }

    private boolean isChameleon(Entity entity) {
        return (entity instanceof Player && kits.hasAbility((Player) entity, "Chameleon"));
    }

}
