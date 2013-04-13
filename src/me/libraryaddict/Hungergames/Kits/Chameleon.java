package me.libraryaddict.Hungergames.Kits;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.Extender;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class Chameleon extends Extender implements Listener {
    DisguiseCraftAPI dcAPI = DisguiseCraft.getAPI();

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (isChameleon(event.getEntity())) {
            Player p = (Player) event.getEntity();
            if (dcAPI.isDisguised(p)) {
                dcAPI.undisguisePlayer(p);
                p.sendMessage(ChatColor.GREEN + "Your disguise was broken!");
            }
        }
        if (isChameleon(event.getDamager())) {
            Player p = (Player) event.getDamager();
            if (event.getEntity() instanceof Player && dcAPI.isDisguised(p)) {
                dcAPI.undisguisePlayer(p);
                p.sendMessage(ChatColor.GREEN + "You broke out of your disguise!");
            } else
                disguise(event.getEntity(), p);
        }
    }

    @EventHandler
    public void onDeath(PlayerKilledEvent event) {
        if (dcAPI.isDisguised(event.getKilled().getPlayer()))
            dcAPI.undisguisePlayer(event.getKilled().getPlayer());
    }

    private void disguise(Entity entity, Player p) {
        if (entity instanceof Creature) {
            if (pm.getGamer(p).isAlive() && kits.hasAbility(p, "Chameleon")) {
                if (!dcAPI.isDisguised(p))
                    dcAPI.disguisePlayer(p,
                            new Disguise(dcAPI.newEntityID(), DisguiseType.fromString(entity.getType().getName())));
                else {
                    Disguise disguise = dcAPI.getDisguise(p);
                    if (disguise.type == DisguiseType.fromString(entity.getType().getName()))
                        return;
                    dcAPI.changePlayerDisguise(p,
                            new Disguise(dcAPI.getDisguise(p).entityID, DisguiseType.fromString(entity.getType().getName())));
                }
                p.sendMessage(ChatColor.GREEN + "Now disguised as a " + kits.toReadable(entity.getType().getName()) + "!");
            }
        }
    }

    private boolean isChameleon(Entity entity) {
        return (entity instanceof Player && kits.hasAbility((Player) entity, "Chameleon"));
    }

}
