package me.libraryaddict.Hungergames.Listeners;

import java.util.Random;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.ConfigManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.server.ServerListPingEvent;

public class GeneralListener implements Listener {

    Hungergames hg = HungergamesApi.getHungergames();
    ConfigManager config = HungergamesApi.getConfigManager();
    PlayerManager pm = HungergamesApi.getPlayerManager();

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (hg.currentTime < 0)
            event.setCancelled(true);
        else if (event.getTarget() instanceof Player && !pm.getGamer((Player) event.getTarget()).isAlive())
            event.setCancelled(true);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.SLIME)
            event.setCancelled(true);
        if (hg.currentTime < 0) {
            event.setCancelled(true);
            if (event.getEntity() instanceof Animals && event.getSpawnReason() == SpawnReason.CHUNK_GEN
                    && new Random().nextInt(4) == 1)
                hg.entitys.put(event.getLocation().clone(), event.getEntityType());
        } else if (event.getEntity() instanceof Animals && event.getSpawnReason() == SpawnReason.CHUNK_GEN
                && new Random().nextInt(4) != 1)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player
                || (event.getEntity() instanceof Tameable && ((Tameable) event.getEntity()).isTamed())) {
            if ((event.getEntity() instanceof Player && (!hg.doSeconds || !pm.getGamer(event.getEntity()).isAlive()))
                    || hg.currentTime <= config.getInvincibilityTime())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void ignite(final BlockIgniteEvent event) {
        if (hg.currentTime < 0 && config.isFireSpreadDisabled()) {
            event.setCancelled(true);
            return;
        }
        /*
         * if (event.getPlayer() != null) {
         * Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
         * public void run() { ItemStack item =
         * event.getPlayer().getItemInHand(); if (item != null && item.getType()
         * == Material.FLINT_AND_STEEL) { item.setDurability((short)
         * (item.getDurability() + 10)); if (item.getDurability() > 63)
         * event.getPlayer().setItemInHand(new ItemStack(Material.AIR)); } } });
         * }
         */
    }

    @EventHandler
    public void pigZap(PigZapEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        if (hg.currentTime >= 0)
            event.setMotd(config.getGameStartedMotd());
        else {
            String curTime = "";
            if (hg.currentTime < -60) {
                curTime = (int) Math.floor(Math.abs(hg.currentTime) / 60) + " " + (config.shortenTime() ? "min" : "minute");
                if (hg.currentTime <= -120)
                    curTime += "s";
            } else {
                curTime = Math.abs(hg.currentTime) + " " + (config.shortenTime() ? "sec" : "second");
                if (hg.currentTime < -1)
                    curTime += "s";
            }
            event.setMotd(config.getGameStartingMotd().replaceAll("%time%", curTime));
        }
    }

}
