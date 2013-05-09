package me.libraryaddict.Hungergames.Abilities;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Kit;

public class Surprise extends AbilityListener implements Disableable {
    public String[] badKits = new String[] { "Surprise" };
    public String messageOnGive = ChatColor.BLUE + "You were given the kit %s!";
    public String messageWhenNoKitFound = ChatColor.BLUE + "Unable to find you a kit to play with!";
    public boolean onlyKitsYouOwn = false;

    private Kit getViableKit(Player player) {
        KitManager kits = HungergamesApi.getKitManager();
        ArrayList<Kit> randomKit = new ArrayList<Kit>();
        for (Kit kit : kits.getKits()) {
            if ((!onlyKitsYouOwn || kits.ownsKit(player, kit)) && !isBadKit(kit)) {
                randomKit.add(kit);
            }
        }
        if (randomKit.size() > 0)
            return randomKit.get(new Random().nextInt(randomKit.size()));
        return null;
    }

    private boolean isBadKit(Kit kit) {
        for (String kitName : badKits)
            if (kit.getName().equalsIgnoreCase(kitName))
                return true;
        return false;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameStart(GameStartEvent event) {
        for (Player p : getMyPlayers()) {
            Kit kit = getViableKit(p);
            if (kit != null) {
                HungergamesApi.getKitManager().setKit(p, kit.getName());
                kit.giveKit(p);
                p.sendMessage(String.format(messageOnGive, kit.getName()));
            } else
                p.sendMessage(messageWhenNoKitFound);
        }
    }
}
