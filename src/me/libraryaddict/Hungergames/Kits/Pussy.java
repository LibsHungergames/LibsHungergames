package me.libraryaddict.Hungergames.Kits;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Pussy implements Listener {
    DisguiseCraftAPI dcAPI = DisguiseCraft.getAPI();

    HashMap<Player, Integer> pussys = new HashMap<Player, Integer>();

    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private KitManager kits = HungergamesApi.getKitManager();
    private Hungergames hg = HungergamesApi.getHungergames();

    @EventHandler
    public void onSprint(PlayerToggleSprintEvent event) {
        final Player p = event.getPlayer();
        if (kits.hasAbility(p, "Pussy") && (pm.getGamer(p).isAlive())) {
            if (event.isSprinting()) {
                int id = Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
                    public void run() {
                        p.sendMessage(ChatColor.BLUE + "Meow!");
                        dcAPI.disguisePlayer(p, new Disguise(dcAPI.newEntityID(), DisguiseType.Ocelot));
                    }
                }, 10 * 20);
                pussys.put(p, id);
            } else if (pussys.containsKey(p)) {
                p.sendMessage(ChatColor.BLUE + "HISS!");
                Bukkit.getScheduler().cancelTask(pussys.remove(p));
                if (dcAPI.isDisguised(p))
                    dcAPI.undisguisePlayer(p);
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
