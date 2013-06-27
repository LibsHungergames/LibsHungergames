package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Ninja extends AbilityListener implements Disableable {
    public String[] potionEffectsDay = new String[] { "SPEED 12000 0" };
    public String[] potionEffectsNight = new String[] { "NIGHT_VISION 12000 0" };
    private int scheduler = -1;

    @EventHandler
    public void gameStartEvent(GameStartEvent event) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HungergamesApi.getHungergames(), getRunnable(), 0, 12000);
    }

    private Runnable getRunnable() {
        return new Runnable() {
            public void run() {
                for (Player p : getMyPlayers()) {
                    if (HungergamesApi.getHungergames().world.getTime() > 0
                            && HungergamesApi.getHungergames().world.getTime() <= 12000) {
                        for (String string : potionEffectsDay) {
                            String[] effect = string.split(" ");
                            PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(effect[0].toUpperCase()),
                                    Integer.parseInt(effect[1]), Integer.parseInt(effect[2]));
                            p.addPotionEffect(potionEffect, true);
                        }
                    } else {
                        for (String string : potionEffectsNight) {
                            String[] effect = string.split(" ");
                            PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(effect[0].toUpperCase()),
                                    Integer.parseInt(effect[1]), Integer.parseInt(effect[2]));
                            p.addPotionEffect(potionEffect, true);
                        }
                    }
                }
            }
        };
    }

    public void registerPlayer(Player player) {
        super.registerPlayer(player);
        if (scheduler < 0 && HungergamesApi.getHungergames().currentTime >= 0)
            scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(HungergamesApi.getHungergames(), getRunnable(), 0,
                    12000 - (HungergamesApi.getHungergames().world.getTime() % 12000));
    }
}
