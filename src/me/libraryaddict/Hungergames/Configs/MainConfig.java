package me.libraryaddict.Hungergames.Configs;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.ReflectionManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Utilities.UpdateChecker;

@Data
@EqualsAndHashCode(callSuper = false)
public class MainConfig extends BaseConfig {
    /**
     * The amount the border closes in per second
     */
    private double amountBorderClosesInPerSecond = 0.2;
    /**
     * The border size
     */
    private double borderSize = 500;
    /**
     * What time does the border start closing in
     */
    private int borderStartsClosingIn = 20 * 60;
    /**
     * Does the plugin check for updates..
     */
    private boolean checkUpdates = true;

    /**
     * Commands to run before the server shuts down
     */
    private String[] commandsToRunBeforeShutdown = new String[] { "kick Notch get out of here notch!",
            "kick MinecraftChick Yer banned" };
    /**
     * Command to stop the server with
     */
    private String commandToStopTheServerWith = "stop";

    /**
     * The current version of the plugin
     */
    private String currentVersion = null;
    /**
     * The amount of damage the border deals
     */
    private double damageBorderDeals = 1;
    /**
     * Is the end disabled? I will change it in the servers config :3
     */
    private boolean endDisabled = true;
    /**
     * Is fire spread enabled pre-game
     */
    private boolean fireSpreadPreGame = false;
    /**
     * Do I force the spawns co-ordinates to be at a location
     */
    private boolean forcedCords = true;

