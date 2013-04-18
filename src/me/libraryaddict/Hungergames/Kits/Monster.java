package me.libraryaddict.Hungergames.Kits;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class Monster implements Listener {

    private KitManager kits = HungergamesApi.getKitManager();

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && kits.hasAbility((Player) event.getTarget(), "Monster")
                && event.getReason() != TargetReason.TARGET_ATTACKED_OWNER && event.getReason() != TargetReason.PIG_ZOMBIE_TARGET
                && event.getReason() != TargetReason.TARGET_ATTACKED_ENTITY
                && event.getReason() != TargetReason.PIG_ZOMBIE_TARGET && event.getReason() != TargetReason.OWNER_ATTACKED_TARGET)
            event.setCancelled(true);
    }
}
