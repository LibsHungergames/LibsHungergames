package me.libraryaddict.Hungergames.Managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Austin
 * Date: 11/7/12
 * Time: 12:04 PM
 */
public class AbilityConfigManager {
    private File configFile;
    private YamlConfiguration config;

    public AbilityConfigManager(JavaPlugin parent) {
        parent.getDataFolder().mkdir();
        configFile = new File(parent.getDataFolder(), "abilities.yml");
        Bukkit.getLogger().info("Creating config file");
        config = new YamlConfiguration();

        try {
            config.load(configFile);
            save();
        } catch (FileNotFoundException e) {
            try {
                configFile.createNewFile();
                save();
            } catch (IOException x) {
                x.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            configFile.createNewFile();
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationSection getConfigSection(String abilityName) {
        ConfigurationSection section = config.getConfigurationSection(abilityName);
        if(section == null)
        {
            section = config.createSection(abilityName);
        }
        return section;
    }
}
