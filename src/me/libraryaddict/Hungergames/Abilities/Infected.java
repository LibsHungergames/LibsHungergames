package me.libraryaddict.Hungergames.Abilities;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseTypes.Disguise;
import me.libraryaddict.disguise.DisguiseTypes.DisguiseType;
import me.libraryaddict.disguise.DisguiseTypes.FlagWatcher;
import me.libraryaddict.disguise.DisguiseTypes.MobDisguise;

public class Infected extends AbilityListener implements Disableable {
    public Infected() throws Exception {
        if (Bukkit.getPluginManager().getPlugin("LibsDisguises") == null)
            throw new Exception(String.format(HungergamesApi.getTranslationManager().getLoggerDependencyNotFound(),
                    "Plugin LibsDisguises"));
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null)
            throw new Exception(String.format(HungergamesApi.getTranslationManager().getLoggerDependencyNotFound(),
                    "Plugin ProtocolLib"));
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (hasAbility(event.getPlayer())) {
            if (event.isSneaking()) {
                Disguise disguise = new MobDisguise(DisguiseType.ZOMBIE);
                DisguiseAPI.disguiseToAll(event.getPlayer(), disguise);
                FlagWatcher watcher = disguise.getWatcher();
                watcher.setSneaking(true);
                watcher.setSneaking(false);
                for (int i = 0; i < 4; i++)
                    watcher.setItemStack(i, new ItemStack(0));
            } else {
                DisguiseAPI.undisguiseToAll(event.getPlayer());
            }
        }
    }
}
