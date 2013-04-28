package me.libraryaddict.Hungergames.Managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.inventory.ItemStack;

import de.robingrether.idisguise.iDisguise;

public class ConfigManager {

    private int feastSize;
    private int invincibility;
    private boolean borderCloseIn;
    private boolean spectatorChat;
    private boolean spectators;
    private int timeTillFeast;
    private double border;
    private double borderClosesIn;
    private int chestLayers;
    private boolean mushroomStew;
    private int mushroomStewRestores;
    private String gameStartingMotd;
    private String gameStartedMotd;
    private String kickMessage;
    private int minPlayers;
    private boolean fireSpread;
    private int wonBroadcastsDelay;
    private int gameShutdownDelay;
    private boolean shortenTime;
    private boolean displayMessages;
    private boolean displayScoreboards;
    private boolean mysqlEnabled;
    private ArrayList<Integer> feastBroadcastTimes = new ArrayList<Integer>();
    private ArrayList<Integer> invincibilityBroadcastTimes = new ArrayList<Integer>();
    private ArrayList<Integer> gameStartingBroadcastTimes = new ArrayList<Integer>();
    private Hungergames hg;
    private boolean kitSelector;
    private ItemStack feastGround;
    private ItemStack feast;
    private ItemStack feastInsides;
    private boolean feastTnt;
    private boolean generatePillars;
    private ItemStack pillarCorner;
    private ItemStack pillarInsides;
    private boolean forceCords;
    private int x;
    private int z;

    public ConfigManager() {
        hg = HungergamesApi.getHungergames();
        loadConfig();
    }

    public int getChestLayers() {
        return chestLayers;
    }

    public int getFeastSize() {
        return feastSize;
    }

    public String getGameStartingMotd() {
        return gameStartingMotd;
    }

    public String getGameStartedMotd() {
        return gameStartedMotd;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void loadConfig() {
        hg.saveDefaultConfig();
        if (Bukkit.getServer().getAllowEnd() && hg.getConfig().getBoolean("DisableEnd", true)) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("bukkit.yml"));
            config.set("settings.allow-end", false);
            try {
                config.save(new File("bukkit.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Disabled the end");
        }
        if (hg.getServer().getAllowNether() && hg.getConfig().getBoolean("DisableNether", true)) {
            ((CraftServer) hg.getServer()).getServer().getPropertyManager().a("allow-nether", false);
            System.out.println("Disabled the nether");
        }
        if (hg.getServer().getSpawnRadius() > 0 && hg.getConfig().getBoolean("ChangeSpawnLimit", true)) {
            ((CraftServer) hg.getServer()).getServer().getPropertyManager().a("spawn-protection", 0);
            System.out.println("Changed spawn radius to 0");
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
            public void run() {
                if (Bukkit.getPluginManager().getPlugin("iDisguise") != null) {
                    if (hg.getConfig().getBoolean("ChangeDisguiseConfig", true)) {
                        iDisguise disguise = (iDisguise) Bukkit.getPluginManager().getPlugin("iDisguise");
                        FileConfiguration config = disguise.getConfig();
                        config.set("save-disguises", false);
                        config.set("undisguise-on-hit", false);
                        try {
                            config.save(new File("plugins/iDisguise/Config.yml"));
                        } catch (IOException e) {
                            System.out.print("Failed to change iDisguise config");
                        }
                        disguise.config.loadConfig();
                    }
                }
            }
        });
        hg.currentTime = -Math.abs(hg.getConfig().getInt("Countdown", 270));
        mysqlEnabled = hg.getConfig().getBoolean("UseMySql", false);
        shortenTime = hg.getConfig().getBoolean("ShortenTime", false);
        displayScoreboards = hg.getConfig().getBoolean("Scoreboards", false);
        displayMessages = hg.getConfig().getBoolean("Messages", true);
        shortenTime = hg.getConfig().getBoolean("ShortenTime", false);
        minPlayers = hg.getConfig().getInt("MinPlayers", 2);
        fireSpread = hg.getConfig().getBoolean("DisableFireSpread", false);
        wonBroadcastsDelay = hg.getConfig().getInt("WinnerBroadcastingDelay");
        gameShutdownDelay = hg.getConfig().getInt("GameShutdownDelay");
        kickMessage = ChatColor.translateAlternateColorCodes('&',
                hg.getConfig().getString("KickMessage", "&6%winner% won!\n\nPlugin provided by libraryaddict"));
        feastSize = hg.getConfig().getInt("FeastSize", 20);
        invincibility = hg.getConfig().getInt("Invincibility", 120);
        border = hg.getConfig().getInt("BorderSize", 500);
        chestLayers = hg.getConfig().getInt("ChestLayers", 500);
        timeTillFeast = hg.getConfig().getInt("TimeTillFeast", 500);
        borderCloseIn = hg.getConfig().getBoolean("BorderCloseIn", true);
        borderClosesIn = hg.getConfig().getDouble("BorderClosesIn", 0.2);
        spectatorChat = hg.getConfig().getBoolean("SpectatorChat", true);
        spectators = hg.getConfig().getBoolean("Spectators", true);
        mushroomStew = hg.getConfig().getBoolean("MushroomStew", false);
        mushroomStewRestores = hg.getConfig().getInt("MushroomStewRestores", 5);
        gameStartingMotd = ChatColor.translateAlternateColorCodes('&',
                hg.getConfig().getString("GameStartingMotd", "&2Game starting in %time%."));
        gameStartedMotd = ChatColor.translateAlternateColorCodes('&',
                hg.getConfig().getString("GameStartedMotd", "&4Game in progress."));
        kitSelector = hg.getConfig().getBoolean("KitSelector", true);
        feastTnt = hg.getConfig().getBoolean("FeastTnt", true);
        feastGround = parseItem(hg.getConfig().getString("FeastGround"));
        feast = parseItem(hg.getConfig().getString("Feast"));
        generatePillars = hg.getConfig().getBoolean("Pillars", true);
        feastInsides = parseItem(hg.getConfig().getString("FeastInsides"));
        pillarCorner = parseItem(hg.getConfig().getString("PillarCorner"));
        pillarInsides = parseItem(hg.getConfig().getString("PillarInsides"));
        forceCords = hg.getConfig().getBoolean("ForceCords", true);
        x = hg.getConfig().getInt("ForceX", 0);
        z = hg.getConfig().getInt("ForceZ", 0);

        // Create the times where it broadcasts and advertises the feast
        for (int i = 1; i < 6; i++)
            feastBroadcastTimes.add(i);
        feastBroadcastTimes.add(30);
        feastBroadcastTimes.add(15);
        feastBroadcastTimes.add(10);

        // Create the times where it advertises invincibility
        for (int i = 1; i <= 5; i++)
            invincibilityBroadcastTimes.add(i);
        invincibilityBroadcastTimes.add(30);
        invincibilityBroadcastTimes.add(15);
        invincibilityBroadcastTimes.add(10);

        // Create the times where it advertises when the game starts

        for (int i = 1; i <= 5; i++)
            gameStartingBroadcastTimes.add(-i);
        gameStartingBroadcastTimes.add(-30);
        gameStartingBroadcastTimes.add(-15);
        gameStartingBroadcastTimes.add(-10);
    }
    
