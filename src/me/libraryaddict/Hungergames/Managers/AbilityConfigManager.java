package me.libraryaddict.Hungergames.Managers;

import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

/**
 * User: Austin Date: 11/7/12 Time: 12:04 PM
 */
public class AbilityConfigManager {
    private File configFile;
    private YamlConfiguration config;
    private ChatManager cm = HungergamesApi.getChatManager();
    private boolean newFile = false;

    public AbilityConfigManager() {
        configFile = new File(HungergamesApi.getHungergames().getDataFolder(), "abilities.yml");
        config = new YamlConfiguration();
        load();
    }

    public void load() {
        try {
            if (!configFile.exists())
                save();
            else
                newFile = false;
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            if (!configFile.exists()) {
                Bukkit.getLogger().info(cm.getLoggerCreatingAbilitysConfig());
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                newFile = true;
            }
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationSection getConfigSection(String abilityName) {
        ConfigurationSection section = config.getConfigurationSection(abilityName);
        if (section == null) {
            section = config.createSection(abilityName);
        }
        return section;
    }

    public boolean isNewFile() {
        return newFile;
    }
}
