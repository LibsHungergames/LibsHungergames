package me.libraryaddict.Hungergames.Abilities;

import java.util.Random;

import org.bukkit.event.EventHandler;

import me.libraryaddict.Hungergames.Events.PlayerTrackEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;

public class Bourne extends AbilityListener implements Disableable {
    public int bourneRange = 15;
    public int compassModifier = 10;

    @EventHandler
    public void onTrack(PlayerTrackEvent event) {
        if (event.getVictim() != null && hasAbility(event.getVictim())
                && event.getTracker().getPlayer().getLocation().distance(event.getLocation()) <= compassModifier) {
            event.setLocation(event.getLocation().add(new Random().nextInt((compassModifier * 2) + 1) - compassModifier,
                    new Random().nextInt((compassModifier * 2) + 1) - compassModifier,
                    new Random().nextInt((compassModifier * 2) + 1) - compassModifier));
        }
    }

}