    /**
     * The 'x' of the worlds spawn if forcing co-ordinates
     */
    private int forceSpawnX;
    /**
     * The 'z' of the worlds spawn if forcing co-ordinates
     */
    private int forceSpawnZ;
    /**
     * How long do I wait before shutting down the game
     */
    private int gameShutdownDelay = 33;
    /**
     * How much hearts does mushroom stew heal
     */
    private int heartsMushroomStewHeals = 6;
    /**
     * Is there a height limit? I will change it!
     */
    private boolean heightLimitChanged = true;
    /**
     * Does the kit selector change its size to fit the kits
     */
    private boolean isKitSelectorSizeDynamic = true;
    /**
     * Is there a kick on death
     */
    private boolean kickOnDeath = false;
    /**
     * What item do I use for the kit selectors back button
     */
    private ItemStack kitSelectorBack = new ItemStack(Material.SIGN);
    /**
     * Do I give the players a kit selector when they join the game
     */
    private boolean kitSelectorEnabled = true;
    /**
     * What item do I use for the kit selectors forward button
     */
    private ItemStack kitSelectorForward = new ItemStack(Material.SIGN);
    /**
     * What item opens the kit selector menu
     */
    private ItemStack kitSelectorIcon = new ItemStack(Material.FEATHER);
    /**
     * What is the max size of the kit selector
     */
    private int kitSelectorInventorySize = 54;
    /**
     * Whats the latest version of the plugin.
     */
    private String latestVersion = null;
    /**
     * If there is a height limit. What should I set it to
     */
    private int maxHeightLimit = 128;
    /**
     * Is metrics enabled
     */
    private boolean metricsEnabled = true;;
    /**
     * Whats the minimal players required to start the game
     */
    private int minPlayersForGameStart = 2;
    /**
     * Whats the chance of a passive mob spawning, 1 in <number> chance. Less then 2 for a 100% chance
     */
    public int mobSpawnChance = 5;
    /**
     * Does mushroom stew instant eat for hearts
     */
    private boolean mushroomStewEnabled = false;
    /**
     * Is mysql enabled, if true, this will enable mysql for kit purchases
     */
    private boolean mysqlEnabled = false;
    /**
     * Is the nether disabled? I will change it in the servers config :3
     */
    private boolean netherDisabled = true;
    /**
     * Can players fly pre-game
     */
    private boolean playersFlyPreGame = true;
    /**
     * What is the players prefix while they are alive
     */
    private String prefixWhenAlive = "<%Name%> %Message%";
    private boolean preventMovingFromSpawnUsingPotions = false;
    /**
     * Is the border round instead of square
     */
    private boolean roundedBorder = false;
    /**
     * Is the scoreboard enabled, if false then other plugins can use their scoreboards
     */
    private boolean scoreboardEnabled = true;
    private int secondsToTeleportPlayerToSpawn = 180;
    /**
     * Should I shorten the names of players in the playerlist to prevent their names overlapping kills
     */
    private boolean shortenedNames = true;
    /**
     * Does the kit gui sort the kits depending on if they own it.
     */
    private boolean sortKitGuiByOwned = false;
    /**
     * Height to check when spawning players in
     */
    private int spawnHeight = 5;
    /**
     * You have spawn protection? No problemo! I fix!
     */
    private boolean spawnProtectionRemoved = true;
    /**
     * The spawn radius
     */
    private int spawnRadius = 10;
    /**
     * What is the prefix of spectators in chat
     */
    private String spectatingPrefix = "<&7%Name%&r> %Message%";
    /**
     * Spectators menu back item
     */
    private ItemStack spectatorItemBack = new ItemStack(Material.SIGN);
    /**
     * Spectators menu forwards item
     */
    private ItemStack spectatorItemForwards = new ItemStack(Material.SIGN);
    /**
     * Does the spectator menu work for them to teleport around
     */
    private boolean spectatorMenuEnabled = true;
    /**
     * Are players allowed to join a game in progress?
     */
    private boolean spectatorsAllowedToJoinInProgressGames = true;
    /**
     * Are spectators forbidden from talking to alive players
     */
    private boolean spectatorsChatHidden = true;
    /**
     * Do spectators see each other as 'ghosts'
     */
    private boolean spectatorsVisibleToEachOther = true;
    /**
     * Shall I teleport the player to his location pregame and detain him there?
     */
    private boolean teleportToSpawnLocationPregame = false;
    /**
     * How long does invincibility last
     */
    private int timeForInvincibility = 120;
    /**
     * Gets the time to set the game to when the plugin enables
     */
    private int timeGameIsSetToWhenEnabled = -300;
    /**
     * Gets the time to set the game time to when there isn't enough players
     */
    private int timeGameIsSetToWhenNotEnoughPlayers = -120;
    /**
     * When the game starts, what time should the world be
     */
    private int timeOfDay = 0;
    /**
     * Times to check for a valid spawn
     */
    private int timesToCheckForValidSpawnPerPlayer = 100;
    /**
     * The update checker
     */
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private UpdateChecker updateChecker;
    /**
     * Should the plugin make its own world to prevent deleting your world?
     */
    private boolean useOwnWorld = true;
    /**
     * The delay between 'libraryaddict won!' broadcasts after the game ends, not related to gameshutdown time
     */
    private int wonBroadcastsDelay = 6;

    public MainConfig() {
        super("config");
        dontSave("latestVersion", "currentVersion", "updateChecker");
    }

