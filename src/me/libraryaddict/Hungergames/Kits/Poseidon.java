package me.libraryaddict.Hungergames.Kits;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Poseidon implements Listener {
    ArrayList<Player> waterBreathers = new ArrayList<Player>();

    private KitManager kits = HungergamesApi.getKitManager();

    public Poseidon() {
        for (Player p : Bukkit.getOnlinePlayers())
            if (kits.hasAbility(p, "Poseidon")) {
                waterBreathers.add(p);
            }
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
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 1));
            }
        }
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        waterBreathers.remove(event.getKilled().getPlayer());
    }

}
