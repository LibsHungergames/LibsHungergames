package me.libraryaddict.Hungergames.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Configs.LoggerConfig;
import me.libraryaddict.Hungergames.Configs.MainConfig;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class MapLoader {
    @Getter
    private static Entry<Material, Byte> borderBlock;
    @Getter
    private static int borderCheckSize;
    @Getter
    private static boolean isBorderParticles;
    @Getter
    private static boolean realBlocks;
    @Getter
    private static boolean setBorderBlocks;

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
            if (!config.contains("DeleteMap")) {
                config.set("DeleteMap", true);
                config.save(mapConfig);
            }
            if (!config.contains("GenerateChunks")) {
                config.set("GenerateChunks", false);
                config.save(mapConfig);
            }
            if (!config.contains("GenerateChunksBackground")) {
                config.set("GenerateChunksBackground", true);
                config.save(mapConfig);
            }
            if (!config.contains("GenerateSpawnPlatform")) {
                config.set("GenerateSpawnPlatform", false);
                config.save(mapConfig);
            }
            if (!config.contains("MapPath")) {
                config.set("MapPath", hg.getDataFolder().getAbsoluteFile().getParentFile().getParent() + "/Maps/");
                config.save(mapConfig);
            }
            if (!config.contains("UseMaps")) {
                config.set("UseMaps", false);
                config.save(mapConfig);
            }
            if (!config.contains("SpawnPlatformBlock")) {
                config.set("SpawnPlatformBlock", new ItemStack(Material.GRASS, 1, (short) 0));
                config.save(mapConfig);
            }
            if (!config.contains("SpawnPlatformSize")) {
                config.set("SpawnPlatformSize", 30);
                config.save(mapConfig);
            }
            if (!config.contains("Border.Particles")) {
                config.set("Border.Particles", true);
                config.save(mapConfig);
            }
            if (!config.contains("Border.Blocks")) {
                config.set("Border.Blocks", true);
                config.save(mapConfig);
            }
            if (!config.contains("Border.Block")) {
                config.set("Border.Block", "GLASS:0");
                config.save(mapConfig);
            }
            if (!config.contains("Border.CheckSize")) {
                config.set("Border.CheckSize", 5);
                config.save(mapConfig);
            }
            if (!config.contains("Border.RealBlocks")) {
                config.set("Border.RealBlocks", true);
                config.save(mapConfig);
            }
            isBorderParticles = config.getBoolean("Border.Particles");
            setBorderBlocks = config.getBoolean("Border.Blocks");
            borderCheckSize = config.getInt("Border.CheckSize");
            realBlocks = config.getBoolean("Border.RealBlocks");
            String str = config.getString("Border.Block");
            String[] spl = str.split(":");
            Material mat = Material.GLASS;
            byte b = (byte) 0;
            try {
                mat = Material.getMaterial(Integer.parseInt(spl[0]));
            } catch (NumberFormatException ex) {
                mat = Material.valueOf(spl[0].toUpperCase());
            }
            if (spl.length > 1) {
                b = Byte.parseByte(spl[1]);
            }
            borderBlock = new HashMap.SimpleEntry(mat, b);
            String worldToUse = HungergamesApi.getReflectionManager().getPropertiesConfig("level-name", "world");
            if (HungergamesApi.getConfigManager().getMainConfig().isUseOwnWorld()) {
                worldToUse = "LibsHungergamesWorld";
                final String oldWorldName = worldToUse;
                HungergamesApi.getReflectionManager().setPropertiesConfig("level-name", "LibsHungergamesWorld");
                HungergamesApi.getReflectionManager().savePropertiesConfig();
                Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                    public void run() {
                        HungergamesApi.getReflectionManager().setPropertiesConfig("level-name", oldWorldName);
                        HungergamesApi.getReflectionManager().savePropertiesConfig();
                    }
                }, 2);
            }
            File worldFolder = new File(worldToUse);
            if (!worldFolder.exists())
                worldFolder.mkdirs();
            if (config.getBoolean("DeleteMap") || config.getBoolean("UseMaps")) {
                clear(worldFolder);
            }
            if (config.getBoolean("UseMaps")) {
                File mapFolder = new File(config.getString("MapPath")).getAbsoluteFile();
                loadMap(mapFolder, worldFolder, config);
            }
            loadMapConfiguration(new File(worldFolder, "config.yml"), config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadMap(File mapDir, File dest, YamlConfiguration config) throws IOException {
        LoggerConfig tm = HungergamesApi.getConfigManager().getLoggerConfig();
        System.out.print(String.format(tm.getNowAttemptingToLoadAMap(), mapDir.toString()));
        List<File> maps = new ArrayList<File>();
        String dontLoad = config.getString("LastMapUsed", null);
        File toRemove = null;
        if (mapDir.exists()) {
            for (File file : mapDir.listFiles()) {
                if (file.isDirectory()) {
                    if (new File(file, "level.dat").exists()) {
                        maps.add(file);
                        if (dontLoad != null && dontLoad.equals(file.getName())) {
                            toRemove = file;
                        }
                    }
                }
            }
        }
        if (maps.size() > 0) {
            if (maps.size() > 1 && toRemove != null) {
                maps.remove(toRemove);
            }
            File toLoad = maps.get(new Random().nextInt(maps.size()));
            config.set("LastMapUsed", toLoad.getName());
            config.save(new File(HungergamesApi.getHungergames().getDataFolder(), "map.yml"));
            for (File f : toLoad.listFiles()) {
                String name = f.getName();
                if (name.equalsIgnoreCase("config.yml") || name.equalsIgnoreCase("region") || name.equalsIgnoreCase("level.dat")
                        || name.equalsIgnoreCase("data") || name.equalsIgnoreCase("map.yml")
                        || name.equalsIgnoreCase("spawns.yml"))
                    copy(f, dest);
            }
            System.out.print(String.format(tm.getSuccessfullyLoadedMap(), toLoad.getName()));
        } else
            System.out.print(String.format(tm.getNoMapsFound(), mapDir.toString()));
    }

    private static void loadMapConfiguration(File worldConfig, YamlConfiguration config2) {
        MainConfig configManager = HungergamesApi.getConfigManager().getMainConfig();
        LoggerConfig tm = HungergamesApi.getConfigManager().getLoggerConfig();
        try {
            System.out.print(tm.getMapConfigNowLoading());
            YamlConfiguration config = null;
            if (!worldConfig.exists()) {
                System.out.print(tm.getMapConfigNotFound());
            } else
                config = YamlConfiguration.loadConfiguration(worldConfig);
            if (config != null) {
                if (config.contains("BorderSize")) {
                    configManager.setBorderSize(config.getInt("BorderSize"));
                    System.out.print(String.format(tm.getMapConfigChangedBorderSize(), config.getInt("BorderSize")));
                }
                if (config.contains("FeastCenterX") && config.contains("FeastCenterZ")) {
                    HungergamesApi.getConfigManager().getFeastConfig().setFeastCenterX(config.getInt("FeastCenterX"));
                    HungergamesApi.getConfigManager().getFeastConfig().setFeastCenterZ(config.getInt("FeastCenterZ"));
                    System.out.print(String.format(tm.getMapConfigChangedFeastInformation(), config.getInt("FeastCenterX"),
                            config.getInt("FeastCenterZ")));
                }
                if (config.contains("BorderCloseInRate")) {
                    configManager.setAmountBorderClosesInPerSecond(config.getDouble("BorderCloseInRate"));
                    System.out.print(String.format(tm.getMapConfigChangedBorderCloseInRate(),
                            config.getDouble("BorderCloseInRate")));
                }
                if (config.contains("TimeOfDayWhenGameStarts")) {
                    configManager.setTimeOfDay(config.getInt("TimeOfDayWhenGameStarts"));
                    System.out.print(String.format(tm.getMapConfigChangedTimeOfDay(), config.getInt("TimeOfDayWhenGameStarts")));
                }
                isBorderParticles = config.getBoolean("Border.Particles", config2.getBoolean("Border.Particles"));
                setBorderBlocks = config.getBoolean("Border.Blocks", config2.getBoolean("Border.Blocks"));
                borderCheckSize = config.getInt("Border.CheckSize", config2.getInt("Border.CheckSize"));
                realBlocks = config.getBoolean("Border.RealBlocks", config2.getBoolean("Border.RealBlocks"));
                String str = config.getString("Border.Block", config2.getString("Border.Block"));
                String[] spl = str.split(":");
                Material mat = Material.GLASS;
                byte b = (byte) 0;
                try {
                    mat = Material.getMaterial(Integer.parseInt(spl[0]));
                } catch (NumberFormatException ex) {
                    mat = Material.valueOf(spl[0].toUpperCase());
                }
                if (spl.length > 1) {
                    b = Byte.parseByte(spl[1]);
                }
                borderBlock = new HashMap.SimpleEntry(mat, b);
            }
            File spawnsFile = new File(worldConfig.getParentFile(), "spawns.yml");
            System.out.print(tm.getLoadSpawnsConfig());
            if (spawnsFile.exists()) {
                config = YamlConfiguration.loadConfiguration(spawnsFile);
                int i = 0;
                for (String spawnName : config.getKeys(false)) {
                    try {
                        ConfigurationSection section = config.getConfigurationSection(spawnName);
                        int radius = 0, height = 0;
                        double x = Integer.MAX_VALUE, y = Integer.MAX_VALUE, z = Integer.MAX_VALUE;
                        float pitch = 0, yaw = 0;
                        for (String key : section.getKeys(false)) {
                            if (key.equalsIgnoreCase("x")) {
                                x = section.getDouble(key);
                            } else if (key.equalsIgnoreCase("y")) {
                                y = section.getDouble(key);
                            } else if (key.equalsIgnoreCase("z")) {
                                z = section.getDouble(key);
                            } else if (key.equalsIgnoreCase("radius")) {
                                radius = section.getInt(key);
                            } else if (key.equalsIgnoreCase("height")) {
                                height = section.getInt(key);
                            } else if (key.equalsIgnoreCase("pitch")) {
                                pitch = (float) section.getDouble(key);
                            } else if (key.equalsIgnoreCase("yaw")) {
                                yaw = (float) section.getDouble(key);
                            }
                        }
                        if (x == Integer.MAX_VALUE) {
                            System.out.print(String.format(tm.getLoadSpawnsConfigError(), spawnName, "X"));
                            continue;
                        }
                        if (y == Integer.MAX_VALUE) {
                            System.out.print(String.format(tm.getLoadSpawnsConfigError(), spawnName, "Y"));
                            continue;
                        }
                        if (z == Integer.MAX_VALUE) {
                            System.out.print(String.format(tm.getLoadSpawnsConfigError(), spawnName, "Y"));
                            continue;
                        }
                        Location loc = new Location(HungergamesApi.getHungergames().world, x, y, z);
                        loc.setPitch(pitch);
                        loc.setYaw(yaw);
                        MainConfig mConfig = HungergamesApi.getConfigManager().getMainConfig();
                        if (mConfig.isForcedCords()) {
                            Location newLoc = new Location(loc.getWorld(), mConfig.getForceSpawnX(), loc.getY(),
                                    mConfig.getForceSpawnZ());
                            double dist = Math.sqrt(Math.pow(loc.getX() - newLoc.getX(), 2)
                                    + Math.pow(loc.getY() - newLoc.getY(), 2) + Math.pow(loc.getZ() - newLoc.getZ(), 2));
                            if (dist >= mConfig.getBorderSize()) {
                                System.out.print(String.format(tm.getLoadedSpawnOutsideBorder(), spawnName));
                            }
                        }
                        HungergamesApi.getPlayerManager().addSpawnPoint(loc, radius, height);
                        i++;
                    } catch (Exception ex) {
                        System.out.print(String.format(tm.getLoadSpawnsConfigError(), spawnName, ex.getMessage()));
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
