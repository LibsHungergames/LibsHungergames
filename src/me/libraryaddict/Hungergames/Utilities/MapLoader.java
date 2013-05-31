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
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.ConfigManager;
import me.libraryaddict.Hungergames.Managers.TranslationManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class MapLoader {

    public static void loadMap() {
        Hungergames hg = HungergamesApi.getHungergames();
        File mapConfig = new File(hg.getDataFolder() + "/map.yml");
        try {
            if (!mapConfig.exists())
                mapConfig.createNewFile();
            YamlConfiguration config = YamlConfiguration.loadConfiguration(mapConfig);
            if (!config.contains("DeleteWorld")) {
                config.set("DeleteWorld", true);
                config.save(mapConfig);
            }
            if (!config.contains("MapPath")) {
                config.set("MapPath", hg.getDataFolder().getAbsoluteFile().getParent() + "/Maps/");
                config.save(mapConfig);
            }
            if (!config.contains("UseMaps")) {
                config.set("LoadMap", false);
                config.save(mapConfig);
            }
            String worldName = ((CraftServer) hg.getServer()).getServer().getPropertyManager().getString("level-name", "world");
            File worldFolder = new File(hg.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().toString() + "/"
                    + worldName);
            if (config.getBoolean("DeleteWorld"))
                clear(worldFolder);
            if (config.getBoolean("UseMaps")) {
                File mapFolder = worldFolder.getParentFile();
                loadMap(mapFolder, worldFolder, config);
                loadMapConfiguration(worldFolder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadMapConfiguration(File worldConfig) {
        ConfigManager configManager = HungergamesApi.getConfigManager();
        try {
            if (!worldConfig.exists())
                return;
            YamlConfiguration config = YamlConfiguration.loadConfiguration(worldConfig);
            if (config.contains("BorderSize"))
                configManager.setBorderSize(config.getDouble("BorderSize"));
        } catch (Exception ex) {

        }
    }

    private static void loadMap(File mapDir, File dest, YamlConfiguration config) {
        TranslationManager tm = HungergamesApi.getTranslationManager();
        System.out.print(String.format(tm.getLoggerNowAttemptingToLoadAMap(), dest.toString()));
        List<File> maps = new ArrayList<File>();
        if (dest.exists()) {
            for (File file : dest.listFiles())
                if (file.isDirectory()) {
                    if (new File(file.toString() + "/level.dat").exists())
                        maps.add(file);
                }
        }
        if (maps.size() > 0) {
            Collections.shuffle(maps, new Random());
            File toLoad = maps.get(0);
            copy(toLoad, dest);
            System.out.print(String.format(HungergamesApi.getTranslationManager().getLoggerSucessfullyLoadedMap(),
                    toLoad.getName()));
        } else
            System.out.print(String.format(HungergamesApi.getTranslationManager().getLoggerNoMapsFound(), mapDir.toString()));
    }

    public static void copyFile(File source, File destination) throws IOException {
        if (source.getName().equalsIgnoreCase("uid.dat"))
            return;
        if (destination.isDirectory())
            destination = new File(destination, source.getName());
        FileInputStream input = new FileInputStream(source);
        copyFile(input, destination);
    }

    public static void copyFile(InputStream input, File destination) throws IOException {
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

    public static void copy(File from, File dest) {
        if (from.isFile()) {
            try {
                copyFile(from, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            for (File f : from.listFiles())
                copy(f, dest);
    }

    public static void clear(File file) {
        if (file.isFile())
            file.delete();
        else
            for (File f : file.listFiles())
                clear(f);
    }

}
