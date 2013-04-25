package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class Monster extends AbilityListener {
    public boolean completelyUntargetable = false;

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player
                && hasAbility((Player) event.getTarget())
                && ((completelyUntargetable) || (event.getReason() != TargetReason.TARGET_ATTACKED_OWNER
                        && event.getReason() != TargetReason.TARGET_ATTACKED_ENTITY && event.getReason() != TargetReason.OWNER_ATTACKED_TARGET)))
            event.setCancelled(true);
    }
}
