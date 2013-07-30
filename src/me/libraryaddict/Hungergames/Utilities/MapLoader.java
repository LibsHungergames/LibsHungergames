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

import org.bukkit.configuration.file.YamlConfiguration;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.ConfigManager;
import me.libraryaddict.Hungergames.Managers.TranslationManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class MapLoader {

    private static void clear(File file) {
        if (file.isFile())
            file.delete();
        else {
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
            if (!config.contains("SpawnPlatformIDandData")) {
                config.set("SpawnPlatformIDandData", "GRASS 0");
                config.save(mapConfig);
            }
            if (!config.contains("RoundedBorder")) {
                config.set("RoundedBorder", false);
                config.save(mapConfig);
            }
            if (!config.contains("BorderSize")) {
                config.set("BorderSize", 500);
                config.save(mapConfig);
            }
            if (!config.contains("BorderCloseInRate")) {
                config.set("BorderCloseInRate", 0.2);
                config.save(mapConfig);
            }
            if (!config.contains("TimeOfDayWhenGameStarts")) {
                config.set("TimeOfDayWhenGameStarts", 0);
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
        TranslationManager tm = HungergamesApi.getTranslationManager();
        System.out.print(String.format(tm.getLoggerNowAttemptingToLoadAMap(), mapDir.toString()));
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
            System.out.print(String.format(HungergamesApi.getTranslationManager().getLoggerSucessfullyLoadedMap(),
                    toLoad.getName()));
        } else
            System.out.print(String.format(HungergamesApi.getTranslationManager().getLoggerNoMapsFound(), mapDir.toString()));
    }

    private static void loadMapConfiguration(File worldConfig) {
        ConfigManager configManager = HungergamesApi.getConfigManager();
        TranslationManager tm = HungergamesApi.getTranslationManager();
        try {
            System.out.print(tm.getLoggerMapConfigNowLoading());
            YamlConfiguration config = null;
            if (!worldConfig.exists()) {
                System.out.print(tm.getLoggerMapConfigNotFound());
            } else
                config = YamlConfiguration.loadConfiguration(worldConfig);
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new File(HungergamesApi.getHungergames()
                    .getDataFolder(), "map.yml"));
            if (config != null && config.contains("BorderSize")) {
                configManager.setBorderSize(config.getInt("BorderSize"));
                System.out.print(String.format(tm.getLoggerMapConfigChangedBorderSize(), config.getInt("BorderSize")));
            } else
                configManager.setBorderSize(defaultConfig.getInt("BorderSize"));
            if (config != null && config.contains("RoundedBorder")) {
                configManager.setRoundedBorder(config.getBoolean("RoundedBorder"));
                System.out.print(String.format(tm.getLoggerMapConfigChangedRoundedBorder(), config.getBoolean("RoundedBorder")));
            } else
                configManager.setRoundedBorder(defaultConfig.getBoolean("RoundedBorder"));
            if (config != null && config.contains("BorderCloseInRate")) {
                configManager.setBorderCloseInRate(config.getDouble("BorderCloseInRate"));
                System.out.print(String.format(tm.getLoggerMapConfigChangedBorderCloseInRate(),
                        config.getDouble("BorderCloseInRate")));
            } else
                configManager.setBorderCloseInRate(defaultConfig.getDouble("BorderCloseInRate"));
            if (config != null && config.contains("TimeOfDayWhenGameStarts")) {
                configManager.setTimeOfDay(config.getInt("TimeOfDayWhenGameStarts"));
                System.out
                        .print(String.format(tm.getLoggerMapConfigChangedTimeOfDay(), config.getInt("TimeOfDayWhenGameStarts")));
            } else
                configManager.setTimeOfDay(defaultConfig.getInt("TimeOfDayWhenGameStarts"));
            if (config != null)
                System.out.print(tm.getLoggerMapConfigLoaded());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
