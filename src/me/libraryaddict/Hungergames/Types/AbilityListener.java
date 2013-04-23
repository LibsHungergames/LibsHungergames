package me.libraryaddict.Hungergames.Types;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Austin Date: 4/22/13 Time: 11:04 PM (c) lazertester
 */
public abstract class AbilityListener implements Listener {

    private transient Set<String> myPlayers = new HashSet<String>();

    public void registerPlayer(String name) {
        myPlayers.add(name);
    }

    public void unregisterPlayer(String name) {
        myPlayers.remove(name);
    }

    public boolean hasThisAbility(Player player) {
        return hasThisAbility(player.getName());
    }

    public boolean hasThisAbility(String name) {
        return myPlayers.contains(name);
    }

    public boolean load(ConfigurationSection section) {
        boolean modified = false;
        for (Field field : getClass().getDeclaredFields()) {
            if (!Modifier.isTransient(field.getModifiers()))
                try {
                    Object value = section.get(field.getName());
                    if (value == null) {
                        value = field.get(this);
                        section.set(field.getName(), field.get(this));
                        modified = true;
                    }
                    field.set(this, value);
                } catch (IllegalAccessException ignored) {
                }
        }
        return modified;
    }

    // Just in case an abilitylistener also implements commandexecutor and has a
    // command to claim.
    public String getCommand() {
        return null;
    }
}
