package me.libraryaddict.Hungergames.Managers;

import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * User: Austin Date: 11/7/12 Time: 12:04 PM
 */
public class AbilityConfigManager {
    private boolean newFile = false;

    public ConfigurationSection getConfigSection(YamlConfiguration config, String abilityName) {
        ConfigurationSection section = config.getConfigurationSection(abilityName);
        if (section == null) {
            section = config.createSection(abilityName);
        }
        return section;
    }

    public boolean isNewFile() {
        return newFile;
    }

    public YamlConfiguration load(JavaPlugin plugin) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            File configFile = new File(plugin.getDataFolder(), "abilities.yml");
            if (!configFile.exists()) {
                save(plugin, config);
            } else
                newFile = false;
            config.load(configFile);
            return config;
        } catch (Exception e) {
            newFile = false;
            e.printStackTrace();
            System.out
                    .print("You have setup your ability configuration wrong. Please make sure you are properly setting up every single line"
                            + "\nProblems can be because of single quotes in the middle of the string, Not surrounding the string with single quotes."
                            + "\nFor more information, Please look up yaml configurations and how to properly do them");

        }
        return null;
    }

    public void save(JavaPlugin plugin, YamlConfiguration config) {
        try {
            File configFile = new File(plugin.getDataFolder(), "abilities.yml");
            if (!configFile.exists()) {
                Bukkit.getLogger().info(
                        String.format(HungergamesApi.getConfigManager().getLoggerConfig().getCreatingConfigFile(), "abilities"));
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                newFile = true;
            } else
                newFile = false;
            config.save(configFile);
        } catch (IOException e) {
            newFile = false;
            e.printStackTrace();
        }
    }
}