    public boolean forceCords() {
        return forceCords;
    }
    
    public int getSpawnX() {
        return x;
    }
    
    public int getSpawnZ() {
        return z;
    }

    private ItemStack parseItem(String string) {
        String[] args = string.split(" ");
        int id = hg.isNumeric(args[0]) ? Integer.parseInt(args[0])
                : (Material.getMaterial(args[0].toUpperCase()) == null ? Material.AIR : Material.getMaterial(args[0]
                        .toUpperCase())).getId();
        return new ItemStack(id, 1, Short.parseShort(args[1]));

    }

    public boolean generatePillars() {
        return generatePillars;
    }

    public ItemStack getPillarCorner() {
        return pillarCorner;
    }

    public ItemStack getPillarInsides() {
        return pillarInsides;
    }

    public ItemStack getFeastGround() {
        return feastGround;
    }

    public ItemStack getFeast() {
        return feast;
    }

    public ItemStack getFeastInsides() {
        return feastInsides;
    }

    public boolean isFeastTntIgnite() {
        return feastTnt;
    }

    public boolean isMySqlEnabled() {
        return mysqlEnabled;
    }

    public boolean useKitSelector() {
        return kitSelector;
    }

    public int feastStartsIn() {
        return timeTillFeast - hg.currentTime;
    }

    public int invincibilityWearsOffIn() {
        return invincibility - hg.currentTime;
    }

    public boolean isMushroomStew() {
        return mushroomStew;
    }

    public boolean advertiseGameStarting(int time) {
        if (time >= -180) {
            if (time % 60 == 0)
                return true;
        } else if (time % 180 == 0 || time % (5 * 60) == 0)
            return true;
        return gameStartingBroadcastTimes.contains(time);
    }

    // Feed current time and it returns if I broadcast
    public boolean advertiseInvincibility(int time) {
        time = invincibility - time;
        if (time <= 180) {
            if (time % 60 == 0)
                return true;
        } else if (time % 180 == 0 || time % (5 * 60) == 0)
            return true;
        return invincibilityBroadcastTimes.contains(time);
    }

    public boolean advertiseFeast(int time) {
        time = timeTillFeast - time;
        if (time % 60 == 0)
            return true;
        if (time <= 180) {
            if (time % 60 == 0)
                return true;
        } else if (time % 180 == 0)
            return true;
        return feastBroadcastTimes.contains(time);
    }

    public int mushroomStewRestores() {
        return mushroomStewRestores;
    }

    public boolean isSpectatorsEnabled() {
        return spectators;
    }

    public boolean isSpectatorChatHidden() {
        return spectatorChat;
    }

    public boolean doesBorderCloseIn() {
        return borderCloseIn;
    }

    public double getBorderCloseInRate() {
        return borderClosesIn;
    }

    public int getTimeFeastStarts() {
        return timeTillFeast;
    }

    public int getInvincibilityTime() {
        return invincibility;
    }

    public double getBorderSize() {
        return border;
    }

    public void setBorderSize(double newBorder) {
        border = newBorder;
    }

    public boolean displayMessages() {
        return displayMessages;
    }

    public boolean displayScoreboards() {
        return displayScoreboards;
    }

    public boolean shortenTime() {
        return shortenTime;
    }

    public int getWinnerBroadcastDelay() {
        return wonBroadcastsDelay;
    }

    public int getGameShutdownDelay() {
        return gameShutdownDelay;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public boolean isFireSpreadDisabled() {
        return fireSpread;
    }
}
