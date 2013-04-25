package me.libraryaddict.Hungergames.Abilities;

import java.util.Random;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.event.EventHandler;

public class Creeper extends AbilityListener {

    public float baseExplosionStrength = 0.4F;
    public boolean randomizeExplosion = true;
    public int randomizedStrength = 3;

    @EventHandler
    public void onExplode(PlayerKilledEvent event) {
        if (hasAbility(event.getKilled().getPlayer())) {
            float strength = (randomizeExplosion ? new Random().nextInt(randomizedStrength + 1) : 0) + baseExplosionStrength;
            event.getDropsLocation().getWorld().createExplosion(event.getDropsLocation(), strength);
        }
    }

}
