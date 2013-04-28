package me.libraryaddict.Hungergames.Types;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * User: Austin Date: 4/22/13 Time: 11:04 PM (c) lazertester
 */
public abstract class AbilityListener implements Listener {

    // RNG for all abilitylisteners to use for convenience
    public static transient Random random = new Random();

    private transient Set<String> myPlayers = new HashSet<String>();

    public void registerPlayer(Player player) {
        myPlayers.add(player.getName());
    }

    public void unregisterPlayer(Player player) {
        myPlayers.remove(player.getName());
    }

    public List<Player> getMyPlayers() {
        List<Player> playerList = new ArrayList<Player>();
        for (String name : myPlayers) {
            final Player player = Bukkit.getPlayer(name);
            if (player != null)
                playerList.add(player);
        }
        return playerList;
    }

    public boolean hasAbility(Player player) {
        return hasAbility(player.getName());
    }

    public boolean hasAbility(String name) {
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
                    if (field.getType().getSimpleName().equals("float") && value.getClass() == Double.class) {
                        double d = (Double) value;
                        field.set(this, ((float) d));
                    } else
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
