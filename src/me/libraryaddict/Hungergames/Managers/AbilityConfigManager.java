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
    private TranslationManager cm = HungergamesApi.getTranslationManager();
    private YamlConfiguration config;
    private File configFile;
    private boolean newFile = false;

    public AbilityConfigManager() {
        configFile = new File(HungergamesApi.getHungergames().getDataFolder(), "abilities.yml");
        config = new YamlConfiguration();
        load();
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

    public void load() {
        try {
            if (!configFile.exists())
                save();
            else
                newFile = false;
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.out
                    .print("You have setup your ability configuration wrong. Please make sure you are properly setting up every single line"
                            + "\nProblems can be because of single quotes in the middle of the string, Not surrounding the string with single quotes."
                            + "\nFor more information, Please look up yaml configurations and how to properly do them");

        }
    }

    public void save() {
        try {
            if (!configFile.exists()) {
                Bukkit.getLogger().info(cm.getLoggerCreatingAbilitiesConfig());
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                newFile = true;
            }
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
