package me.libraryaddict.Hungergames.Managers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import me.libraryaddict.Hungergames.Types.HungergamesApi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class TranslationManager {
    private String broadcastFeastBegun = ChatColor.RED + "The feast has begun!";
    private String broadcastFeastStartingCompassMessage = "\nUse /feast to fix your compass on it!";
    private String broadcastFeastStartingIn = ChatColor.RED + "The feast will begin at (%s, %s, %s) in %s";
    private String broadcastGameStartedMessage = ChatColor.RED + "The game has started!";
    private String broadcastGameStartingIn = ChatColor.RED + "The game will start in %s";
    private String broadcastInvincibilityWornOff = ChatColor.RED + "Invincibility has worn off!";
    private String broadcastInvincibiltyWearsOffIn = ChatColor.RED + "Invincibility wears off in %s!";
    private String broadcastNotEnoughPlayers = ChatColor.RED + "You need more people!";
    private String broadcastWinnerWon = ChatColor.RED + "%s won!";
    private String commandBuildChangedOwnBuild = ChatColor.YELLOW + "Changed own build mode to %s";
    private String commandBuildNoPermission = ChatColor.RED + "You do not have permission to use this command.";
    private String commandBuildPlayerDoesntExist = ChatColor.RED + "Player doesn't exist";
    private String commandBuildRecieverBuildChanged = ChatColor.YELLOW + "%s has set your build mode to %s";
    private String commandBuildSenderBuildChanged = ChatColor.YELLOW + "You have set %s build mode to %s";
    private String commandBuyKitAlreadyOwn = ChatColor.AQUA + "You already own this kit!";
    private String commandBuyKitCantAfford = ChatColor.AQUA + "You can't afford this kit!";
    private String commandBuyKitCantBuyKit = ChatColor.AQUA + "You can't buy this kit!";
    private String commandBuyKitKitsNotLoaded = ChatColor.AQUA + "Your kits have not loaded yet!";
    private String commandBuyKitMysqlNotEnabled = ChatColor.GREEN
            + "Magical forces render you powerless and- Just kidding. The server owner did not setup mysql.";
    private String commandBuyKitNoArgs = ChatColor.AQUA + "You must define a kit id or name";
    private String commandBuyKitPurchasedKit = ChatColor.AQUA + "Successully purchased kit %s!";
    private String commandChunkCooldown = ChatColor.RED + "You may not do this again yet!";
    private String commandChunkLoggerReloadingChunks = "[Hungergames] Reloading %s's chunks";
    private String commandChunkReloadedChunks = ChatColor.RED + "Chunks reloaded!";
    private String commandCreator = ChatColor.RED + "%s created this plugin!\nDownload it at %s";
    private String commandFeastHappened = ChatColor.YELLOW + "Compass now pointing to the feast!";
    private String commandFeastNotHappened = ChatColor.RED + "The feast has not happened yet!";
    private String commandForceFeastGenerated = ChatColor.RED + "A feast has been spawned at (%s, %s, %s)";
    private String commandForceFeastNoPermission = ChatColor.RED + "You do not have permission to use this command";
    private String commandForceFeastNotANumber = ChatColor.RED + "'%s' isn't a number!";
    private String commandForceFeastStupidInput = ChatColor.RED + "Don't be ridiculous!";
    private String commandForceStartAlreadyStarted = ChatColor.RED + "The game has already started!";
    private String commandForceStartChangedCountdownTime = ChatColor.RED + "%s changed the countdown time to %s!";
    private String commandForceStartNoPermission = ChatColor.RED + "You do not have permission";
    private String commandForceStartNotANumber = ChatColor.RED + "'%s' is not a number!";
    private String commandForceTimeBroadcast = ChatColor.RED + "%s changed the time to %s!";
    private String commandForceTimeInfo = ChatColor.RED + "/forcetime <New Time>";
    private String commandForceTimeNoPermission = ChatColor.RED + "You do not have permission to use this command.";
    private String commandForceTimeNotANumber = ChatColor.RED + "Thats not a number silly!";
    private String commandGotoFeastFailed = ChatColor.RED + "The feast has not started yet!";
    private String commandGotoNameOfFeast = "feast";
    private String commandGotoNotEnoughArgs = ChatColor.RED + "Not enough arguements!";
    private String commandGotoNotSpectator = ChatColor.YELLOW + "You are not a spectator!";
    private String commandGotoPlayerDoesntExist = ChatColor.RED + "Player doesn't exist!";
    private String commandInvisHide = ChatColor.BLUE + "Hidden spectators";
    private String commandInvisHideAll = ChatColor.BLUE + "Hidden all spectators";
    private String commandInvisHidePlayerFail = ChatColor.BLUE + "Can't find the player %s";
    private String commandInvisHidePlayerNoArgs = ChatColor.BLUE + "You must give a playername";
    private String commandInvisHidePlayerSuccess = ChatColor.BLUE + "Hidden %s";
    private String commandInvisNameOfHide = "hide";
    private String commandInvisNameOfHideAll = "hideall";
    private String commandInvisNameOfHidePlayer = "hideplayer";
    private String commandInvisNameOfShow = "show";
    private String commandInvisNameOfShowAll = "showall";
    private String commandInvisNameOfShowPlayer = "showplayer";
    private String commandInvisNoPermission = ChatColor.RED + "You do not have permission to use this command";
    private String commandInvisNotEnoughArguments = ChatColor.RED
            + "Dude.. Use show, showall, hide, hideall, showplayer, hideplayer as parameters";
    private String commandInvisShow = ChatColor.BLUE + "You just forced all current spectators to show themselves to you.";
    private String commandInvisShowAll = ChatColor.BLUE + "All current players are now visible to each other";
    private String commandInvisShowPlayerFail = ChatColor.BLUE + "Can't find the player %s";
    private String commandInvisShowPlayerNoArgs = ChatColor.BLUE + "You must give a playername";
    private String commandInvisShowPlayerSuccess = ChatColor.BLUE + "Revealed %s";
    private String commandKillMurderMessage = "%1$2s was killed by a command.";
    private String commandKillNotAlive = ChatColor.RED + "He is not alive";
    private String commandKillPlayerNotFound = ChatColor.RED + "He doesn't exist";
    private String commandKillSomeoneNoPermission = ChatColor.RED + "You may not kill someone..";
    private String commandKillUseSuicide = ChatColor.RED + "Please use /suicide";
    private String commandKitAlreadyUsing = ChatColor.RED + "Already using kit %s!";
    private String commandKitGameAlreadyStarted = ChatColor.RED + "The game has already started!";
    private String commandKitInfoDefineKitName = ChatColor.AQUA + "You need to define a kit name or id!";
    private String commandKitItemsDefineKitName = ChatColor.AQUA + "You need to define a kit name!";
    private String commandKitItemsItemWithEnchant = "%s with enchant: %s";
    private String commandKitItemsItemWithEnchants = "%s with enchants: %s";
    private String commandKitKitDoesntExist = ChatColor.AQUA
            + "This kit does not exist!\nType /kit for all the kits you can use!";
    private String commandKitKitsDivider = ChatColor.DARK_GREEN + ", " + ChatColor.RESET;
    private String commandKitNoPermission = ChatColor.RED + "You do not have access to this kit!";
    private String commandKitNowUsingKit = ChatColor.AQUA + "Now using kit %s!";
    private String commandMessageEventCancelled = ChatColor.GREEN + "You are forbidden to talk to %s!";
    private String commandMessageNoArgs = ChatColor.GREEN + "Please fill in a player name and message!";
    private String commandMessageNoReceiver = ChatColor.GREEN + "Player does not exist";
    private String commandMessagePlayerNoArgs = ChatColor.GREEN + "Did you forget to type a message?";
    private String commandMessageReceiveMessage = ChatColor.GRAY + "[%s" + ChatColor.RESET + ChatColor.GRAY + " -> me] "
            + ChatColor.RESET + "%s";
    private String commandMessageSendMessage = ChatColor.GRAY + "[me -> %s" + ChatColor.RESET + ChatColor.GRAY + "] "
            + ChatColor.RESET + "%s";
    private String commandPlayers = ChatColor.DARK_GRAY + "There are " + ChatColor.GRAY + "%s" + ChatColor.DARK_GRAY
            + " gamers and " + ChatColor.GRAY + "%s" + ChatColor.DARK_GRAY + " spectators\n" + ChatColor.DARK_GRAY + "Gamers: "
            + ChatColor.GRAY + "%3$2s";
    private String commandPlayersTimeStatusStarted = ChatColor.DARK_GRAY + "The game has been going for %s.";
    private String commandPlayersTimeStatusStarting = ChatColor.DARK_GRAY + "The game is starting in %s.";
    private String commandReplyEventCancelled = ChatColor.GREEN + "You are forbidden to talk to %s!";
    private String commandReplyNoArgs = ChatColor.GREEN + "Did you forget to type a message?";
    private String commandReplyNoReceiver = ChatColor.GREEN + "You were not talking to anyone. Feeling alone?";
    private String commandReplyReceiveReply = ChatColor.GRAY + "[%s" + ChatColor.RESET + ChatColor.GRAY + " -> me] "
            + ChatColor.RESET + "%s";
    private String commandReplyReceiverLeft = ChatColor.GREEN + "Cannot find %s!";
    private String commandReplySendReply = ChatColor.GRAY + "[me -> %s" + ChatColor.RESET + ChatColor.GRAY + "] "
            + ChatColor.RESET + "%s";
    private String commandRideNameOfRideall = "rideall";
    private String commandRideRideAll = ChatColor.GREEN + "Giddy up horsie!";
    private String commandRideToggle = ChatColor.GREEN + "Toggled riding to %s! Yee-haw!";
    private String commandSpawnPointingToSpawn = ChatColor.YELLOW + "Compass is now pointing to spawn";
    private String commandSuicideAssistedDeathMessage = "%s was helped on the path to suicide";
    private String commandSuicideDoesntExist = ChatColor.RED + "He doesn't exist";
    private String commandSuicideKillMessage = "%s commited suicide.";
    private String commandSuicideNoPermission = ChatColor.RED + "You may not kill someone..";
    private String commandSuicideNotAlive = ChatColor.RED + "Dead men can't die";
    private String commandTimeStatusStarted = ChatColor.DARK_GRAY + "The game has been going for %s.";
    private String commandTimeStatusStarting = ChatColor.DARK_GRAY + "The game is starting in %s.";
    private transient YamlConfiguration config;
    private transient File configFile;
    private String gameStartedMotd = ChatColor.DARK_RED + "Game in progress.";
    private String inventoryDontOwnKit = "";
    private String inventoryOwnKit = "";
    private String[] itemKitSelectorBackDescription = new String[] { ChatColor.LIGHT_PURPLE + "Click this to move",
            ChatColor.LIGHT_PURPLE + "back a page" };
    private String itemKitSelectorBackName = ChatColor.RED + "Back";
    private String[] itemKitSelectorDescription = new String[] { ChatColor.LIGHT_PURPLE + "Right click with this",
            ChatColor.LIGHT_PURPLE + "to open a kit selection screen!" };
    private String[] itemKitSelectorForwardsDescription = new String[] { ChatColor.LIGHT_PURPLE + "Click this to move",
            ChatColor.LIGHT_PURPLE + "forwards a page" };
    private String itemKitSelectorForwardsName = ChatColor.RED + "Forward";
    private String itemKitSelectorName = ChatColor.WHITE + "Kit Selector";
    private String[] itemSpectatorInventoryBackDescription = new String[] { ChatColor.LIGHT_PURPLE + "Click this to move",
            ChatColor.LIGHT_PURPLE + "forwards a page" };
    private String itemSpectatorInventoryBackName = ChatColor.RED + "Back";
    private String[] itemSpectatorInventoryForwardsDescription = new String[] { ChatColor.LIGHT_PURPLE + "Click this to move",
            ChatColor.LIGHT_PURPLE + "forwards a page" };
    private String itemSpectatorInventoryForwardsName = ChatColor.RED + "Back";
    private String kickDeathMessage = "%s";
    private String kickGameFull = "The game is full!";
    private String kickGameShutdownUnexpected = "The game was shut down by a admin";
    private String kickMessageWon = ChatColor.BLUE + "%s won!\n\n" + ChatColor.GREEN + "Thanks for playing!\n\n"
            + ChatColor.DARK_GREEN + "Server restarting!";
    private String kickNobodyWonMessage = "Nobody won..\n\nThat could have been you!";
    private String kickSpectatorsDisabled = "Spectators have been disabled!";
    private String killMessageFellToDeath = "%s fell to their death";
    private String killMessageFormatPlayerKit = ChatColor.RED + "%s" + ChatColor.DARK_RED + "(" + ChatColor.RED + "%s"
            + ChatColor.DARK_RED + ")";
    private String killMessageKilledByBorder = "%s believed the rumors of a better life beyond the border";
    private String killMessageLeavingGame = "%s was slaughtered for leaving the game";
    private String killMessageNoKit = "None";
    private String[] killMessages = new String[] { "%Killer% dual wielded a %Weapon% and laid waste upon %Killed%",
            "%Killer% slid a %Weapon% into %Killed% when he wasn't looking",
            "%Killed% was murdered in cold blood by %Killer% with a %Weapon%",
            "%Killed% gasped his last breath as %Killer% savagely stabbed him with a %Weapon%",
            "%Killed% screamed in agony as he was bludgeoned over the head with a %Weapon% by %Killer%",
            "%Killed% was killed by %Killer% with a %Weapon%",
            "%Killer% gave %Killed% a helping hand into death's sweet embrace with his trusty %Weapon%",
            "%Killer%'s %Weapon% could not resist killing %Killed%", "%Killer% and his trusty %Weapon% slew %Killed%",
            "%Killed%'s weapon could not stand up against %Killer%'s %Weapon% of doom!" };
    private String kitDescriptionDefault = "No description was provided for this kit";
    private String loggerAbilityAlreadyExists = "Ability already exists!";
    private String loggerAbilityMissingValue = "[Hungergames] Restored ability '%s' missing config '%s'";
    private String loggerAddAbility = "[Hungergames] Added ability: %s";
    private String loggerChangedHeightLimit = "[Hungergames] Changed build height limit";
    private String loggerChangedIDisguiseConfig = "[Hungergames] Changed iDisguise config";
    private String loggerChangedSpawnRadius = "[Hungergames] Changed spawn radius to 0";
    private String loggerCommandsMissingValue = "[Hungergames] Restored missing config '%s' for command %s";
    private String loggerCreatingAbilitiesConfig = "[Hungergames] Creating ability config file";
    private String loggerCreatingCommandsConfig = "[Hungergames] Creating commands config file";
    private String loggerCreatingNamesConfig = "[Hungergames] Creating names config file";
    private String loggerCreatingTranslationConfig = "[Hungergames] Creating translation config file";
    private String loggerDependencyNotFound = "Dependency %s not found";
    private String loggerDisabledEnd = "[Hungergames] Disabled the end";
    private String loggerDisabledNether = "[Hungergames] Disabled the nether";
    private String loggerErrorWhileLoadingAbility = "[Hungergames] Error while loading ability: %s - %s";
    private String loggerErrorWhileLoadingCommands = "[Hungergames] Error while loading the commands: %s - %s";
    private String loggerErrorWhileLoadingTranslation = "[Hungergames] Error while loading the translation: %s";
    private String loggerErrorWhileParsingItemStack = "[Hungergames] Error while parsing itemstack line %s, %s";
    private String loggerErrorWhileRegisteringPlayerForAbility = "[Hungergames] Tried to register %s for the %s ability but it does not exist";
    private String loggerFailedToChangIDisguiseConfig = "[Hungergames] Failed to change iDisguise config";
    private String loggerFailedToCheckUpdate = "[Hungergames] Error while checking for a update - %s";
    private String loggerFoundAbilityInPackage = "[HungerGames] Found ability %s";
    private String loggerFoundCommandInPackage = "[HungerGames] Found command %s";
    private String loggerKitAlreadyExists = "[Hungergames] Error! Kit %s already exists!";
    private String loggerLoadAbilitiesInPackage = "[HungerGames] Initializing all abilities found in %s in the %s package";
    private String loggerLoadCommandsInPackage = "[HungerGames] Initializing all commands found in %s in the %s package";
    private String loggerLoadTranslationConfig = "[HungerGames] Loading the translation config";
    private String loggerMapConfigChangedBorderSize = "[Hungergames] Map config - Changed border size to %s";
    private String loggerMapConfigLoaded = "[Hungergames] Successfully loaded map config";
    private String loggerMapConfigNotFound = "[Hungergames] Map config not found";
    private String loggerMapConfigNowLoading = "[Hungergames] Now loading map config";
    private String loggerMetricsMessage = "[Hungergames] Dangit. Think you can opt back into metrics for me? I do want to see how popular my plugin is..";
    private String loggerMySqlClosing = "[%s] Disconnecting from MySQL database...";
    private String loggerMySqlClosingError = "[%s] Error while closing the connection...";
    private String loggerMySqlConnecting = "[%s] Connecting to MySQL database..";
    private String loggerMySqlConnectingError = "[%s] Error while connecting to MySQL. %s";
    private String loggerMySqlErrorLoadPlayer = "[PlayerJoinThread] Error while loading player %s - %s";
    private String loggerNoMapsFound = "[Hungergames] There are no maps to be found in %s";
    private String loggerNowAttemptingToLoadAMap = "[Hungergames] Now attempting to load a map from the path %s";
    private String loggerShutdownCancelled = "[Hungergames] Shutdown event was cancelled by some plugin!";
    private String loggerShuttingDown = "[Hungergames] Hungergames is now shutting the server down!";
    private String loggerSucessfullyLoadedMap = "[Hungergames] Sucessfully loaded map %s";
    private String loggerTranslationMissingValue = "[Hungergames] Restored translation missing config '%s'";
    private String loggerUnrecognisedItemId = "[Hungergames] Failed to recognise item ID %s";
    private String loggerWaitingForLoadGamerToComplete = "[Hungergames] Waiting for load gamer to complete, %s left!";
    private String messagePlayerApproachingBorder = ChatColor.YELLOW + "You are approaching the border!";
    private String messagePlayerHasHealthAndHunger = ChatColor.RED
            + "%1$2s has %2$2s/20 health\n%1$2s has %3$2s/20 hunger\n%1$2s is using kit: %4$2s";
    private String messagePlayerKitDesciprionPrice = ChatColor.DARK_AQUA + "Price:" + ChatColor.AQUA + " $%s";
    private String messagePlayerKitDesciprionPriceFree = ChatColor.DARK_AQUA + "Price:" + ChatColor.AQUA + " Free";
    private String messagePlayerKitDesciprionPriceUnbuyable = ChatColor.DARK_AQUA + "Price:" + ChatColor.AQUA + " Unbuyable";
    private String messagePlayerKitDesciptionId = ChatColor.DARK_AQUA + "Kit ID:" + ChatColor.AQUA + " %s";
    private String messagePlayerKitDescriptionDoesntExist = ChatColor.AQUA + "This kit does not exist!";
    private String messagePlayerKitDescriptionName = ChatColor.DARK_AQUA + "Name:" + ChatColor.AQUA + " %s";
    private String messagePlayerKitDescritionMoreInfo = ChatColor.AQUA
            + "Use /kititems %1$2s to view the items given with this kit\nUse /buykit %1$2s to purchase a kit";
    private String messagePlayerSendKitItemsDoesntExist = ChatColor.AQUA + "This kit does not exist!";
    private String messagePlayerSendKitItemsKitBoots = ChatColor.DARK_AQUA + "Kit Boots:" + ChatColor.AQUA + " %s";
    private String messagePlayerSendKitItemsKitChestplate = ChatColor.DARK_AQUA + "Kit Chestplate:" + ChatColor.AQUA + " %s";
    private String messagePlayerSendKitItemsKitHelmet = ChatColor.DARK_AQUA + "Kit Helmet:" + ChatColor.AQUA + " %s";
    private String messagePlayerSendKitItemsKitLeggings = ChatColor.DARK_AQUA + "Kit Leggings:" + ChatColor.AQUA + " %s";
    private String messagePlayerSendKitItemsKitName = ChatColor.DARK_AQUA + "Kit Name:" + ChatColor.AQUA + " %s";
    private String messagePlayerSendKitItemsNoItems = "No other items to display";
    private String messagePlayerSendKitItemsOtherItems = ChatColor.DARK_AQUA + "Other items:" + ChatColor.AQUA + " %s";
    private String messagePlayerShowKitsCurrentSelectedKit = ChatColor.DARK_GREEN + "Your current kit:" + ChatColor.RESET + " %s";
    private String messagePlayerShowKitsHisKits = ChatColor.DARK_GREEN + "Your kits:" + ChatColor.RESET + " %s";
    private String messagePlayerShowKitsNoKit = "None";
    private String messagePlayerShowKitsNoKits = "No kits available..";
    private String messagePlayerShowKitsOtherKits = ChatColor.DARK_GREEN + "Other kits:" + ChatColor.RESET + " %s";
    private String messagePlayerShowKitsUseKitInfo = ChatColor.AQUA + "To view the information on a kit, Use /kitinfo <Kit Name>";
    private String messagePlayerTrack = ChatColor.YELLOW + "Compass pointing at %s";
    private String messagePlayerTrackNoVictim = ChatColor.YELLOW + "No players found, Pointing at spawn";
    private String messagePlayerUpdateAvailable = ChatColor.RED + "[Hungergames]" + ChatColor.DARK_RED
            + " There is a update available, The new version is " + ChatColor.RED + "%s" + ChatColor.DARK_RED
            + " while your current version is " + ChatColor.RED + "%s";
    private String messagePlayerWarningForgeUnstableEnchants = ChatColor.RED
            + "Minecraft will crash if you attempt to put this in";
    private String messagePlayerWhosePlugin = ChatColor.GOLD + "[Hungergames] " + ChatColor.DARK_GREEN + "You are using "
            + ChatColor.GREEN + "LibsHungergames %s" + ChatColor.DARK_GREEN + " by " + ChatColor.GREEN + "libraryaddict";
    private transient boolean newFile = false;
    private String scoreboardBorderSize = ChatColor.GOLD + "BorderSize:";
    private String scoreboardFeastStartingIn = ChatColor.GOLD + "Feast in:";
    private String scoreBoardGameStartingIn = ChatColor.GOLD + "Starting in:";
    private String scoreboardInvincibleRemaining = ChatColor.GOLD + "Invincible:";
    private String scoreboardPlayersLength = ChatColor.GREEN + "Players:";
    private String scoreboardStageFeastHappened = ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Finishing Up";
    private String scoreboardStageFeastHappening = ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Looting Feast";
    private String scoreboardStageFighting = ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Fighting";
    private String scoreboardStageInvincibility = ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Invincibility";
    private String scoreboardStagePreFeast = ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Pre-Feast";
    private String scoreboardStagePreGame = ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Pregame";
    private String selectKitInventoryTitle = ChatColor.DARK_RED + "Select Kit";
    private boolean shouldIMessagePlayersWhosePlugin = true;
    private String spectatorHeadKills = ChatColor.GREEN + "Kills: " + ChatColor.BLUE + "%s";
    private String spectatorHeadKit = ChatColor.GREEN + "Kit: " + ChatColor.BLUE + "%s";
    private String spectatorInventoryTitle = ChatColor.DARK_GRAY + "Alive gamers";
    private String timeFormatMinute = "%s minute";
    private String timeFormatMinutes = "%s minutes";
    private String timeFormatMotdMinute = ChatColor.DARK_GREEN + "Game starting in %s minute.";
    private String timeFormatMotdMinutes = ChatColor.DARK_GREEN + "Game starting in %s minutes.";
    private String timeFormatMotdSecond = ChatColor.DARK_GREEN + "Game starting in %s second.";
    private String timeFormatMotdSecondAndMinute = ChatColor.DARK_GREEN + "Game starting in %s minute.";
    private String timeFormatMotdSecondAndMinutes = ChatColor.DARK_GREEN + "Game starting in %s minutes.";
    private String timeFormatMotdSeconds = ChatColor.DARK_GREEN + "Game starting in %s seconds";
    private String timeFormatMotdSecondsAndMinute = ChatColor.DARK_GREEN + "Game starting in %s minute, %s seconds";
    private String timeFormatMotdSecondsAndMinutes = ChatColor.DARK_GREEN + "Game starting in %s minutes, %s seconds";
    private String timeFormatNoTime = "No time at all";
    private String timeFormatSecond = "%s second";
    private String timeFormatSecondAndMinute = "%s minute, %s second";
    private String timeFormatSecondAndMinutes = "%s minutes, %s second";
    private String timeFormatSeconds = "%s seconds";
    private String timeFormatSecondsAndMinute = "%s minute, %s seconds";
    private String timeFormatSecondsAndMinutes = "%s minutes, %s seconds";

    public TranslationManager() {
        configFile = new File(HungergamesApi.getHungergames().getDataFolder(), "translation.yml");
        config = new YamlConfiguration();
        try {
            load();
            loadConfig();
            Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                public void run() {

                    /**
                     * Touch this and you better leave this entire plugin alone because I didn't give you permission to modify
                     * this. By changing the creatorMessage to something which doesn't refer players to the plugin itself. You are
                     * going against my wishes.
                     */
                    boolean foundMe = false;
                    for (String message : new String[] {
                            String.format(getCommandCreator(), "libraryaddict", "http://ow.ly/kWBpO").toLowerCase(),
                            getKickMessageWon().toLowerCase(),
                            getShouldIMessagePlayersWhosePlugin() ? getMessagePlayerWhosePlugin().toLowerCase() : "" })
                        if (message.contains("libraryaddict") || message.contains("ow.ly/kwbpo")
                                || message.contains("spigotmc.org/resources/libs-hungergames.55")) {
                            foundMe = true;
                            break;
                        }
                    if (!foundMe)
                        Bukkit.getScheduler().scheduleSyncRepeatingTask(HungergamesApi.getHungergames(), new Runnable() {
                            public void run() {
                                Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "[Hungergames] " + ChatColor.AQUA
                                        + "This plugin was created by libraryaddict! Download it at http://ow.ly/kWBpO");
                            }
                        }, 20 * 60 * 10, 20 * 60 * 10);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getBroadcastFeastBegun() {
        return broadcastFeastBegun;
    }

    public String getBroadcastFeastStartingCompassMessage() {
        return broadcastFeastStartingCompassMessage;
    }

    public String getBroadcastFeastStartingIn() {
        return broadcastFeastStartingIn;
    }

    public String getBroadcastGameStartedMessage() {
        return broadcastGameStartedMessage;
    }

    public String getBroadcastGameStartingIn() {
        return broadcastGameStartingIn;
    }

    public String getBroadcastInvincibilityWornOff() {
        return broadcastInvincibilityWornOff;
    }

    public String getBroadcastInvincibiltyWearsOffIn() {
        return broadcastInvincibiltyWearsOffIn;
    }

    public String getBroadcastNotEnoughPlayers() {
        return broadcastNotEnoughPlayers;
    }

    public String getBroadcastWinnerWon() {
        return broadcastWinnerWon;
    }

    public String getCommandBuildChangedOwnBuild() {
        return commandBuildChangedOwnBuild;
    }

    public String getCommandBuildNoPermission() {
        return commandBuildNoPermission;
    }

    public String getCommandBuildPlayerDoesntExist() {
        return commandBuildPlayerDoesntExist;
    }

    public String getCommandBuildRecieverBuildChanged() {
        return commandBuildRecieverBuildChanged;
    }

    public String getCommandBuildSenderBuildChanged() {
        return commandBuildSenderBuildChanged;
    }

    public String getCommandBuyKitAlreadyOwn() {
        return commandBuyKitAlreadyOwn;
    }

    public String getCommandBuyKitCantAfford() {
        return commandBuyKitCantAfford;
    }

    public String getCommandBuyKitCantBuyKit() {
        return commandBuyKitCantBuyKit;
    }

    public String getCommandBuyKitKitsNotLoaded() {
        return commandBuyKitKitsNotLoaded;
    }

    public String getCommandBuyKitMysqlNotEnabled() {
        return commandBuyKitMysqlNotEnabled;
    }

    public String getCommandBuyKitNoArgs() {
        return commandBuyKitNoArgs;
    }

    public String getCommandBuyKitPurchasedKit() {
        return commandBuyKitPurchasedKit;
    }

    public String getCommandChunkCooldown() {
        return commandChunkCooldown;
    }

    public String getCommandChunkLoggerReloadingChunks() {
        return commandChunkLoggerReloadingChunks;
    }

    public String getCommandChunkLogReloadingChunks() {
        return commandChunkLoggerReloadingChunks;
    }

    public String getCommandChunkReloadedChunks() {
        return commandChunkReloadedChunks;
    }

    public String getCommandCreator() {
        return commandCreator;
    }

    public String getCommandFeastHappened() {
        return commandFeastHappened;
    }

    public String getCommandFeastNotHappened() {
        return commandFeastNotHappened;
    }

    public String getCommandForceFeastGenerated() {
        return commandForceFeastGenerated;
    }

    public String getCommandForceFeastNoPermission() {
        return commandForceFeastNoPermission;
    }

    public String getCommandForceFeastNotANumber() {
        return commandForceFeastNotANumber;
    }

    public String getCommandForceFeastStupidInput() {
        return commandForceFeastStupidInput;
    }

    public String getCommandForceStartAlreadyStarted() {
        return commandForceStartAlreadyStarted;
    }

    public String getCommandForceStartChangedCountdownTime() {
        return commandForceStartChangedCountdownTime;
    }

    public String getCommandForceStartNoPermission() {
        return commandForceStartNoPermission;
    }

    public String getCommandForceStartNotANumber() {
        return commandForceStartNotANumber;
    }

    public String getCommandForceTimeBroadcast() {
        return commandForceTimeBroadcast;
    }

    public String getCommandForceTimeInfo() {
        return commandForceTimeInfo;
    }

    public String getCommandForceTimeNoPermission() {
        return commandForceTimeNoPermission;
    }

    public String getCommandForceTimeNotANumber() {
        return commandForceTimeNotANumber;
    }

    public String getCommandGotoFeastFailed() {
        return commandGotoFeastFailed;
    }

    public String getCommandGotoNameOfFeast() {
        return commandGotoNameOfFeast;
    }

    public String getCommandGotoNotEnoughArgs() {
        return commandGotoNotEnoughArgs;
    }

    public String getCommandGotoNotSpectator() {
        return commandGotoNotSpectator;
    }

    public String getCommandGotoPlayerDoesntExist() {
        return commandGotoPlayerDoesntExist;
    }

    public String getCommandInvisHide() {
        return commandInvisHide;
    }

    public String getCommandInvisHideAll() {
        return commandInvisHideAll;
    }

    public String getCommandInvisHidePlayerFail() {
        return commandInvisHidePlayerFail;
    }

    public String getCommandInvisHidePlayerNoArgs() {
        return commandInvisHidePlayerNoArgs;
    }

    public String getCommandInvisHidePlayerSuccess() {
        return commandInvisHidePlayerSuccess;
    }

    public String getCommandInvisNameOfHide() {
        return commandInvisNameOfHide;
    }

    public String getCommandInvisNameOfHideAll() {
        return commandInvisNameOfHideAll;
    }

    public String getCommandInvisNameOfHidePlayer() {
        return commandInvisNameOfHidePlayer;
    }

    public String getCommandInvisNameOfShow() {
        return commandInvisNameOfShow;
    }

    public String getCommandInvisNameOfShowAll() {
        return commandInvisNameOfShowAll;
    }

    public String getCommandInvisNameOfShowPlayer() {
        return commandInvisNameOfShowPlayer;
    }

    public String getCommandInvisNoPermission() {
        return commandInvisNoPermission;
    }

    public String getCommandInvisNotEnoughArguments() {
        return commandInvisNotEnoughArguments;
    }

    public String getCommandInvisShow() {
        return commandInvisShow;
    }

    public String getCommandInvisShowAll() {
        return commandInvisShowAll;
    }

    public String getCommandInvisShowPlayerFail() {
        return commandInvisShowPlayerFail;
    }

    public String getCommandInvisShowPlayerNoArgs() {
        return commandInvisShowPlayerNoArgs;
    }

    public String getCommandInvisShowPlayerSuccess() {
        return commandInvisShowPlayerSuccess;
    }

    public String getCommandKillMurderMessage() {
        return commandKillMurderMessage;
    }

    public String getCommandKillNotAlive() {
        return commandKillNotAlive;
    }

    public String getCommandKillPlayerNotFound() {
        return commandKillPlayerNotFound;
    }

    public String getCommandKillSomeoneNoPermission() {
        return commandKillSomeoneNoPermission;
    }

    public String getCommandKillUseSuicide() {
        return commandKillUseSuicide;
    }

    public String getCommandKitAlreadyUsing() {
        return commandKitAlreadyUsing;
    }

    public String getCommandKitGameAlreadyStarted() {
        return commandKitGameAlreadyStarted;
    }

    public String getCommandKitInfoDefineKitName() {
        return commandKitInfoDefineKitName;
    }

    public String getCommandKitItemsDefineKitName() {
        return commandKitItemsDefineKitName;
    }

    public String getCommandKitItemsItemWithEnchant() {
        return commandKitItemsItemWithEnchant;
    }

    public String getCommandKitItemsItemWithEnchants() {
        return commandKitItemsItemWithEnchants;
    }

    public String getCommandKitKitDoesntExist() {
        return commandKitKitDoesntExist;
    }

    public String getCommandKitKitsDivider() {
        return commandKitKitsDivider;
    }

    public String getCommandKitNoPermission() {
        return commandKitNoPermission;
    }

    public String getCommandKitNowUsingKit() {
        return commandKitNowUsingKit;
    }

    public String getCommandMessageEventCancelled() {
        return commandMessageEventCancelled;
    }

    public String getCommandMessageNoArgs() {
        return commandMessageNoArgs;
    }

    public String getCommandMessageNoReceiver() {
        return commandMessageNoReceiver;
    }

    public String getCommandMessagePlayerNoArgs() {
        return commandMessagePlayerNoArgs;
    }

    public String getCommandMessageReceiveMessage() {
        return commandMessageReceiveMessage;
    }

    public String getCommandMessageSendMessage() {
        return commandMessageSendMessage;
    }

    public String getCommandPlayers() {
        return commandPlayers;
    }

    public String getCommandPlayersTimeStatusStarted() {
        return commandPlayersTimeStatusStarted;
    }

    public String getCommandPlayersTimeStatusStarting() {
        return commandPlayersTimeStatusStarting;
    }

    public String getCommandReplyEventCancelled() {
        return commandReplyEventCancelled;
    }

    public String getCommandReplyNoArgs() {
        return commandReplyNoArgs;
    }

    public String getCommandReplyNoReceiver() {
        return commandReplyNoReceiver;
    }

    public String getCommandReplyReceiveReply() {
        return commandReplyReceiveReply;
    }

    public String getCommandReplyReceiverLeft() {
        return commandReplyReceiverLeft;
    }

    public String getCommandReplySendReply() {
        return commandReplySendReply;
    }

    public String getCommandRideNameOfRideall() {
        return commandRideNameOfRideall;
    }

    public String getCommandRideRideAll() {
        return commandRideRideAll;
    }

    public String getCommandRideToggle() {
        return commandRideToggle;
    }

    public String getCommandSpawnPointingToSpawn() {
        return commandSpawnPointingToSpawn;
    }

    public String getCommandSuicideAssistedDeathMessage() {
        return commandSuicideAssistedDeathMessage;
    }

    public String getCommandSuicideDoesntExist() {
        return commandSuicideDoesntExist;
    }

    public String getCommandSuicideKillMessage() {
        return commandSuicideKillMessage;
    }

    public String getCommandSuicideNoPermission() {
        return commandSuicideNoPermission;
    }

    public String getCommandSuicideNotAlive() {
        return commandSuicideNotAlive;
    }

    public String getCommandTimeStatusStarted() {
        return commandTimeStatusStarted;
    }

    public String getCommandTimeStatusStarting() {
        return commandTimeStatusStarting;
    }

    public String getGameStartedMotd() {
        return gameStartedMotd;
    }

    public String getInventoryDontOwnKit() {
        return inventoryDontOwnKit;
    }

    public String getInventoryOwnKit() {
        return inventoryOwnKit;
    }

    public String[] getItemKitSelectorBackDescription() {
        return itemKitSelectorBackDescription;
    }

    public String getItemKitSelectorBackName() {
        return itemKitSelectorBackName;
    }

    public String[] getItemKitSelectorDescription() {
        return itemKitSelectorDescription;
    }

    public String[] getItemKitSelectorForwardsDescription() {
        return itemKitSelectorForwardsDescription;
    }

    public String getItemKitSelectorForwardsName() {
        return itemKitSelectorForwardsName;
    }

    public String getItemKitSelectorName() {
        return itemKitSelectorName;
    }

    public String[] getItemSpectatorInventoryBackDescription() {
        return itemSpectatorInventoryBackDescription;
    }

    public String getItemSpectatorInventoryBackName() {
        return itemSpectatorInventoryBackName;
    }

    public String[] getItemSpectatorInventoryForwardsDescription() {
        return itemSpectatorInventoryForwardsDescription;
    }

    public String getItemSpectatorInventoryForwardsName() {
        return itemSpectatorInventoryForwardsName;
    }

    public String getKickDeathMessage() {
        return kickDeathMessage;
    }

    public String getKickGameFull() {
        return kickGameFull;
    }

    public String getKickGameShutdownUnexpected() {
        return kickGameShutdownUnexpected;
    }

    public String getKickMessageWon() {
        return kickMessageWon;
    }

    public String getKickNobodyWonMessage() {
        return kickNobodyWonMessage;
    }

    public String getKickSpectatorsDisabled() {
        return kickSpectatorsDisabled;
    }

    public String getKillMessageFellToDeath() {
        return killMessageFellToDeath;
    }

    public String getKillMessageFormatPlayerKit() {
        return killMessageFormatPlayerKit;
    }

    public String getKillMessageKilledByBorder() {
        return killMessageKilledByBorder;
    }

    public String getKillMessageLeavingGame() {
        return killMessageLeavingGame;
    }

    public String getKillMessageNoKit() {
        return killMessageNoKit;
    }

    public String[] getKillMessages() {
        return killMessages;
    }

    public String getKitDescriptionDefault() {
        return kitDescriptionDefault;
    }

    public String getLoggerAbilityAlreadyExists() {
        return loggerAbilityAlreadyExists;
    }

    public String getLoggerAbilityMissingValue() {
        return loggerAbilityMissingValue;
    }

    public String getLoggerAddAbility() {
        return loggerAddAbility;
    }

    public String getLoggerChangedHeightLimit() {
        return loggerChangedHeightLimit;
    }

    public String getLoggerChangedIDisguiseConfig() {
        return loggerChangedIDisguiseConfig;
    }

    public String getLoggerChangedSpawnRadius() {
        return loggerChangedSpawnRadius;
    }

    public String getLoggerCommandsMissingValue() {
        return loggerCommandsMissingValue;
    }

    public String getLoggerCreatingAbilitiesConfig() {
        return loggerCreatingAbilitiesConfig;
    }

    public String getLoggerCreatingCommandsConfig() {
        return loggerCreatingCommandsConfig;
    }

    public String getLoggerCreatingNamesConfig() {
        return loggerCreatingNamesConfig;
    }

    private String getLoggerCreatingTranslationConfig() {
        return loggerCreatingTranslationConfig;
    }

    public String getLoggerDependencyNotFound() {
        return loggerDependencyNotFound;
    }

    public String getLoggerDisabledEnd() {
        return loggerDisabledEnd;
    }

    public String getLoggerDisabledNether() {
        return loggerDisabledNether;
    }

    public String getLoggerErrorWhileLoadingAbility() {
        return loggerErrorWhileLoadingAbility;
    }

    public String getLoggerErrorWhileLoadingCommands() {
        return loggerErrorWhileLoadingCommands;
    }

    private String getLoggerErrorWhileLoadingTranslation() {
        return loggerErrorWhileLoadingTranslation;
    }

    public String getLoggerErrorWhileParsingItemStack() {
        return loggerErrorWhileParsingItemStack;
    }

    public String getLoggerErrorWhileRegisteringPlayerForAbility() {
        return loggerErrorWhileRegisteringPlayerForAbility;
    }

    public String getLoggerFailedToChangIDisguiseConfig() {
        return loggerFailedToChangIDisguiseConfig;
    }

    public String getLoggerFailedToCheckUpdate() {
        return loggerFailedToCheckUpdate;
    }

    public String getLoggerFoundAbilityInPackage() {
        return loggerFoundAbilityInPackage;
    }

    public String getLoggerFoundCommandInPackage() {
        return loggerFoundCommandInPackage;
    }

    public String getLoggerKitAlreadyExists() {
        return loggerKitAlreadyExists;
    }

    public String getLoggerLoadAbilitiesInPackage() {
        return loggerLoadAbilitiesInPackage;
    }

    public String getLoggerLoadCommandsInPackage() {
        return loggerLoadCommandsInPackage;
    }

    private String getLoggerLoadTranslationConfig() {
        return loggerLoadTranslationConfig;
    }

    public String getLoggerMapConfigChangedBorderSize() {
        return loggerMapConfigChangedBorderSize;
    }

    public String getLoggerMapConfigLoaded() {
        return loggerMapConfigLoaded;
    }

    public String getLoggerMapConfigNotFound() {
        return loggerMapConfigNotFound;
    }

    public String getLoggerMapConfigNowLoading() {
        return loggerMapConfigNowLoading;
    }

    public String getLoggerMetricsMessage() {
        return loggerMetricsMessage;
    }

    public String getLoggerMySqlClosing() {
        return loggerMySqlClosing;
    }

    public String getLoggerMySqlClosingError() {
        return loggerMySqlClosingError;
    }

    public String getLoggerMySqlConnecting() {
        return loggerMySqlConnecting;
    }

    public String getLoggerMySqlConnectingError() {
        return loggerMySqlConnectingError;
    }

    public String getLoggerMySqlErrorLoadPlayer() {
        return loggerMySqlErrorLoadPlayer;
    }

    public String getLoggerNoMapsFound() {
        return loggerNoMapsFound;
    }

    public String getLoggerNowAttemptingToLoadAMap() {
        return loggerNowAttemptingToLoadAMap;
    }

    public String getLoggerShutdownCancelled() {
        return loggerShutdownCancelled;
    }

    public String getLoggerShuttingDown() {
        return loggerShuttingDown;
    }

    public String getLoggerSucessfullyLoadedMap() {
        return loggerSucessfullyLoadedMap;
    }

    public String getLoggerTranslationMissingValue() {
        return loggerTranslationMissingValue;
    }

    public String getLoggerUnrecognisedItemId() {
        return loggerUnrecognisedItemId;
    }

    public String getLoggerWaitingForLoadGamerToComplete() {
        return loggerWaitingForLoadGamerToComplete;
    }

    public String getMessagePlayerApproachingBorder() {
        return messagePlayerApproachingBorder;
    }

    public String getMessagePlayerHasHealthAndHunger() {
        return messagePlayerHasHealthAndHunger;
    }

    public String getMessagePlayerKitDesciprionPrice() {
        return messagePlayerKitDesciprionPrice;
    }

    public String getMessagePlayerKitDesciprionPriceFree() {
        return messagePlayerKitDesciprionPriceFree;
    }

    public String getMessagePlayerKitDesciprionPriceUnbuyable() {
        return messagePlayerKitDesciprionPriceUnbuyable;
    }

    public String getMessagePlayerKitDesciptionId() {
        return messagePlayerKitDesciptionId;
    }

    public String getMessagePlayerKitDescriptionDoesntExist() {
        return messagePlayerKitDescriptionDoesntExist;
    }

    public String getMessagePlayerKitDescriptionName() {
        return messagePlayerKitDescriptionName;
    }

    public String getMessagePlayerKitDescritionMoreInfo() {
        return messagePlayerKitDescritionMoreInfo;
    }

    public String getMessagePlayerSendKitItemsDoesntExist() {
        return messagePlayerSendKitItemsDoesntExist;
    }

    public String getMessagePlayerSendKitItemsKitBoots() {
        return messagePlayerSendKitItemsKitBoots;
    }

    public String getMessagePlayerSendKitItemsKitChestplate() {
        return messagePlayerSendKitItemsKitChestplate;
    }

    public String getMessagePlayerSendKitItemsKitHelmet() {
        return messagePlayerSendKitItemsKitHelmet;
    }

    public String getMessagePlayerSendKitItemsKitLeggings() {
        return messagePlayerSendKitItemsKitLeggings;
    }

    public String getMessagePlayerSendKitItemsKitName() {
        return messagePlayerSendKitItemsKitName;
    }

    public String getMessagePlayerSendKitItemsNoItems() {
        return messagePlayerSendKitItemsNoItems;
    }

    public String getMessagePlayerSendKitItemsOtherItems() {
        return messagePlayerSendKitItemsOtherItems;
    }

    public String getMessagePlayerShowKitsCurrentSelectedKit() {
        return messagePlayerShowKitsCurrentSelectedKit;
    }

    public String getMessagePlayerShowKitsHisKits() {
        return messagePlayerShowKitsHisKits;
    }

    public String getMessagePlayerShowKitsNoKit() {
        return messagePlayerShowKitsNoKit;
    }

    public String getMessagePlayerShowKitsNoKits() {
        return messagePlayerShowKitsNoKits;
    }

    public String getMessagePlayerShowKitsOtherKits() {
        return messagePlayerShowKitsOtherKits;
    }

    public String getMessagePlayerShowKitsUseKitInfo() {
        return messagePlayerShowKitsUseKitInfo;
    }

    public String getMessagePlayerTrack() {
        return messagePlayerTrack;
    }

    public String getMessagePlayerTrackNoVictim() {
        return messagePlayerTrackNoVictim;
    }

    public String getMessagePlayerUpdateAvailable() {
        return messagePlayerUpdateAvailable;
    }

    public String getMessagePlayerWarningForgeUnstableEnchants() {
        return messagePlayerWarningForgeUnstableEnchants;
    }

    public String getMessagePlayerWhosePlugin() {
        return messagePlayerWhosePlugin;
    }

    public String getScoreboardBorderSize() {
        return scoreboardBorderSize;
    }

    public String getScoreboardFeastStartingIn() {
        return scoreboardFeastStartingIn;
    }

    public String getScoreBoardGameStartingIn() {
        return scoreBoardGameStartingIn;
    }

    public String getScoreboardInvincibleRemaining() {
        return scoreboardInvincibleRemaining;
    }

    public String getScoreboardPlayersLength() {
        return scoreboardPlayersLength;
    }

    public String getScoreboardStageFeastHappened() {
        return scoreboardStageFeastHappened;
    }

    public String getScoreboardStageFeastHappening() {
        return scoreboardStageFeastHappening;
    }

    public String getScoreboardStageFighting() {
        return scoreboardStageFighting;
    }

    public String getScoreboardStageInvincibility() {
        return scoreboardStageInvincibility;
    }

    public String getScoreboardStagePreFeast() {
        return scoreboardStagePreFeast;
    }

    public String getScoreboardStagePreGame() {
        return scoreboardStagePreGame;
    }

    public String getSelectKitInventoryTitle() {
        return selectKitInventoryTitle;
    }

    public boolean getShouldIMessagePlayersWhosePlugin() {
        return shouldIMessagePlayersWhosePlugin;
    }

    public String getSpectatorHeadKills() {
        return spectatorHeadKills;
    }

    public String getSpectatorHeadKit() {
        return spectatorHeadKit;
    }

    public String getSpectatorInventoryTitle() {
        return spectatorInventoryTitle;
    }

    public String getTimeFormatMinute() {
        return timeFormatMinute;
    }

    public String getTimeFormatMinutes() {
        return timeFormatMinutes;
    }

    public String getTimeFormatMotdMinute() {
        return timeFormatMotdMinute;
    }

    public String getTimeFormatMotdMinutes() {
        return timeFormatMotdMinutes;
    }

    public String getTimeFormatMotdSecond() {
        return timeFormatMotdSecond;
    }

    public String getTimeFormatMotdSecondAndMinute() {
        return timeFormatMotdSecondAndMinute;
    }

    public String getTimeFormatMotdSecondAndMinutes() {
        return timeFormatMotdSecondAndMinutes;
    }

    public String getTimeFormatMotdSeconds() {
        return timeFormatMotdSeconds;
    }

    public String getTimeFormatMotdSecondsAndMinute() {
        return timeFormatMotdSecondsAndMinute;
    }

    public String getTimeFormatMotdSecondsAndMinutes() {
        return timeFormatMotdSecondsAndMinutes;
    }

    public String getTimeFormatNoTime() {
        return timeFormatNoTime;
    }

    public String getTimeFormatSecond() {
        return timeFormatSecond;
    }

    public String getTimeFormatSecondAndMinute() {
        return timeFormatSecondAndMinute;
    }

    public String getTimeFormatSecondAndMinutes() {
        return timeFormatSecondAndMinutes;
    }

    public String getTimeFormatSeconds() {
        return timeFormatSeconds;
    }

    public String getTimeFormatSecondsAndMinute() {
        return timeFormatSecondsAndMinute;
    }

    public String getTimeFormatSecondsAndMinutes() {
        return timeFormatSecondsAndMinutes;
    }

    public boolean isNewFile() {
        return newFile;
    }

    public void load() throws Exception {
        try {
            if (!configFile.exists())
                save();
            else
                newFile = false;
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(
                    "You have setup your translation configuration wrong. Please make sure you are properly setting up every single line"
                            + "\nProblems can be because of single quotes in the middle of the string, Not surrounding the string with single quotes."
                            + "\nFor more information, Please look up yaml configurations and how to properly do them");
        }
    }

    public void loadConfig() {
        try {
            boolean saveConfig = false;
            System.out.print(getLoggerLoadTranslationConfig());
            try {
                boolean modified = false;
                for (Field field : getClass().getDeclaredFields()) {
                    if (!Modifier.isTransient(field.getModifiers()))
                        try {
                            Object value = config.get(field.getName());
                            if (value == null) {
                                value = field.get(this);
                                if (value instanceof String[]) {
                                    String[] strings = (String[]) value;
                                    String[] newStrings = new String[strings.length];
                                    for (int i = 0; i < strings.length; i++) {
                                        newStrings[i] = strings[i].replace("\n", "\\n").replace("§", "&");
                                    }
                                    config.set(field.getName(), newStrings);
                                } else {
                                    if (value instanceof String)
                                        value = ((String) value).replace("\n", "\\n").replace("§", "&");
                                    config.set(field.getName(), value);
                                }
                                modified = true;
                                if (!newFile)
                                    System.out.print(String.format(getLoggerTranslationMissingValue(), field.getName()));
                            } else if (field.getType().isArray() && value.getClass() == ArrayList.class) {
                                List<Object> array = (List<Object>) value;
                                value = array.toArray(new String[array.size()]);
                            }
                            if (value instanceof String) {
                                value = ChatColor.translateAlternateColorCodes('&', (String) value).replace("\\n", "\n");
                            }
                            if (value instanceof String[]) {
                                String[] strings = (String[]) value;
                                for (int i = 0; i < strings.length; i++)
                                    strings[i] = ChatColor.translateAlternateColorCodes('&', strings[i]).replace("\\n", "\n");
                                value = strings;
                            }
                            if (field.getType().getSimpleName().equals("float") && value.getClass() == Double.class) {
                                field.set(this, ((float) (double) (Double) value));
                            } else
                                field.set(this, value);
                        } catch (Exception e) {
                            System.out.print(String.format(getLoggerErrorWhileLoadingTranslation(), e.getMessage()));
                        }
                }
                if (modified) {
                    saveConfig = true;
                }
            } catch (Exception e) {
                System.out.print(String.format(getLoggerErrorWhileLoadingTranslation(), e.getMessage()));
            }
            if (saveConfig)
                save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            if (!configFile.exists()) {
                Bukkit.getLogger().info(getLoggerCreatingTranslationConfig());
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                newFile = true;
            }
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
