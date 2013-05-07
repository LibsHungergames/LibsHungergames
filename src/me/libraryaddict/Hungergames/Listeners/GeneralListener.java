package me.libraryaddict.Hungergames.Listeners;

import java.util.Random;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.TranslationManager;
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

    private TranslationManager cm = HungergamesApi.getTranslationManager();
    private ConfigManager config = HungergamesApi.getConfigManager();
    private Hungergames hg = HungergamesApi.getHungergames();
    private PlayerManager pm = HungergamesApi.getPlayerManager();

    @EventHandler
    public void ignite(final BlockIgniteEvent event) {
        if (hg.currentTime < 0 && config.isFireSpreadDisabled()) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (hg.currentTime <= config.getInvincibilityTime() || !hg.doSeconds || !pm.getGamer(event.getEntity()).isAlive())
                event.setCancelled(true);
        } else if (event.getEntity() instanceof Tameable && ((Tameable) event.getEntity()).isTamed()
                && hg.currentTime <= config.getInvincibilityTime())
            event.setCancelled(true);
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        if (hg.currentTime >= 0)
            event.setMotd(cm.getGameStartedMotd());
        else
            event.setMotd(returnTime(hg.currentTime));
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

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (hg.currentTime < 0)
            event.setCancelled(true);
        else if (event.getTarget() instanceof Player && !pm.getGamer((Player) event.getTarget()).isAlive())
            event.setCancelled(true);
    }

    @EventHandler
    public void pigZap(PigZapEvent event) {
        event.setCancelled(true);
    }

    private String returnTime(Integer i) {
        i = Math.abs(i);
        int remainder = i % 3600, minutes = remainder / 60, seconds = remainder % 60;
        if (seconds == 0 && minutes == 0)
            return cm.getTimeFormatNoTime();
        if (minutes == 0) {
            if (seconds == 1)
                return String.format(cm.getTimeFormatMotdSecond(), seconds);
            return String.format(cm.getTimeFormatMotdSeconds(), seconds);
        }
        if (seconds == 0) {
            if (minutes == 1)
                return String.format(cm.getTimeFormatMotdMinute(), minutes);
            return String.format(cm.getTimeFormatMotdMinutes(), minutes);
        }
        if (seconds == 1) {
            if (minutes == 1)
                return String.format(cm.getTimeFormatMotdSecondAndMinute(), minutes, seconds);
            return String.format(cm.getTimeFormatMotdSecondAndMinutes(), minutes, seconds);
        }
        if (minutes == 1) {
            return String.format(cm.getTimeFormatMotdSecondsAndMinute(), minutes, seconds);
        }
        return String.format(cm.getTimeFormatMotdSecondsAndMinutes(), minutes, seconds);
    }

}
