package me.libraryaddict.Hungergames.Abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Kit;

public class PotionLover extends AbilityListener implements Disableable {

    public String[] potionEffects = new String[] { "Ghost INVISIBILITY 2600 0" };

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        KitManager kits = HungergamesApi.getKitManager();
        for (String effect : potionEffects) {
            String[] split = effect.split(" ");
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(split[1].toUpperCase()),
                    Integer.parseInt(split[2]), Integer.parseInt(split[3]));
            for (Player p : getMyPlayers()) {
                Kit kit = kits.getKitByPlayer(p);
                if (kit.getName().equals(split[0])) {
                    p.addPotionEffect(potionEffect);
                }
            }
        }
    }
}
