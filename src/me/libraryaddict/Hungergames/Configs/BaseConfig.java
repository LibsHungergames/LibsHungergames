package me.libraryaddict.Hungergames.Configs;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import lombok.Data;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.RandomItem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

@Data
public abstract class BaseConfig {
    private YamlConfiguration config;
    private File configFile;
    private String configName;
    protected HashSet<String> dontSave = new HashSet<String>();
    private boolean newFile;

    public BaseConfig(String configName) {
        this.configName = configName;
        dontSave("dontSave", "configFile", "config", "newFile", "configName");
        config = new YamlConfiguration();
        configFile = new File(HungergamesApi.getHungergames().getDataFolder(), configName + ".yml");
    }

    // TODO Give a 'what this should be cast to' to avoid errors
    protected Object deserialize(Object object) {
        if (object instanceof String) {
            return ChatColor.translateAlternateColorCodes('&', (String) object).replace("\\n", "\n");
        } else if (object instanceof String[]) {
            String[] array = (String[]) object;
            String[] returns = new String[array.length];
            for (int i = 0; i < returns.length; i++) {
                returns[i] = ChatColor.translateAlternateColorCodes('&', array[i]).replace("\\n", "\n");
            }
            return returns;
        } else if (object instanceof MemorySection) {
            MemorySection map = (MemorySection) object;
            HashMap newMap = new HashMap();
            for (String obj : map.getKeys(false)) {
                if (map.get(obj) instanceof String) {
                    String s = ChatColor.translateAlternateColorCodes('&', (String) map.get(obj)).replace("\\n", "\n");
                    newMap.put(Integer.parseInt(obj), s);
                } else if (map.get(obj) instanceof Serializable) {
                    newMap.put(Integer.parseInt(obj), map.get(obj));
                } else
                    return object;
            }
            return newMap;
        } else if (object instanceof ArrayList) {
            ArrayList array = (ArrayList) object;
            if (!array.isEmpty() && array.get(0).getClass() == RandomItem.class)
                return object;
            return array.toArray(new String[array.size()]);
        }
        return object;
    }

    /**
     * Adds the fields to not be saved when the config is being saved.
     */
    protected void dontSave(String... fieldNames) {
        for (String fieldName : fieldNames)
            dontSave.add(fieldName);
    }

    public void load() throws Exception {
        try {
            if (!configFile.exists())
                save();
            else
                newFile = false;
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(
                    "You have setup your translation configuration wrong. Please make sure you are properly setting up every single line"
                            + "\nProblems can be because of single quotes in the middle of the string, Not surrounding the string with single quotes."
                            + "\nFor more information, Please look up yaml configurations and how to properly do them");
        }
    }

    public void loadConfig() {
        try {
            boolean saveConfig = false;
            LoggerConfig loggerConfig = HungergamesApi.getConfigManager().getLoggerConfig();
            System.out.print(String.format(loggerConfig.getLoadingConfigFile(), this.configName));
            try {
                boolean modified = false;
                for (Field field : getClass().getDeclaredFields()) {
                    if (this.dontSave.contains(field.getName()))
                        continue;
                    field.setAccessible(true);
                    try {
                        Object value = config.get(field.getName());
                        if (value == null) {
                            value = field.get(this);
                            config.set(field.getName(), serialize(value));
                            modified = true;
                            if (!newFile) {
                                System.out.print(String.format(loggerConfig.getAddedMissingConfigValue(), field.getName(),
                                        this.getConfigName()));
                            }
                        } else {
                            value = deserialize(value);
                            if (field.getType().getSimpleName().equals("float") && value.getClass() == Double.class) {
                                field.set(this, ((float) (double) (Double) value));
                            } else {
                                field.set(this, deserialize(value));
                            }
                        }
                    } catch (Exception e) {
                        System.out.print(String.format(loggerConfig.getErrorWhileLoadingConfigValue(), this.getConfigName(),
                                field.getName(), e.getMessage()));
                    }
                }
                if (modified) {
                    saveConfig = true;
                }
            } catch (Exception e) {
                System.out.print(String.format(loggerConfig.getErrorWhileLoadingConfig(), getConfigName(), e.getMessage()));
            }
            if (saveConfig)
                save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            if (!configFile.exists()) {
                Bukkit.getLogger().info(
                        String.format(HungergamesApi.getConfigManager().getLoggerConfig().getCreatingConfigFile(),
                                getConfigName()));
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                newFile = true;
            }
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Object serialize(Object object) {
        if (object instanceof String) {
            return ((String) object).replace("\n", "\\n").replace("§", "&");
        } else if (object instanceof String[]) {
            String[] array = (String[]) object;
            String[] returns = new String[array.length];
            for (int i = 0; i < returns.length; i++) {
                returns[i] = array[i].replace("\n", "\\n").replace("§", "&");
            }
            return returns;
        } else if (object instanceof HashMap) {
            HashMap map = (HashMap) object;
            HashMap newMap = new HashMap();
            for (Object obj : map.keySet()) {
                if (map.get(obj) instanceof String) {
                    String s = ((String) map.get(obj)).replace("\n", "\\n").replace("§", "&");
                    newMap.put(obj, s);
                } else
                    return object;
            }
            return newMap;
        }
        return object;
    }
}
