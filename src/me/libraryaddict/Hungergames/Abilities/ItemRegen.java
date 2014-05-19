package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Kit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class ItemRegen extends AbilityListener implements Disableable {
    public String[] itemsToRegen = new String[] { "SecondsBetweenItems KitName ItemType ItemDurability ItemAmount ItemData",
            "300 libraryaddict BOOK 0 1 Unlootable 1 Name=Explosive_Reading" };

    @EventHandler
    public void onExplode(TimeSecondEvent event) {
        for (String string : itemsToRegen) {
            String[] split = string.split(" ");
            try {
                if (Integer.parseInt(split[0]) != HungergamesApi.getHungergames().currentTime)
                    continue;
            } catch (Exception ex) {
                continue;
            }
            ItemStack[] items = HungergamesApi.getKitManager().parseItem(
                    string.substring(2 + split[1].length() + split[0].length()));
            for (Player player : this.getMyPlayers()) {
                Kit kit = HungergamesApi.getKitManager().getKitByPlayer(player);
                if (kit != null) {
                    if (string.equalsIgnoreCase(kit.getSafeName())) {
                        for (ItemStack item : items) {
                            HungergamesApi.getKitManager().addItem(player, item);
                        }
                    }
                }
            }
        }
    }

}
