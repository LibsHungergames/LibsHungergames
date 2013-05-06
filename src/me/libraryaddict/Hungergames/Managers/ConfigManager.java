package me.libraryaddict.Hungergames.Managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
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
    private int minPlayers;
    private boolean fireSpread;
    private int wonBroadcastsDelay;
    private int gameShutdownDelay;
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
    private ItemStack kitSelectorIcon;
    private ItemStack kitSelectorForward;
    private ItemStack kitSelectorBack;
    private boolean kitSelectorDymanicSize;
    private int kitSelectorInventorySize;

    public ConfigManager() {
        hg = HungergamesApi.getHungergames();
        loadConfig();
    }

    /**
     * 
     * @return How many layers high is the feast
     */
    public int getChestLayers() {
        return chestLayers;
    }

    /**
     * 
     * @return How big is the feast generation
     */
    public int getFeastSize() {
        return feastSize;
    }

    /**
     * 
     * @return How many players are required to start the game
     */
    public int getMinPlayers() {
        return minPlayers;
    }

    /**
     * Reload the config. This doesn't reload some values however
     */
    public void loadConfig() {
        hg.saveDefaultConfig();
        final ChatManager cm = HungergamesApi.getChatManager();
        if (Bukkit.getServer().getAllowEnd() && hg.getConfig().getBoolean("DisableEnd", true)) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("bukkit.yml"));
            config.set("settings.allow-end", false);
            try {
                config.save(new File("bukkit.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(cm.getLoggerDisabledEnd());
        }
        if (hg.getServer().getAllowNether() && hg.getConfig().getBoolean("DisableNether", true)) {
            ((CraftServer) hg.getServer()).getServer().getPropertyManager().a("allow-nether", false);
            System.out.println(cm.getLoggerDisabledNether());
        }
        if (hg.getServer().getSpawnRadius() > 0 && hg.getConfig().getBoolean("ChangeSpawnLimit", true)) {
            ((CraftServer) hg.getServer()).getServer().getPropertyManager().a("spawn-protection", 0);
            System.out.println(cm.getLoggerChangedSpawnRadius());
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
            public void run() {
                if (Bukkit.getPluginManager().getPlugin("iDisguise") != null) {
                    if (hg.getConfig().getBoolean("ChangeDisguiseConfig", true)) {
                        iDisguise disguise = (iDisguise) Bukkit.getPluginManager().getPlugin("iDisguise");
                        FileConfiguration config = disguise.getConfig();
                        if (config.getBoolean("save-disguises") || config.getBoolean("undisguise-on-hit")) {
                            config.set("save-disguises", false);
                            config.set("undisguise-on-hit", false);
                            try {
                                config.save(new File("plugins/iDisguise/Config.yml"));
                                disguise.config.loadConfig();
                                System.out.print(cm.getLoggerChangedIDisguiseConfig());
                            } catch (IOException e) {
                                System.out.print(cm.getLoggerFailedToChangIDisguiseConfig());
                            }
                        }
                    }
                }
            }
        });
        hg.currentTime = -Math.abs(hg.getConfig().getInt("Countdown", 270));
        mysqlEnabled = hg.getConfig().getBoolean("UseMySql", false);
        displayScoreboards = hg.getConfig().getBoolean("Scoreboards", false);
        displayMessages = hg.getConfig().getBoolean("Messages", true);
        minPlayers = hg.getConfig().getInt("MinPlayers", 2);
        fireSpread = hg.getConfig().getBoolean("DisableFireSpread", false);
        wonBroadcastsDelay = hg.getConfig().getInt("WinnerBroadcastingDelay");
        gameShutdownDelay = hg.getConfig().getInt("GameShutdownDelay");
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
        kitSelector = hg.getConfig().getBoolean("UseKitSelector", true);
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
        kitSelectorIcon = parseItem(hg.getConfig().getString("KitSelectorIcon"));
        if (kitSelectorIcon == null)
            kitSelectorIcon = new ItemStack(Material.FEATHER);
        kitSelectorBack = parseItem(hg.getConfig().getString("KitSelectorForward"));
        if (kitSelectorBack == null)
            kitSelectorBack = new ItemStack(Material.SUGAR_CANE_BLOCK);
        kitSelectorForward = parseItem(hg.getConfig().getString("KitSelectorBack"));
        if (kitSelectorForward == null)
            kitSelectorForward = new ItemStack(Material.SUGAR_CANE_BLOCK);
        kitSelectorDymanicSize = hg.getConfig().getBoolean("KitSelectorDymanicSize");
        kitSelectorInventorySize = hg.getConfig().getInt("KitSelectorInventorySize");

        // Create the times where it broadcasts and advertises the feast
        feastBroadcastTimes.clear();
        for (int i = 1; i < 6; i++)
            feastBroadcastTimes.add(i);
        feastBroadcastTimes.add(30);
        feastBroadcastTimes.add(15);
        feastBroadcastTimes.add(10);

        invincibilityBroadcastTimes.clear();
        // Create the times where it advertises invincibility
        for (int i = 1; i <= 5; i++)
            invincibilityBroadcastTimes.add(i);
        invincibilityBroadcastTimes.add(30);
        invincibilityBroadcastTimes.add(15);
        invincibilityBroadcastTimes.add(10);

        // Create the times where it advertises when the game starts
        gameStartingBroadcastTimes.clear();
        for (int i = 1; i <= 5; i++)
            gameStartingBroadcastTimes.add(-i);
        gameStartingBroadcastTimes.add(-30);
        gameStartingBroadcastTimes.add(-15);
        gameStartingBroadcastTimes.add(-10);
    }

    /**
     * 
     * @return Should the plugin force the worlds spawn to be here
     */
    public boolean forceCords() {
        return forceCords;
    }

    /**
     * @return Get the item which will be displayed as the kit selector
     */
    public ItemStack getKitSelectorIcon() {
        return kitSelectorIcon;
    }

    /**
     * @return Get the item which will is the kit selectors 'forward'
     */
    public ItemStack getKitSelectorBack() {
        return kitSelectorBack;
    }

    /**
     * @return Get the item which will is the kit selectors 'back'
     */
    public ItemStack getKitSelectorForward() {
        return kitSelectorForward;
    }

    /**
     * 
     * @return Whats the X its forcing spawn to be
     */
    public int getSpawnX() {
        return x;
    }

    /**
     * 
     * @return Whats the Z its forcing spawn to be
     */
    public int getSpawnZ() {
        return z;
    }

    /**
     * 
     * @param String
     *            containing item
     * @return Itemstack parsed from the string
     */
    private ItemStack parseItem(String string) {
        String[] args = string.split(" ");
        int id = hg.isNumeric(args[0]) ? Integer.parseInt(args[0])
                : (Material.getMaterial(args[0].toUpperCase()) == null ? Material.AIR : Material.getMaterial(args[0]
                        .toUpperCase())).getId();
        return new ItemStack(id, 1, Short.parseShort(args[1]));

    }

    /**
     * 
     * @return Should it generate pillars beneath spawn to make it realistic
     */
    public boolean generatePillars() {
        return generatePillars;
    }

    /**
     * 
     * @return Whats the material used for the pillars corners
     */
    public ItemStack getPillarCorner() {
        return pillarCorner;
    }

    /**
     * 
     * @return Whats the material used for the rest of the pillars
     */
    public ItemStack getPillarInsides() {
        return pillarInsides;
    }

    /**
     * 
     * @return Whats the material used for the feast ground
     */
    public ItemStack getFeastGround() {
        return feastGround;
    }

    /**
     * 
     * @return Whats the material used for the outside covering of the feast
     */
    public ItemStack getFeast() {
        return feast;
    }

    /**
     * 
     * @return Whats the material used for the inside of the feast where no one
     *         sees
     */
    public ItemStack getFeastInsides() {
        return feastInsides;
    }

    /**
     * 
     * @return Does the topmost tnt hidden under the enchanting table ignite on
     *         punch?
     */
    public boolean isFeastTntIgnite() {
        return feastTnt;
    }

    /**
     * 
     * @return Is the plugin using mysql
     */
    public boolean isMySqlEnabled() {
        return mysqlEnabled;
    }

    /**
     * 
     * @return Should it give players that fancy kit selector
     */
    public boolean useKitSelector() {
        return kitSelector;
    }

    /**
     * 
     * @return The feast starts in T-Minus <Seconds>
     */
    public int feastStartsIn() {
        return timeTillFeast - hg.currentTime;
    }

    /**
     * 
     * @return Invincibility wears off in T-Minus <Seconds>
     */
    public int invincibilityWearsOffIn() {
        return invincibility - hg.currentTime;
    }

    /**
     * 
     * @return Is mushroom stew enabled?
     */
    public boolean isMushroomStew() {
        return mushroomStew;
    }

    /**
     * @param time
     *            till game starts
     * @return Should it advertise the game is starting?
     */
    public boolean advertiseGameStarting(int time) {
        if (time > -180) {
            if (time % 60 == 0)
                return true;
        } else if (time % 180 == 0 || time % (5 * 60) == 0)
            return true;
        return gameStartingBroadcastTimes.contains(time);
    }

    /**
     * 
     * @param Currenttime
     * @return Should it advertise about invincibility?
     */
    public boolean advertiseInvincibility(int time) {
        time = invincibility - time;
        if (time <= 180) {
            if (time % 60 == 0)
                return true;
        } else if (time % 180 == 0 || time % (5 * 60) == 0)
            return true;
        return invincibilityBroadcastTimes.contains(time);
    }

    /**
     * 
     * @param Current
     *            time
     * @return Should it advertise about the feast?
     */
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

    /**
     * 
     * @return How much hearts or hunger should soup restore
     */
    public int mushroomStewRestores() {
        return mushroomStewRestores;
    }

    /**
     * 
     * @return Do players spectate when killed? Or joining? Or are they kicked?
     */
    public boolean isSpectatorsEnabled() {
        return spectators;
    }

    /**
     * 
     * @return Is spectator chat hidden from mortal eyes to prevent the giving
     *         away of tactics and distractions?
     */
    public boolean isSpectatorChatHidden() {
        return spectatorChat;
    }

    /**
     * 
     * @return Does the border close in after the feast starts?
     */
    public boolean doesBorderCloseIn() {
        return borderCloseIn;
    }

    /**
     * 
     * @return How much does the border close in per second?
     */
    public double getBorderCloseInRate() {
        return borderClosesIn;
    }

    /**
     * 
     * @return How long until the feast starts?
     */
    public int getTimeFeastStarts() {
        return timeTillFeast;
    }

    /**
     * 
     * @return How long does invincibility last?
     */
    public int getInvincibilityTime() {
        return invincibility;
    }

    /**
     * 
     * @return Whats the current size of the border?
     */
    public double getBorderSize() {
        return border;
    }

    /**
     * 
     * @param Whats
     *            the new border size?
     */
    public void setBorderSize(double newBorder) {
        border = newBorder;
    }

    /**
     * 
     * @return Should it display messages about the game starting in bla bla?
     */
    public boolean displayMessages() {
        return displayMessages;
    }

    /**
     * 
     * @return Should it use scoreboards at all?
     */
    public boolean displayScoreboards() {
        return displayScoreboards;
    }

    /**
     * 
     * @return How much delay before crowing the name of the winner?
     */
    public int getWinnerBroadcastDelay() {
        return wonBroadcastsDelay;
    }

    /**
     * 
     * @return How much delay before shutting the game down?
     */
    public int getGameShutdownDelay() {
        return gameShutdownDelay;
    }

    /**
     * 
     * @return Should there be forest fires before the game starts?
     */
    public boolean isFireSpreadDisabled() {
        return fireSpread;
    }

    public boolean isKitSelectorDymanicSize() {
        return kitSelectorDymanicSize;
    }

    public int getKitSelectorInventorySize() {
        return kitSelectorInventorySize;
    }
}
