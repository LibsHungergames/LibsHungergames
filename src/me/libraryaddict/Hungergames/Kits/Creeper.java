package me.libraryaddict.Hungergames.Kits;

import java.util.Random;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Creeper implements Listener {
    private KitManager kits = HungergamesApi.getKitManager();

    @EventHandler
    public void onExplode(PlayerKilledEvent event) {
        if (kits.hasAbility(event.getKilled().getPlayer(), "Creeper")) {
            float strength = new Random().nextInt(4) + 0.1F;
            event.getDropsLocation().getWorld().createExplosion(event.getDropsLocation(), strength);
        }
    }

}
