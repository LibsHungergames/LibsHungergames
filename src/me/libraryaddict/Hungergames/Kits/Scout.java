package me.libraryaddict.Hungergames.Kits;

import me.libraryaddict.Hungergames.Types.Extender;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Scout extends Extender implements Listener {

    public Scout() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(hg, new Runnable() {
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers())
                    if (kits.hasAbility(p, "Scout"))
                        kits.addItem(p, new ItemStack(Material.POTION, 2, (short) 16418));
            }
        }, 10 * 60 * 20, 10 * 60 * 20);
    }
}
