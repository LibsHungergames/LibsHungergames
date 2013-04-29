package me.libraryaddict.Hungergames.Abilities;

import java.util.ArrayList;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;

public class Poseidon extends AbilityListener {
    ArrayList<Player> waterBreathers = new ArrayList<Player>();
    public int potionMultiplier = 1;

    @EventHandler
    public void gameStart(GameStartEvent event) {
        for (Player p : Bukkit.getOnlinePlayers())
            if (hasAbility(p))
                waterBreathers.add(p);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (waterBreathers.contains(event.getPlayer()) && event.getPlayer().getLocation().getBlock().isLiquid())
            event.getPlayer().setRemainingAir(200);
    }

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        for (Player p : waterBreathers) {
            if (p.getLocation().getBlock().isLiquid()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, potionMultiplier));
            }
        }
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        waterBreathers.remove(event.getKilled().getPlayer());
    }

}
