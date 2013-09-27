package me.libraryaddict.Hungergames.Listeners;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Events.FeastAnnouncedEvent;
import me.libraryaddict.Hungergames.Events.FeastSpawnedEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Interfaces.ChestManager;
import me.libraryaddict.Hungergames.Managers.ConfigManager;
import me.libraryaddict.Hungergames.Managers.GenerationManager;
import me.libraryaddict.Hungergames.Managers.ScoreboardManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.RandomItem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class LibsFeastManager implements Listener {
    private static LibsFeastManager feastManager;
    static {
        feastManager = new LibsFeastManager();
    }

    public static LibsFeastManager getFeastManager() {
        return feastManager;
    }

    protected HashMap<Integer, String> advertisements = new HashMap<Integer, String>();
    protected int chestLayerHeight = 3;
    protected String feastBlocks = "QUATYZ_BLOCk 1";
    protected int feastGenerateTime = 20 * 60;
    protected String feastGround = "QUARTZ_BLOCK 0";
    protected String feastInsides = "TNT 0";
    protected Location feastLoc;
    protected int feastPlatformGenerateTime = 15 * 60;
    protected int feastPlatformSize = 20;
    protected GenerationManager gen;
    protected boolean isEnabled;
    protected String scoreboardDuringFeast = ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Looting feast";
    protected String scoreboardFeastStartingIn = ChatColor.GOLD + "Feast in:";;
    protected String scoreboardPrefeast = ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Prefeast";;

    public LibsFeastManager() {
        // Setup the messages and times
        for (int i = 0; i < 4; i++) {
            advertisements.put((60 * 15) + (i * 60), ChatColor.RED + "The feast will begin at (%s, %s, %s) in %s");
            advertisements.put((60 * 15) + (i * 60), ChatColor.RED + "Use /feast to fix your compass on it!");
        }
        advertisements.put((60 * 19) + 30, ChatColor.RED + "The feast will begin at (%s, %s, %s) in %s");
        advertisements.put((60 * 19) + 45, ChatColor.RED + "The feast will begin at (%s, %s, %s) in %s");
        advertisements.put((60 * 19) + 50, ChatColor.RED + "The feast will begin at (%s, %s, %s) in %s");
        for (int i = 1; i <= 5; i++)
            advertisements.put((60 * 20) - i, ChatColor.RED + "The feast will begin at (%s, %s, %s) in %s");

        Location spawn = HungergamesApi.getHungergames().world.getSpawnLocation();
        feastLoc = new Location(spawn.getWorld(), spawn.getX() + (new Random().nextInt(200) - 100), -1, spawn.getZ()
                + (new Random().nextInt(200) - 100));
        Hungergames hg = HungergamesApi.getHungergames();
        File file = new File(hg.getDataFolder().toString() + "/feast.yml");
        try {
            if (!file.exists())
                file.createNewFile();
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            ChestManager chest = HungergamesApi.getChestManager();
            // Use reflection to set all the fields. Because I'm lazy.
            for (Field field : getClass().getDeclaredFields()) {
                Object obj = field.get(this);
                if (obj != null && (obj instanceof String || obj instanceof Integer)) {
                    if (!config.contains(field.getName()))
                        config.set(field.getName(), obj);
                    else
                        field.set(this, config.get(field.getName()));
                }
            }
            if (!config.contains("Advertisements")) {
                for (int time : advertisements.keySet()) {
                    config.set("Advertisements." + time, advertisements.get(time));
                }
            } else {
                advertisements.clear();
                for (String key : config.getConfigurationSection("Advertisements").getKeys(false)) {
                    advertisements.put(Integer.parseInt(key), config.getString("Advertisements." + key));
                }
            }
            if (!config.contains("FeastLoot")) {
                ArrayList<String> strings = new ArrayList<String>();
                for (RandomItem item : chest.getRandomItems())
                    strings.add(item.toString());
                config.set("How To Use",
                        "Chance in hundred, MinAmount, MaxAmount, ID or Material, Data Value, Addictional data like in kits items such as enchants");
                config.set("FeastLoot", strings);
            }
            config.save(file);
            chest.clearRandomItems();
            for (String string : config.getStringList("FeastLoot")) {
                chest.addRandomItem(new RandomItem(string));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Generates the chests. Height is the amount of chests. There will be a enchanting table on top of this putting the total at
     * 4 blocks high.
     * 
     * @param loc
     * @param height
     *            of chests
     */
    public void generateChests(final Location loc, final int height) {
        ChestManager cm = HungergamesApi.getChestManager();
        ConfigManager config = HungergamesApi.getConfigManager();
        for (Player p : Bukkit.getOnlinePlayers()) {
            Location l = p.getLocation().clone();
            l.setY(loc.getY());
            if (l.distance(loc) < height && p.getLocation().getY() >= l.getY() && p.getLocation().getY() - l.getY() <= height) {
                l.setY(l.getY() + height + 1);
                p.teleport(l);
            }
        }
        ItemStack feastInside = config.parseItem(feastInsides);
        ItemStack feastBlock = config.parseItem(feastBlocks);
        for (int x = -height; x < height + 1; x++) {
            for (int z = -height; z < height + 1; z++) {
                int y = Math.abs(x);
                if (Math.abs(z) > y)
                    y = Math.abs(z);
                y = -y + height;
                Block block = loc.clone().add(x, y, z).getBlock();
                Block b = block;
                // while repeated > 0
                for (int yLevel = y; yLevel > 0; yLevel--) {
                    b = b.getRelative(BlockFace.DOWN);
                    if (y - 1 >= yLevel)
                        gen.setBlockFast(b, feastInside.getTypeId(), feastInside.getDurability());
                    else
                        gen.setBlockFast(b, feastBlock.getTypeId(), feastBlock.getDurability());
                }
                if (x == 0 && z == 0) {
                    gen.setBlockFast(block, Material.ENCHANTMENT_TABLE.getId(), (byte) 0);
                    gen.setBlockFast(block.getRelative(BlockFace.DOWN), feastInside.getTypeId(),
                            (feastInside.getType() == Material.TNT ? 1 : feastInside.getDurability()));
                } else if (Math.abs(x + z) % 2 == 0) {
                    block.setTypeIdAndData(Material.CHEST.getId(), (byte) 0, false);
                    gen.addToProcessedBlocks(block);
                    Chest chest = (Chest) block.getState();
                    cm.fillChest(chest.getInventory());
                    chest.update();
                } else
                    gen.setBlockFast(block, feastBlock.getTypeId(), feastBlock.getDurability());
            }
        }
    }

    public void generatePlatform(Location loc, int chestLayers, int platformSize) {
        ItemStack item = HungergamesApi.getConfigManager().parseItem(feastGround);
        gen.generatePlatform(feastLoc, chestLayers, platformSize, item.getType(), item.getDurability());
    }

    public int getFeastLayers() {
        return this.chestLayerHeight;
    }

    public Location getFeastLocation() {
        return this.feastLoc;
    }

    public int getPlatformSize() {
        return this.feastPlatformSize;
    }

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        int currentTime = HungergamesApi.getHungergames().currentTime;
        if (advertisements.containsKey(currentTime)) {
            Bukkit.broadcastMessage(String.format(advertisements.get(currentTime), feastLoc.getX(), feastLoc.getY(),
                    feastLoc.getZ(), HungergamesApi.getHungergames().returnTime(currentTime)));
        }
        if (currentTime == feastPlatformGenerateTime) {
            feastLoc.setY(feastLoc.getWorld().getHighestBlockYAt(feastLoc.getBlockX(), feastLoc.getBlockZ()));
            int feastHeight = gen.getSpawnHeight(feastLoc, feastPlatformSize);
            generatePlatform(feastLoc, chestLayerHeight, feastHeight);
            ScoreboardManager.setDisplayName("Main", DisplaySlot.SIDEBAR, this.scoreboardPrefeast);
            HungergamesApi.getInventoryManager().updateSpectatorHeads();
            Bukkit.getPluginManager().callEvent(new FeastAnnouncedEvent());
        }
        if (currentTime == feastGenerateTime) {
            ScoreboardManager.hideScore("Main", DisplaySlot.SIDEBAR, scoreboardFeastStartingIn);
            generateChests(feastLoc, chestLayerHeight);
            World world = HungergamesApi.getHungergames().world;
            world.playSound(world.getSpawnLocation(), Sound.IRONGOLEM_DEATH, 1000, 0);
            Bukkit.getPluginManager().callEvent(new FeastSpawnedEvent());
        } else if (currentTime > feastPlatformGenerateTime && currentTime < feastGenerateTime) {
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, scoreboardFeastStartingIn, feastGenerateTime - currentTime);
        }
    }

    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled) {
            isEnabled = enabled;
            if (isEnabled) {
                Bukkit.getPluginManager().registerEvents(this, HungergamesApi.getHungergames());
            } else {
                HandlerList.unregisterAll(this);
            }
        }
    }
}
