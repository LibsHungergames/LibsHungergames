package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Ninja extends AbilityListener implements Disableable {
    public String[] potionEffectsDuringDay = new String[] { "SPEED 0" };
    public String[] potionEffectsDuringNight = new String[] { "NIGHT_VISION 0" };
    private int scheduler = -1;

    @EventHandler
    public void gameStartEvent(GameStartEvent event) {
        getRunnable().run();
        registerScheduler();
    }

    private Runnable getRunnable() {
        return new Runnable() {
            public void run() {
                World world = HungergamesApi.getHungergames().world;
                int timeTillNextSwitch = (int) (12000 - (world.getTime() % 12000));
                for (Player p : getMyPlayers()) {
                    if (world.getTime() >= 0 && world.getTime() < 12000) {
                        for (String string : potionEffectsDuringDay) {
                            String[] effect = string.split(" ");
                            PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(effect[0].toUpperCase()),
                                    timeTillNextSwitch, Integer.parseInt(effect[1]));
                            p.addPotionEffect(potionEffect, true);
                        }
                    } else {
                        for (String string : potionEffectsDuringNight) {
                            String[] effect = string.split(" ");
                            PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(effect[0].toUpperCase()),
                                    timeTillNextSwitch, Integer.parseInt(effect[1]));
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
            registerScheduler();
    }

    private void registerScheduler() {
        int timeTillNextSwitch = (int) (12000 - (HungergamesApi.getHungergames().world.getTime() % 12000));
        scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(HungergamesApi.getHungergames(), getRunnable(),
                timeTillNextSwitch, 12000);
    }

    public void unregisterPlayer(Player player) {
        super.unregisterPlayer(player);
        if (scheduler >= 0 && getMyPlayers().isEmpty()) {
            Bukkit.getScheduler().cancelTask(scheduler);
            scheduler = -1;
        }
    }
}
