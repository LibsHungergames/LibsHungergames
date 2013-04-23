package me.libraryaddict.Hungergames.Managers;

import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Utilities.ClassGetter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * User: Austin Date: 4/22/13 Time: 11:03 PM (c) lazertester
 */
public class AbilityManager {

    private HashMap<String, AbilityListener> abilities = new HashMap<String, AbilityListener>();
    private HashMap<String, List<String>> playerAbilities = new HashMap<String, List<String>>();
    private AbilityConfigManager abilityConfigManager;

    public AbilityManager(AbilityConfigManager abilityConfigManager) {
        this.abilityConfigManager = abilityConfigManager;
        boolean saveConfig = false;
        for (Class abilityClass : ClassGetter.getClassesForPackage("me.libraryaddict.Hungergames.Abilities")) {
            if (AbilityListener.class.isAssignableFrom(abilityClass)) {
                try {
                    Bukkit.getLogger().info("[HungerGames] Found ability " + abilityClass.getSimpleName());
                    AbilityListener abilityListener = (AbilityListener) abilityClass.newInstance();
                    final boolean modified = abilityListener.load(abilityConfigManager.getConfigSection(abilityClass
                            .getSimpleName()));
                    if (modified)
                        saveConfig = true;
                    if (abilityListener instanceof CommandExecutor && abilityListener.getCommand() != null) {
                        HungergamesApi.getHungergames().getCommand(abilityListener.getCommand())
                                .setExecutor((CommandExecutor) abilityListener);
                    }
                    abilities.put(abilityClass.getSimpleName(), abilityListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // if(saveConfig)
        abilityConfigManager.save();
    }

    public void addAbility(String name, AbilityListener abilityListener) {
        abilities.put(name, abilityListener);
    }

    public void registerAbilityListeners() {
        for (AbilityListener abilityListener : abilities.values()) {
            Bukkit.getPluginManager().registerEvents(abilityListener, HungergamesApi.getHungergames());
        }
    }

    public AbilityListener getAbility(String abilityName) {
        return abilities.get(abilityName);
    }

    public List<String> getPlayerAbilities(String name) {
        if (!playerAbilities.containsKey(name))
            playerAbilities.put(name, new ArrayList<String>());
        return playerAbilities.get(name);
    }

    public void unregisterPlayer(String name) {
        List<String> abilitiesCopyList = new ArrayList<String>();
        abilitiesCopyList.addAll(getPlayerAbilities(name));
        for (String abilityName : abilitiesCopyList)
            unregisterPlayerAbility(name, abilityName);
        playerAbilities.remove(name);
    }

    public void unregisterPlayerAbility(String name, String abilityName) {
        final AbilityListener abilityListener = getAbility(abilityName);
        if (abilityListener != null)
            abilityListener.unregisterPlayer(name);
        getPlayerAbilities(name).remove(abilityName);
    }

    public void registerPlayerAbility(String name, String abilityName) {
        final AbilityListener abilityListener = getAbility(abilityName);
        if (abilityListener != null) {
            abilityListener.registerPlayer(name);
        } else
            Bukkit.getLogger().info(
                    "[HungerGames] Tried to register " + name + " for the " + abilityName + " ability but it does not exist.");
        getPlayerAbilities(name).add(abilityName);
    }

    public AbilityConfigManager getAbilityConfigManager() {
        return abilityConfigManager;
    }
}