    public boolean advertiseInvincibility(int timeToCheck) {
        int time = this.getTimeForInvincibility() - timeToCheck;
        if (time <= 180) {
            if (time % 60 == 0)
                return true;
            if (time <= 30) {
                if (time % 15 == 0 || time == 10 || time <= 5)
                    return true;
            }
        } else if (time % 180 == 0 || time % (5 * 60) == 0)
            return true;
        return false;
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
                    p.sendMessage(String.format(HungergamesApi.getConfigManager().getTranslationsConfig()
                            .getMessagePlayerUpdateAvailable(), getCurrentVersion(), getLatestVersion()));
        }
    }

    public boolean isGameStarting(int timeToCheck) {
        timeToCheck = -timeToCheck;
        if (timeToCheck < 180) {
            if (timeToCheck % 60 == 0)
                return true;
            if (timeToCheck <= 30) {
                if (timeToCheck % 15 == 0 || timeToCheck == 10 || timeToCheck <= 5)
                    return true;
            }
        } else if (timeToCheck % 180 == 0 || timeToCheck % (5 * 60) == 0)
            return true;
        return false;
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        final LoggerConfig cm = HungergamesApi.getConfigManager().getLoggerConfig();
        if (Bukkit.getAllowEnd() && this.isEndDisabled()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("bukkit.yml"));
            config.set("settings.allow-end", false);
            try {
                config.save(new File("bukkit.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(cm.getDisabledEnd());
        }
        ReflectionManager rm = HungergamesApi.getReflectionManager();
        if (Bukkit.getAllowNether() && this.isNetherDisabled()) {
            rm.setPropertiesConfig("allow-nether", false);
            rm.savePropertiesConfig();
            System.out.println(cm.getDisabledNether());
        }
        if (Bukkit.getSpawnRadius() > 0 && this.isSpawnProtectionRemoved()) {
            rm.setPropertiesConfig("spawn-protection", 0);
            rm.savePropertiesConfig();
            System.out.println(cm.getDisabledSpawnRadius());
        }
        if ((Integer) rm.getPropertiesConfig("max-build-height", 128) > this.getMaxHeightLimit() && this.isHeightLimitChanged()) {
            rm.setPropertiesConfig("max-build-height", this.getMaxHeightLimit());
            rm.savePropertiesConfig();
            System.out.println(String.format(cm.getChangedWorldHeight(), this.getMaxHeightLimit()));
        }
        Hungergames hg = HungergamesApi.getHungergames();
        hg.currentTime = this.getTimeGameIsSetToWhenEnabled();
        currentVersion = "v" + hg.getDescription().getVersion();
        if (checkUpdates)
            Bukkit.getScheduler().scheduleAsyncDelayedTask(hg, new Runnable() {
                public void run() {
                    try {
                        checkUpdate();
                    } catch (Exception ex) {
                        System.out.print(String.format(cm.getErrorWhenCheckingForUpdate(), ex.getMessage()));
                    }
                }
            });
        Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
            public void run() {

                /**
                 * Touch this and you better leave this entire plugin alone because I didn't give you permission to modify this.
                 * By changing the creatorMessage to something which doesn't refer players to the plugin itself. You are going
                 * against my wishes.
                 */

                /**
                 * I reserve the right to issue a DMCA if you effectively remove this from your server but use at least 30% of
                 * this code.
                 */
                TranslationConfig translation = HungergamesApi.getConfigManager().getTranslationsConfig();
                translation.setMessagePlayerWhosePlugin(String.format(translation.getMessagePlayerWhosePlugin(),
                        getCurrentVersion(), "libraryaddict"));
                String message = ChatColor.stripColor(translation.getMessagePlayerWhosePlugin()).toLowerCase().replace(" ", "");
                String[] toCheck = new String[] { "libraryaddict", "ow.ly/kwbpo", "libshungergames" };
                boolean contains = false;
                for (String check : toCheck) {
                    if (message.contains(check) && message.length() + 2 > check.length()) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    translation.setMessagePlayerWhosePlugin(ChatColor.GOLD + "[Hungergames] " + ChatColor.DARK_GREEN
                            + "You are using " + ChatColor.GREEN + "LibsHungergames " + getCurrentVersion()
                            + ChatColor.DARK_GREEN + " by " + ChatColor.GREEN + "libraryaddict");
                }
                translation.setMessagePlayerWhosePlugin(scatterCodes(translation.getMessagePlayerWhosePlugin()));
            }
        });
    }

    private String scatterCodes(String toScatter) {
        StringBuilder builder = new StringBuilder();
        char[] chars = toScatter.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i > 0 && i % 2 == 0) {
                if (new Random().nextBoolean()) {
                    String color = ChatColor.getLastColors(builder.toString());
                    builder.append(ChatColor.WHITE + color);
                }
            }
            builder.append(chars[i]);
        }
        return builder.toString();
    }
}
