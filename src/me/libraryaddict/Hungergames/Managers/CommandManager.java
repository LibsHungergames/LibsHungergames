package me.libraryaddict.Hungergames.Managers;

import me.libraryaddict.Hungergames.Configs.LoggerConfig;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Utilities.ClassGetter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Austin Date: 11/7/12 Time: 12:04 PM
 */
public class CommandManager {
    private LoggerConfig cm = HungergamesApi.getConfigManager().getLoggerConfig();
    private Map<String, Map<String, Object>> commandsMap = new HashMap<String, Map<String, Object>>();
    private YamlConfiguration config;
    private File configFile;
    private boolean newFile = false;

    public CommandManager() {
        loadCommands(HungergamesApi.getHungergames(), "me.libraryaddict.Hungergames.Commands");
    }

    private void addCreatorAliases(List<String> list, String commandName) {
        for (String string : new String[] { "author", "maker", "coder", "download", "hungergames", "creator" })
            if (!commandName.equalsIgnoreCase(string) && !list.contains(string))
                list.add(string);
    }

    private ConfigurationSection getConfigSection(String commandName) {
        ConfigurationSection section = config.getConfigurationSection(commandName);
        if (section == null) {
            section = config.createSection(commandName);
        }
        return section;
    }

    private void load(File file) {
        configFile = file;
        try {
            config = new YamlConfiguration();
            if (!configFile.exists()) {
                newFile = true;
                save();
            } else
                newFile = false;
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean loadCommand(JavaPlugin owningPlugin, CommandExecutor exc, boolean save) {
        File newFile = new File(owningPlugin.getDataFolder(), "commands.yml");
        if (configFile == null || !configFile.equals(newFile)) {
            load(newFile);
        }
        boolean modified = false;
        if (exc instanceof AbilityListener) {
            try {
                Method field = exc.getClass().getMethod("getCommands");
                if (field != null) {
                    String[] commands = (String[]) field.invoke(exc);
                    for (String command : commands) {
                        boolean modify = startRegisteringCommand(exc, command);
                        if (!modified)
                            modified = modify;
                    }
                }
            } catch (Exception ex) {
            }
            try {
                Method field = exc.getClass().getMethod("getCommand");
                String command = (String) field.invoke(exc);
                if (command != null) {
                    boolean modify = startRegisteringCommand(exc, command);
                    if (!modified)
                        modified = modify;
                }
            } catch (Exception ex) {
            }
        } else {
            modified = startRegisteringCommand(exc, exc.getClass().getSimpleName().replace("Command", ""));
        }
        // System.out.print(String.format(cm.getLoggerFoundCommandInPackage(),
        // commandName));
        if (save && modified)
            save();
        HungergamesApi
                .getReflectionManager()
                .getCommandMap()
                .registerAll(HungergamesApi.getHungergames().getDescription().getName(),
                        PluginCommandYamlParser.parse(HungergamesApi.getHungergames()));
        return modified;
    }

    private void loadCommands(JavaPlugin plugin, String packageName) {
        boolean saveConfig = false;
        System.out.print(String.format(cm.getLoadCommandsPackage(), plugin.getName(), packageName));
        try {
            Field commands = plugin.getDescription().getClass().getDeclaredField("commands");
            commands.setAccessible(true);
            commands.set(plugin.getDescription(), commandsMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (Class commandClass : ClassGetter.getClassesForPackage(plugin, packageName)) {
            if (CommandExecutor.class.isAssignableFrom(commandClass)) {
                try {
                    CommandExecutor commandListener = (CommandExecutor) commandClass.newInstance();
                    final boolean modified = loadCommand(plugin, commandListener, false);
                    if (modified)
                        saveConfig = true;
                } catch (Exception e) {
                    System.out
                            .print(String.format(cm.getErrorWhileLoadingCommand(), commandClass.getSimpleName(), e.getMessage()));
                }
            }
        }
        if (saveConfig)
            save();
    }

    private boolean loadConfig(ConfigurationSection section, CommandExecutor exc, String commandName) {
        try {
            boolean modified = false;
            if (!section.contains("CommandName")) {
                modified = true;
                section.set("CommandName", commandName);
            }
            if (!section.contains("EnableCommand")) {
                modified = true;
                section.set("EnableCommand", true);
            }
            for (Field field : exc.getClass().getFields()) {
                if (!Modifier.isTransient(field.getModifiers())) {
                    try {
                        Object value = section.get(field.getName());
                        if (value == null) {
                            value = field.get(exc);
                            if (value instanceof String[]) {
                                String[] strings = (String[]) value;
                                String[] newStrings = new String[strings.length];
                                for (int i = 0; i < strings.length; i++) {
                                    newStrings[i] = strings[i].replace("\n", "\\n").replace("§", "&").toLowerCase();
                                }
                                section.set(field.getName(), newStrings);
                            } else if (value instanceof String) {
                                value = ((String) value).replace("\n", "\\n").replace("§", "&");
                                section.set(field.getName(), value);
                            } else if (value instanceof ItemStack) {
                                section.set(field.getName(), translateItemTo((ItemStack) value));
                            } else if (value instanceof ItemStack[]) {
                                ItemStack[] items = (ItemStack[]) value;
                                ItemStack[] newItems = new ItemStack[items.length];
                                for (int i = 0; i < items.length; i++) {
                                    newItems[i] = translateItemTo(items[i]);
                                }
                                section.set(field.getName(), newItems);
                            } else {
                                section.set(field.getName(), value);
                            }
                            modified = true;
                            if (!newFile)
                                System.out.print(String.format(cm.getAddedMissingConfigValue(), field.getName(), commandName));
                        } else if (field.getType().isArray() && value.getClass() == ArrayList.class) {
                            List<Object> array = (List<Object>) value;
                            Object[] newArray = (Object[]) Array.newInstance(((Object[]) field.get(exc))[0].getClass(),
                                    array.size());
                            value = array.toArray(newArray);
                        }
                        if (value instanceof ItemStack) {
                            value = translateItemFrom((ItemStack) value);
                        } else if (value instanceof ItemStack[]) {
                            ItemStack[] items = (ItemStack[]) value;
                            for (int i = 0; i < items.length; i++) {
                                items[i] = translateItemFrom(items[i]);
                            }
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
                            field.set(exc, ((float) (double) (Double) value));
                        } else
                            field.set(exc, value);
                    } catch (Exception e) {
                        System.out.print(String.format(cm.getErrorWhileLoadingConfig(), "commands", exc.getClass()
                                .getSimpleName() + " - " + e.getMessage()));
                        e.printStackTrace();
                    }
                }
            }
            return modified;
        } catch (Exception e) {
            System.out.print(String.format(cm.getErrorWhileLoadingConfig(), "commands", e.getMessage()));
        }
        return false;
    }

    private ItemStack translateItemTo(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            item = item.clone();
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                meta.setDisplayName(meta.getDisplayName().replace("§", "&"));
            }
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                ArrayList<String> newLore = new ArrayList<String>();
                for (int i = 0; i < lore.size(); i++) {
                    newLore.add(lore.get(i).replace("§", "&"));
                }
                meta.setLore(newLore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack translateItemFrom(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            item = item.clone();
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', meta.getDisplayName()));
            }
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                for (int i = 0; i < lore.size(); i++) {
                    lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
                }
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private void registerCommand(final String name, final CommandExecutor exc, final JavaPlugin plugin, boolean isAlias)
            throws Exception {
        String desc = null;
        if (!isAlias) {
            List<String> aliases = new ArrayList<String>();
            try {
                Field field = exc.getClass().getDeclaredField("aliases");
                if (field.get(exc) instanceof String[]) {
                    aliases = new ArrayList<String>(Arrays.asList((String[]) field.get(exc)));
                }
            } catch (Exception ex) {
            }
            if (exc.getClass().getSimpleName().equalsIgnoreCase("CreatorCommand")) {
                addCreatorAliases(aliases, name);
            }
            for (String alias : aliases) {
                registerCommand(alias, exc, plugin, true);
            }
        }
        try {
            Field field = exc.getClass().getDeclaredField("description");
            desc = ChatColor.translateAlternateColorCodes('&', (String) field.get(exc));
        } catch (Exception ex) {
        }
        HashMap<String, Object> newMap = new HashMap<String, Object>();
        if (desc != null) {
            newMap.put("description", desc);
        }
        commandsMap.put(name.toLowerCase(), newMap);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                PluginCommand command = plugin.getCommand(name.toLowerCase());
                if (command != null) {
                    command.setExecutor(exc);
                } else {
                    System.out.print(String.format(HungergamesApi.getConfigManager().getLoggerConfig()
                            .getErrorWhileLoadingCommand(), name, "Can't register command"));
                }
            }
        });
    }

    private void save() {
        try {
            if (!configFile.exists()) {
                System.out.print(String.format(cm.getCreatingConfigFile(), "commands"));
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                newFile = true;
            } else
                newFile = false;
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean startRegisteringCommand(CommandExecutor exc, String commandName) {
        ConfigurationSection section = getConfigSection(commandName);
        boolean modified = loadConfig(section, exc, commandName);
        if (section.getBoolean("EnableCommand") || exc.getClass().getSimpleName().equals("CreatorCommand")) {
            try {
                registerCommand(section.getString("CommandName"), exc, HungergamesApi.getHungergames(), false);
            } catch (Exception ex) {
                System.out
                        .print(String.format(cm.getErrorWhileLoadingCommand(), exc.getClass().getSimpleName(), ex.getMessage()));
            }
        }
        return modified;
    }
}
