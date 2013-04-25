package me.libraryaddict.Hungergames.Abilities;

import java.util.HashMap;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.api.MobDisguise;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Pussy extends AbilityListener {
    private transient HashMap<Player, Integer> pussys = new HashMap<Player, Integer>();
    public int timeToTransform = 10;

    public Pussy() throws Exception {
        if (Bukkit.getPluginManager().getPlugin("iDisguise") == null)
            throw new Exception("iDisguise not found");
    }

    @EventHandler
    public void onSprint(PlayerToggleSprintEvent event) {
        final Player p = event.getPlayer();
        if (hasAbility(p)) {
            if (event.isSprinting()) {
                int id = Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                    public void run() {
                        p.sendMessage(ChatColor.BLUE + "Meow!");
                        DisguiseAPI.disguiseToAll(p, new MobDisguise(EntityType.OCELOT, true));
                    }
                }, timeToTransform * 20);
                pussys.put(p, id);
            } else if (pussys.containsKey(p)) {
                Bukkit.getScheduler().cancelTask(pussys.remove(p));
                if (DisguiseAPI.isDisguised(p)) {
                    p.sendMessage(ChatColor.BLUE + "HISS!");
                    DisguiseAPI.undisguiseToAll(p);
                }
            }
        }
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        Player p = event.getKilled().getPlayer();
        if (pussys.containsKey(p)) {
            Bukkit.getScheduler().cancelTask(pussys.remove(p));
        }
    }

}
