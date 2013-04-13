package me.libraryaddict.Hungergames.Kits;

import java.util.Random;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.Extender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Creeper extends Extender implements Listener {

    @EventHandler
    public void onExplode(PlayerKilledEvent event) {
        if (kits.hasAbility(event.getKilled().getPlayer(), "Creeper")) {
            float strength = new Random().nextInt(4) + 0.1F;
            event.getDropsLocation().getWorld().createExplosion(event.getDropsLocation(), strength);
        }
    }

}
