package me.libraryaddict.Hungergames.Abilities;

import java.util.Random;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.event.EventHandler;

public class Creeper extends AbilityListener {


    @EventHandler
    public void onExplode(PlayerKilledEvent event) {
        if (hasThisAbility(event.getKilled().getPlayer())) {
            float strength = new Random().nextInt(4) + 0.1F;
            event.getDropsLocation().getWorld().createExplosion(event.getDropsLocation(), strength);
        }
    }

}
