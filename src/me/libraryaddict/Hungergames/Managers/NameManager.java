package me.libraryaddict.Hungergames.Managers;

import java.io.File;
import java.io.IOException;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class NameManager {

    private YamlConfiguration config;
    private File configFile;

    public NameManager() {
        configFile = new File(HungergamesApi.getHungergames().getDataFolder(), "names.yml");
        config = new YamlConfiguration();
        load();
    }

    public String getEnchantName(Enchantment enchant) {
        return getName(enchant.getName());
    }

    public String getItemName(ItemStack item) {
        if (item == null)
            item = new ItemStack(0);
        if (config.contains("" + item.getType().getId()))
            return config.getString("" + item.getType().getId());
        return getName(item.getType().name());
    }

    public String getName(String string) {
        if (config.contains(string))
            return config.getString(string);
        return toReadable(string);
    }

    public void load() {
        try {
            if (!configFile.exists())
                save();
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            if (!configFile.exists()) {
                System.out.print(String.format(HungergamesApi.getConfigManager().getLoggerConfig().getCreatingConfigFile(),
                        "names"));
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                config.set("Material-ID-or-Name-In-UpperCase", "Name as you want it to display");
                config.set("AIR", "fist");
                config.set(Enchantment.DAMAGE_ALL.getName(), "Sharpness");
                config.set(Enchantment.ARROW_FIRE.getName(), "Flame");
                config.set(Enchantment.ARROW_INFINITE.getName(), "Infinite Arrows");
                config.set(Enchantment.ARROW_DAMAGE.getName(), "Power");
                config.set(Enchantment.ARROW_KNOCKBACK.getName(), "Punch");
                config.set(Enchantment.DAMAGE_ARTHROPODS.getName(), "Bane of Arthropods");
                config.set(Enchantment.DAMAGE_UNDEAD.getName(), "Smite");
                config.set(Enchantment.LOOT_BONUS_MOBS.getName(), "Looting");
                config.set(Enchantment.LOOT_BONUS_BLOCKS.getName(), "Fortune");
                config.set(Enchantment.WATER_WORKER.getName(), "Aqua Affinity");
                config.set(Enchantment.OXYGEN.getName(), "Respiration");
                config.set(Enchantment.DIG_SPEED.getName(), "Efficency");
                config.set(Enchantment.PROTECTION_ENVIRONMENTAL.getName(), "Protection");
                config.set(Enchantment.PROTECTION_FALL.getName(), "Feather Falling");
                config.set(Enchantment.PROTECTION_EXPLOSIONS.getName(), "Blast Protection");
                config.set(Enchantment.PROTECTION_PROJECTILE.getName(), "Projectile Protection");
                config.set(Enchantment.PROTECTION_FIRE.getName(), "Fire Protection");
                config.set(EnchantmentManager.UNLOOTABLE.getName(), "Unlootable");
                config.set(EnchantmentManager.UNDROPPABLE.getName(), "Undroppable");
                for (EntityType type : EntityType.values())
                    config.set(type.name(), toReadable(type.name()));
            }
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toReadable(String string) {
        String[] names = string.split("_");
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].substring(0, 1) + names[i].substring(1).toLowerCase();
        }
        return StringUtils.join(names, " ");
    }

}
