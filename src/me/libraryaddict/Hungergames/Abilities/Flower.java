package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class Flower extends AbilityListener implements Disableable {
    private int currentSecond = 0;
    public int repairsEveryWhatSecond = 5;

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        long time = HungergamesApi.getHungergames().world.getTime();
        currentSecond++;
        if (repairsEveryWhatSecond <= currentSecond) {
            currentSecond = 0;
            if (time >= 0 && time <= 12000) {
                for (Player p : getMyPlayers()) {
                    Location loc = p.getLocation();
                    if (p.getWorld().getHighestBlockAt(loc.getBlockX(), loc.getBlockZ()).getY() <= p.getLocation().getY()) {
                        boolean refresh = false;
                        for (ItemStack item : p.getInventory().getArmorContents()) {
                            if (repairItem(item))
                                refresh = true;
                        }
                        if (refresh)
                            p.getInventory().setArmorContents(p.getInventory().getArmorContents());
                        for (int i = 0; i < p.getInventory().getSize(); i++) {
                            if (repairItem(p.getInventory().getItem(i)))
                                p.getInventory().setItem(i, p.getInventory().getItem(i));
                        }
                    }
                }
            }
        }
    }

    private boolean repairItem(ItemStack item) {
        if (item != null && item.getType().getMaxStackSize() == 1 && item.getType().getMaxDurability() > 16
                && item.getDurability() > 0) {
            item.setDurability((short) (item.getDurability() - 1));
            return true;
        }
        return false;
    }
}
