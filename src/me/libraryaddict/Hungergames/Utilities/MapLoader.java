package me.libraryaddict.Hungergames.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Configs.LoggerConfig;
import me.libraryaddict.Hungergames.Configs.MainConfig;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class MapLoader {

    public static void clear(File file) {
        if (!file.exists())
            return;
        if (file.isFile()) {
            file.delete();
        } else {
            for (File f : file.listFiles())
                clear(f);
            file.delete();
        }
    }

    private static void copy(File from, File dest) {
        if (from.isFile()) {
            try {
                copyFile(from, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            for (File f : from.listFiles())
                copy(f, new File(dest.toString(), from.getName()));
    }

    private static void copyFile(File source, File destination) throws IOException {
        destination.mkdirs();
        if (destination.isDirectory())
            destination = new File(destination, source.getName());
        if (source.getName().equalsIgnoreCase("uid.dat"))
            return;
        FileInputStream input = new FileInputStream(source);
        copyFile(input, destination);
    }

    private static void copyFile(InputStream input, File destination) throws IOException {
        OutputStream output = null;
        output = new FileOutputStream(destination);
        byte[] buffer = new byte[1024];
        int bytesRead = input.read(buffer);
        while (bytesRead >= 0) {
            output.write(buffer, 0, bytesRead);
            bytesRead = input.read(buffer);
        }
        input.close();
        output.close();
    }

    public static void loadMap() {
        Hungergames hg = HungergamesApi.getHungergames();
        File mapConfig = new File(hg.getDataFolder() + "/map.yml");
        try {
            if (!mapConfig.exists())
                mapConfig.createNewFile();
            YamlConfiguration config = YamlConfiguration.loadConfiguration(mapConfig);
            if (!config.contains("MapPath")) {
                config.set("MapPath", hg.getDataFolder().getAbsoluteFile().getParentFile().getParent() + "/Maps/");
                config.save(mapConfig);
            }
            if (!config.contains("UseMaps")) {
                config.set("UseMaps", false);
                config.save(mapConfig);
            }
            if (!config.contains("DeleteMap")) {
                config.set("DeleteMap", true);
                config.save(mapConfig);
            }
            if (!config.contains("GenerateChunks")) {
                config.set("GenerateChunks", false);
                config.save(mapConfig);
            }
            if (!config.contains("GenerateSpawnPlatform")) {
                config.set("GenerateSpawnPlatform", false);
                config.save(mapConfig);
            }
            if (!config.contains("SpawnPlatformSize")) {
                config.set("SpawnPlatformSize", 30);
                config.save(mapConfig);
            }
            if (!config.contains("SpawnPlatformBlock")) {
                config.set("SpawnPlatformBlock", new ItemStack(Material.GRASS, 1, (short) 0));
                config.save(mapConfig);
            }
            if (!config.contains("GenerateChunksBackground")) {
                config.set("GenerateChunksBackground", true);
                config.save(mapConfig);
            }
            String worldName = (String) HungergamesApi.getReflectionManager().getPropertiesConfig("level-name", "world");
            File worldFolder = new File(hg.getDataFolder().getAbsoluteFile().getParentFile().getParent().toString() + "/"
                    + worldName);
            if (!worldFolder.exists())
                worldFolder.mkdirs();
            if (config.getBoolean("DeleteMap") || config.getBoolean("UseMaps")) {
                clear(worldFolder);
            }
            if (config.getBoolean("UseMaps")) {
                File mapFolder = new File(config.getString("MapPath"));
                loadMap(mapFolder, worldFolder, config);
            }
            loadMapConfiguration(new File(worldFolder, "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadMap(File mapDir, File dest, YamlConfiguration config) {
        LoggerConfig tm = HungergamesApi.getConfigManager().getLoggerConfig();
        System.out.print(String.format(tm.getNowAttemptingToLoadAMap(), mapDir.toString()));
        List<File> maps = new ArrayList<File>();
        if (mapDir.exists()) {
            for (File file : mapDir.listFiles()) {
                if (file.isDirectory()) {
                    if (new File(file.toString() + "/level.dat").exists())
                        maps.add(file);
                }
            }
        }
        if (maps.size() > 0) {
            Collections.shuffle(maps, new Random());
            File toLoad = maps.get(0);
            for (File f : toLoad.listFiles())
                copy(f, dest);
            System.out.print(String.format(tm.getSuccessfullyLoadedMap(), toLoad.getName()));
        } else
            System.out.print(String.format(tm.getNoMapsFound(), mapDir.toString()));
    }

    private static void loadMapConfiguration(File worldConfig) {
        MainConfig configManager = HungergamesApi.getConfigManager().getMainConfig();
        LoggerConfig tm = HungergamesApi.getConfigManager().getLoggerConfig();
        try {
            System.out.print(tm.getMapConfigNowLoading());
            YamlConfiguration config = null;
            if (!worldConfig.exists()) {
                System.out.print(tm.getMapConfigNotFound());
            } else
                config = YamlConfiguration.loadConfiguration(worldConfig);
            if (config != null && config.contains("BorderSize")) {
                configManager.setBorderSize(config.getInt("BorderSize"));
                System.out.print(String.format(tm.getMapConfigChangedBorderSize(), config.getInt("BorderSize")));
            }
            if (config != null && config.contains("RoundedBorder")) {
                configManager.setRoundedBorder(config.getBoolean("RoundedBorder"));
                System.out.print(String.format(tm.getMapConfigChangedRoundedBorder(), config.getBoolean("RoundedBorder")));
            }
            if (config != null && config.contains("BorderCloseInRate")) {
                configManager.setAmountBorderClosesInPerSecond(config.getDouble("BorderCloseInRate"));
                System.out.print(String.format(tm.getMapConfigChangedBorderCloseInRate(), config.getDouble("BorderCloseInRate")));
            }
            if (config != null && config.contains("TimeOfDayWhenGameStarts")) {
                configManager.setTimeOfDay(config.getInt("TimeOfDayWhenGameStarts"));
                System.out.print(String.format(tm.getMapConfigChangedTimeOfDay(), config.getInt("TimeOfDayWhenGameStarts")));
            }
            File spawnsFile = new File(worldConfig.getParentFile(), "spawns.yml");
            System.out.print(tm.getLoadSpawnsConfig());
            if (spawnsFile.exists()) {
                config = YamlConfiguration.loadConfiguration(spawnsFile);
                int i = 0;
                for (String key : config.getKeys(true)) {
                    try {
                        ConfigurationSection section = config.getConfigurationSection(key);
                        int x, y, z, radius, height;
                        if (section.contains("X"))
                            x = section.getInt("X");
                        else {
                            System.out.print(String.format(tm.getLoadSpawnsConfigError(), key, "X"));
                            continue;
                        }
                        if (section.contains("Y"))
                            y = section.getInt("Y");
                        else {
                            System.out.print(String.format(tm.getLoadSpawnsConfigError(), key, "Y"));
                            continue;
                        }
                        if (section.contains("Z"))
                            z = section.getInt("Z");
                        else {
                            System.out.print(String.format(tm.getLoadSpawnsConfigError(), key, "Z"));
                            continue;
                        }
                        if (section.contains("Radius"))
                            radius = section.getInt("Radius");
                        else {
                            System.out.print(String.format(tm.getLoadSpawnsConfigError(), key, "Radius"));
                            continue;
                        }
                        if (section.contains("Height"))
                            height = section.getInt("Height");
                        else {
                            System.out.print(String.format(tm.getLoadSpawnsConfigError(), key, "Height"));
                            continue;
                        }
                        Location loc = new Location(HungergamesApi.getHungergames().world, x, y, z);
                        HungergamesApi.getPlayerManager().addSpawnPoint(loc, radius, height);
                        i++;
                    } catch (Exception ex) {
                        System.out.print(String.format(tm.getLoadSpawnsConfigError(), key, ex.getMessage()));
                    }
                }
                System.out.print(String.format(tm.getLoadedSpawnsConfig(), i));
            } else
                System.out.print(tm.getLoadSpawnsConfigNotFound());
            if (config != null)
                System.out.print(tm.getMapConfigLoaded());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
