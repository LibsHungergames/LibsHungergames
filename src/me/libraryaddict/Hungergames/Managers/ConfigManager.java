package me.libraryaddict.Hungergames.Managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Listeners.LibsFeastManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Utilities.UpdateChecker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConfigManager {

    private String alivePrefix;
    private double border;
    private double borderClosesIn;
    private List<String> commandsToRunBeforeShutdown;
    private String currentVersion;
    private boolean disableMetrics;
    private boolean displayMessages;
    private boolean displayScoreboards;
    private boolean fireSpread;
    private boolean flyPreGame;
    private boolean forceCords;
    private int gameShutdownDelay;
    private ArrayList<Integer> gameStartingBroadcastTimes = new ArrayList<Integer>();
    private Hungergames hg;
    private int invincibility;
    private ArrayList<Integer> invincibilityBroadcastTimes = new ArrayList<Integer>();
    private boolean invisSpectators;
    private boolean kickOnDeath;
    private boolean kitSelector;
    private ItemStack kitSelectorBack;
    private boolean kitSelectorDynamicSize;
    private ItemStack kitSelectorForward;
    private ItemStack kitSelectorIcon;
    private int kitSelectorInventorySize;
    private String latestVersion = null;
    private int minPlayers;
    public int mobSpawnChance;
    private boolean mushroomStew;
    private int mushroomStewRestores;
    private boolean mysqlEnabled;
    private boolean roundedBorder;
    private boolean shortenNames;
    private String spectatingPrefix;
    private boolean spectatorChat;
    private ItemStack spectatorItemBack;
    private ItemStack spectatorItemForwards;
    private boolean spectators;
    private int timeOfDay = 0;
    private UpdateChecker updateChecker;
    private int wonBroadcastsDelay;
    private int x;
    private int z;

    public ConfigManager() {
        hg = HungergamesApi.getHungergames();
        loadConfig();
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
     * Makes the update checker check for a update
     */
    public void checkUpdate() throws Exception {
        updateChecker = new UpdateChecker();
        updateChecker.checkUpdate(getCurrentVersion());
        latestVersion = updateChecker.getLatestVersion();
        if (latestVersion != null) {
            latestVersion = "v" + latestVersion;
            for (Player p : Bukkit.getOnlinePlayers())
                if (p.hasPermission("hungergames.update"))
                    p.sendMessage(String.format(HungergamesApi.getTranslationManager().getMessagePlayerUpdateAvailable(),
                            getCurrentVersion(), getLatestVersion()));
        }
    }

    /**
     * Check for a update
     */

    /**
     * @return Should it display messages about the game starting in bla bla?
     */
    public boolean displayMessages() {
        return displayMessages;
    }

    /**
     * @return Should it use scoreboards at all?
     */
    public boolean displayScoreboards() {
        return displayScoreboards;
    }

    /**
     * @return Should the plugin force the worlds spawn to be here
     */
    public boolean forceCords() {
        return forceCords;
    }

    public String getAlivePrefix() {
        return alivePrefix;
    }

    /**
     * @return How much does the border close in per second?
     */
    public double getBorderCloseInRate() {
        return borderClosesIn;
    }

    /**
     * @return Whats the current size of the border?
     */
    public double getBorderSize() {
        return border;
    }

    /**
     * Get the commands to run before shutdown
     */
    public List<String> getCommandsToRunBeforeShutdown() {
        return commandsToRunBeforeShutdown;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    /**
     * @return How much delay before shutting the game down?
     */
    public int getGameShutdownDelay() {
        return gameShutdownDelay;
    }

    /**
     * @return How long does invincibility last?
     */
    public int getInvincibilityTime() {
        return invincibility;
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
     * @return Get the item which will be displayed as the kit selector
     */
    public ItemStack getKitSelectorIcon() {
        return kitSelectorIcon;
    }

    public int getKitSelectorInventorySize() {
        return kitSelectorInventorySize;
    }

    /**
     * Get the latest version released
     */
    public String getLatestVersion() {
        return latestVersion;
    }

    /**
     * @return How many players are required to start the game
     */
    public int getMinPlayers() {
        return minPlayers;
    }

    /**
     * What chance does a animal have of spawning
     */
    public int getMobSpawnChance() {
        return mobSpawnChance;
    }

    /**
     * @return Whats the X its forcing spawn to be
     */
    public int getSpawnX() {
        return x;
    }

    /**
     * @return Whats the Z its forcing spawn to be
     */
    public int getSpawnZ() {
        return z;
    }

    public String getSpectatingPrefix() {
        return spectatingPrefix;
    }

    public ItemStack getSpectatorInventoryBack() {
        return spectatorItemBack;
    }

    public ItemStack getSpectatorInventoryForwards() {
        return spectatorItemForwards;
    }

    public int getTimeOfDay() {
        return timeOfDay;
    }

    /**
     * @return How much delay before crowing the name of the winner?
     */
    public int getWinnerBroadcastDelay() {
        return wonBroadcastsDelay;
    }

    /**
     * @return Invincibility wears off in T-Minus <Seconds>
     */
    public int invincibilityWearsOffIn() {
        return invincibility - hg.currentTime;
    }

    /**
     * @return Should there be forest fires before the game starts?
     */
    public boolean isFireSpreadDisabled() {
        return fireSpread;
    }

    public boolean isFlyPreGame() {
        return flyPreGame;
    }

    public boolean isInvisSpectators() {
        return invisSpectators;
    }

    /**
     * @return Does the game kick the players on death
     */
    public boolean isKickOnDeath() {
        return kickOnDeath;
    }

    public boolean isKitSelectorDynamicSize() {
        return kitSelectorDynamicSize;
    }

    public boolean isMetricsDisabled() {
        return disableMetrics;
    }

    /**
     * @return Is mushroom stew enabled?
     */
    public boolean isMushroomStew() {
        return mushroomStew;
    }

    /**
     * @return Is the plugin using mysql
     */
    public boolean isMySqlEnabled() {
        return mysqlEnabled;
    }

    public boolean isRoundedBorder() {
        return roundedBorder;
    }

    /**
     * Is everyones name shortened to view their killstreak in tab
     */
    public boolean isShortenedNames() {
        return shortenNames;
    }

    /**
     * @return Is spectator chat hidden from mortal eyes to prevent the giving away of tactics and distractions?
     */
    public boolean isSpectatorChatHidden() {
        return spectatorChat;
    }

    /**
     * @return Are players allowed to join a game in progress?
     */
    public boolean isSpectatorsEnabled() {
        return spectators;
    }

    /**
     * Reload the config. This doesn't reload some values however
     */
    public void loadConfig() {
        hg.saveDefaultConfig();
        final TranslationManager cm = HungergamesApi.getTranslationManager();
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
        ReflectionManager rm = HungergamesApi.getReflectionManager();
        if (hg.getServer().getAllowNether() && hg.getConfig().getBoolean("DisableNether", true)) {
            rm.setPropertiesConfig("allow-nether", false);
            rm.savePropertiesConfig();
            System.out.println(cm.getLoggerDisabledNether());
        }
        if (hg.getServer().getSpawnRadius() > 0 && hg.getConfig().getBoolean("ChangeSpawnLimit", true)) {
            rm.setPropertiesConfig("spawn-protection", 0);
            rm.savePropertiesConfig();
            System.out.println(cm.getLoggerChangedSpawnRadius());
        }
        if ((Integer) rm.getPropertiesConfig("max-build-height", 128) > 128
                && hg.getConfig().getBoolean("ChangeHeightLimit", true)) {
            rm.setPropertiesConfig("max-build-height", 128);
            rm.savePropertiesConfig();
            System.out.println(cm.getLoggerChangedHeightLimit());
        }
        currentVersion = "v" + hg.getDescription().getVersion();
        if (hg.getConfig().getBoolean("CheckUpdates"))
            Bukkit.getScheduler().scheduleAsyncDelayedTask(hg, new Runnable() {
                public void run() {
                    try {
                        checkUpdate();
                    } catch (Exception ex) {
                        System.out.print(String.format(cm.getLoggerFailedToCheckUpdate(), ex.getMessage()));
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
        invincibility = hg.getConfig().getInt("Invincibility", 120);
        spectatorChat = !hg.getConfig().getBoolean("SpectatorChat", true);
        shortenNames = hg.getConfig().getBoolean("ShortenNames");
        spectators = hg.getConfig().getBoolean("Spectators", true);
        kickOnDeath = hg.getConfig().getBoolean("KickOnDeath");
        mushroomStew = hg.getConfig().getBoolean("MushroomStew", false);
        mushroomStewRestores = hg.getConfig().getInt("MushroomStewRestores", 5);
        kitSelector = hg.getConfig().getBoolean("EnableKitSelector", true);
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

        spectatorItemBack = parseItem(hg.getConfig().getString("SpectatorInventoryForward"));
        if (spectatorItemBack == null)
            spectatorItemBack = new ItemStack(Material.SUGAR_CANE_BLOCK);
        spectatorItemForwards = parseItem(hg.getConfig().getString("SpectatorInventoryBack"));
        if (spectatorItemForwards == null)
            spectatorItemForwards = new ItemStack(Material.SUGAR_CANE_BLOCK);

        kitSelectorDynamicSize = hg.getConfig().getBoolean("KitSelectorDynamicSize");
        kitSelectorInventorySize = hg.getConfig().getInt("KitSelectorInventorySize");
        mobSpawnChance = hg.getConfig().getInt("MobSpawnChance");
        if (hg.getConfig().contains("CommandsToRunBeforeShutdown"))
            commandsToRunBeforeShutdown = hg.getConfig().getStringList("CommandsToRunBeforeShutdown");
        else
            commandsToRunBeforeShutdown = new ArrayList<String>();
        disableMetrics = hg.getConfig().getBoolean("DisableMetrics");
        flyPreGame = hg.getConfig().getBoolean("FlyPregame");
        if (hg.getConfig().getBoolean("ChangeAlivePrefix"))
            alivePrefix = hg.getConfig().getString("AlivePrefix");
        if (hg.getConfig().getBoolean("ChangeSpectatingPrefix"))
            spectatingPrefix = hg.getConfig().getString("SpectatingPrefix");
        invisSpectators = !hg.getConfig().getBoolean("InvisibleSpectators");

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
        if (hg.getConfig().getBoolean("Feast"))
            LibsFeastManager.getFeastManager().setEnabled(true);
    }

    /**
     * @return How much hearts or hunger should soup restore
     */
    public int mushroomStewRestores() {
        return mushroomStewRestores;
    }

    /**
     * @param String
     *            containing item
     * @return Itemstack parsed from the string
     */
    public ItemStack parseItem(String string) {
        String[] args = string.split(" ");
        int id = hg.isNumeric(args[0]) ? Integer.parseInt(args[0])
                : (Material.getMaterial(args[0].toUpperCase()) == null ? Material.AIR : Material.getMaterial(args[0]
                        .toUpperCase())).getId();
        return new ItemStack(id, 1, Short.parseShort(args[1]));
    }

    public void setBorderCloseInRate(double rate) {
        this.borderClosesIn = rate;
    }

    /**
     * @param Whats
     *            the new border size?
     */
    public void setBorderSize(double newBorder) {
        border = newBorder;
    }

    public void setRoundedBorder(boolean roundedBorder) {
        this.roundedBorder = roundedBorder;
    }

    public void setTimeOfDay(int timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    /**
     * @return Should it give players that fancy kit selector
     */
    public boolean useKitSelector() {
        return kitSelector;
    }

}
