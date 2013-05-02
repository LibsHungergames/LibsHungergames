package me.libraryaddict.Hungergames.Types;

import me.libraryaddict.Hungergames.Managers.ChatManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * User: Austin Date: 4/22/13 Time: 11:04 PM (c) lazertester
 */
public abstract class AbilityListener implements Listener {

    private transient Set<String> myPlayers = new HashSet<String>();

    public void registerPlayer(Player player) {
        myPlayers.add(player.getName());
    }

    public void unregisterPlayer(Player player) {
        myPlayers.remove(player.getName());
    }

    /**
     * 
     * Is this items displayname set by the plugin and matches this. Aka. It
     * checks the displayname has a chatcolor in it. Unsettable by the client.
     * Then it compares the stripped colors to the string fed.
     */
    public boolean isSpecialItem(ItemStack item, String displayName) {
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String itemName = item.getItemMeta().getDisplayName();
            if (!itemName.equals(ChatColor.stripColor(itemName))
                    && ChatColor.stripColor(itemName).equals(ChatColor.stripColor(displayName))) {
                return true;
            }
        }
        return false;
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
        ChatManager cm = HungergamesApi.getChatManager();
        boolean modified = false;
        for (Field field : getClass().getDeclaredFields()) {
            if (!Modifier.isTransient(field.getModifiers()) && Modifier.isPublic(field.getModifiers()))
                try {
                    Object value = section.get(field.getName());
                    if (value == null) {
                        value = field.get(this);
                        if (value instanceof String) {
                            value = ((String) value).replace("\n", "\\n").replace("§", "&");
                        }
                        if (field.getType().isArray() && value.getClass() == ArrayList.class) {
                            List<Object> array = (List<Object>) value;
                            String[] strings = array.toArray(new String[array.size()]);
                            for (int i = 0; i < strings.length; i++)
                                strings[i] = strings[i].replace("\n", "\\n").replace("§", "&");
                            value = strings;
                        }
                        section.set(field.getName(), value);
                        modified = true;
                    } else if (field.getType().isArray() && value.getClass() == ArrayList.class) {
                        List<Object> array = (List<Object>) value;
                        value = array.toArray(new String[array.size()]);
                    }
                    if (value instanceof String) {
                        value = ((String) value).replace("\\n", "\n").replace("&", "§");
                    }
                    if (value instanceof String[]) {
                        String[] strings = (String[]) value;
                        for (int i = 0; i < strings.length; i++)
                            strings[i] = strings[i].replace("\\n", "\n").replace("&", "§");
                        value = strings;
                    }
                    if (field.getType().getSimpleName().equals("float") && value.getClass() == Double.class) {
                        field.set(this, ((float) (double) (Double) value));
                    } else
                        field.set(this, value);
                } catch (Exception e) {
                    System.out.print(String.format(cm.getLoggerErrorWhileLoadingAbility(), e.getMessage()));
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
