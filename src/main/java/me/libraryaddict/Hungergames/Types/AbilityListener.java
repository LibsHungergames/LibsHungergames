package me.libraryaddict.Hungergames.Types;

import me.libraryaddict.Hungergames.Configs.LoggerConfig;
import me.libraryaddict.Hungergames.Interfaces.Disableable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * User: Austin Date: 4/22/13 Time: 11:04 PM (c) lazertester
 */
public abstract class AbilityListener implements Listener {

    protected transient Set<String> myPlayers = new HashSet<String>();

    // Just in case an abilitylistener also implements commandexecutor and has a
    // command to claim.
    public String getCommand() {
        return null;
    }

    // Does the same thing, but is for more then one command
    public String[] getCommands() {
        return new String[0];
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

    /**
     * Is this items displayname set by the plugin and matches this. Aka. It checks the displayname has a chatcolor in it.
     * Unsettable by the client. Then it compares the stripped colors to the string fed.
     */
    public boolean isSpecialItem(ItemStack item, String displayName) {
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String itemName = item.getItemMeta().getDisplayName();
            String stripped = ChatColor.stripColor(itemName);
            if (!itemName.equals(stripped) && stripped.equals(ChatColor.stripColor(displayName))) {
                return true;
            }
        }
        return false;
    }

    public boolean load(ConfigurationSection section) {
        return load(section, true);
    }

    public boolean load(ConfigurationSection section, boolean isNewFile) {
        LoggerConfig cm = HungergamesApi.getConfigManager().getLoggerConfig();
        boolean modified = false;
        for (Field field : getClass().getDeclaredFields()) {
            if (!Modifier.isTransient(field.getModifiers()) && Modifier.isPublic(field.getModifiers()))
                try {
                    Object value = section.get(field.getName());
                    if (value == null) {
                        value = field.get(this);
                        if (value instanceof String[]) {
                            String[] strings = (String[]) value;
                            String[] newStrings = new String[strings.length];
                            for (int i = 0; i < strings.length; i++) {
                                newStrings[i] = strings[i].replace("\n", "\\n").replace("§", "&");
                            }
                            section.set(field.getName(), newStrings);
                        } else {
                            if (value instanceof String)
                                value = ((String) value).replace("\n", "\\n").replace("§", "&");
                            section.set(field.getName(), value);
                        }
                        modified = true;
                        if (!isNewFile)
                            System.out.print(String.format(cm.getAbilityMissingConfigValue(), getClass().getSimpleName(),
                                    field.getName()));
                    } else if (field.getType().isArray() && value.getClass() == ArrayList.class) {
                        List<Object> array = (List<Object>) value;
                        value = array.toArray(new String[array.size()]);
                    }
                    if (value instanceof String) {
                        value = ChatColor.translateAlternateColorCodes('&', (String) value).replace("\\n", "\n");
                    }
                    if (value instanceof String[]) {
                        String[] strings = (String[]) value;
                        for (int i = 0; i < strings.length; i++)
                            strings[i] = ChatColor.translateAlternateColorCodes('&', strings[i]).replace("\\n", "\n");
                        value = strings;
                    }
                    if (field.getType().getSimpleName().equals("float") && value.getClass() == Double.class) {
                        field.set(this, ((float) (double) (Double) value));
                    } else
                        field.set(this, value);
                } catch (Exception e) {
                    System.out.print(String.format(cm.getErrorWhileLoadingAbility(), getClass().getSimpleName(), e.getMessage()));
                }
        }
        return modified;
    }

    public void registerPlayer(Player player) {
        if (HungergamesApi.getHungergames().currentTime >= 0 && this instanceof Disableable && myPlayers.size() == 0) {
            Bukkit.getPluginManager().registerEvents(this, HungergamesApi.getHungergames());
        }
        myPlayers.add(player.getName());
    }

    public void unregisterPlayer(Player player) {
        myPlayers.remove(player.getName());
        if (HungergamesApi.getHungergames().currentTime >= 0 && this instanceof Disableable && myPlayers.size() == 0) {
            HandlerList.unregisterAll(this);
        }
    }
}
