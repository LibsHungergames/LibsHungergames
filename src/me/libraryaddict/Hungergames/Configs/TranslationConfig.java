package me.libraryaddict.Hungergames.Configs;

import java.io.File;
import java.util.HashMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.libraryaddict.Hungergames.Managers.ScoreboardManager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

@Data
@EqualsAndHashCode(callSuper = false)
public class TranslationConfig extends BaseConfig {
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
    private String commandKillNotAlive = ChatColor.RED + "The player is not alive";
    private String commandKillPlayerNotFound = ChatColor.RED + "The player doesn't exist";
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
    private String commandKitMenuGameInProgress = ChatColor.RED + "The game is already in progress!";
    private String commandKitNoPermission = ChatColor.RED + "You do not have access to this kit!";
    private String commandKitNowUsingKit = ChatColor.AQUA + "Now using kit %s!";
    private String commandMeSpectating = ChatColor.RED + "Spectators may not use this command!";
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
    private String commandRespawnGameHasntStarted = ChatColor.RED + "The game hasn't started yet!";
    private String commandRespawnNoPermission = ChatColor.RED + "You don't have permission!";
    private String commandRespawnPlayerDoesntExist = ChatColor.RED + "Player doesn't exist!";
    private String commandRespawnPlayerIsAlive = ChatColor.RED + "The player is still alive!";
    private String commandRespawnRespawnedPlayer = ChatColor.RED + "Successfully respawned %s!";
    private String commandRespawnYouHaveBeenRespawned = ChatColor.RED + "You have been respawned!";
    private String commandRideNameOfRideall = "rideall";
    private String commandRideRideAll = ChatColor.GREEN + "Giddy up horsie!";
    private String commandRideToggle = ChatColor.GREEN + "Toggled riding to %s! Yee-haw!";
    private String commandSpawnPointingToSpawn = ChatColor.YELLOW + "Compass is now pointing to spawn";
    private String commandSuicideAssistedDeathMessage = "%s was helped on the path to suicide";
    private String commandSuicideDoesntExist = ChatColor.RED + "The player requested cannot be found";
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
            ChatColor.LIGHT_PURPLE + "back a page" };
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
    private String killMessageFormatPlayerKit = ChatColor.RED + "%Player%" + ChatColor.DARK_RED + "(" + ChatColor.RED + "%Kit%"
            + ChatColor.DARK_RED + ")";
    private String killMessageKilledByBorder = "%s believed the rumors of a better life beyond the border";
    private String killMessageLeavingGame = "%s was slaughtered for leaving the game";
    private String killMessageNoKit = "None";
    private String[] killMessages = new String[] { "%Killer% dual wielded a %Weapon% and laid waste upon %Killed%",
            "%Killer% slid a %Weapon% into %Killed% when their back was turned",
            "%Killed% was murdered in cold blood by %Killer% with a %Weapon%",
            "%Killed% gasped their last breath as %Killer% savagely stabbed him with a %Weapon%",
            "%Killed% screamed in agony as they were bludgeoned over the head with a %Weapon% by %Killer%",
            "%Killed% was killed by %Killer% with a %Weapon%",
            "%Killer% gave %Killed% a helping hand into death's sweet embrace with their trusty %Weapon%",
            "%Killer%'s %Weapon% could not resist killing %Killed%", "%Killer% and their trusty %Weapon% slew %Killed%",
            "%Killed%'s weapon could not stand up against %Killer%'s %Weapon% of doom!" };
    private String kitDescriptionDefault = "No description was provided for this kit";

    private String messageMobHasHealth = ChatColor.RED + "The %s has %s/%s health";
    private String messagePlayerApproachingBorder = ChatColor.YELLOW + "You are approaching the border!";
    private String messagePlayerChunksGenerating = ChatColor.YELLOW + "Chunks are currently generating!\n\nCheck back soon!";
    private String messagePlayerHasHealthAndHunger = ChatColor.DARK_RED + "%1$2s has" + ChatColor.RED + " %2$2s/%maxhp% health"
            + ChatColor.DARK_RED + "\n%1$2s has" + ChatColor.RED + " %3$2s/20 hunger" + ChatColor.DARK_RED
            + "\n%1$2s is using kit:" + ChatColor.RED + " %4$2s";
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
            + ChatColor.GREEN + "LibsHungergames %s" + ChatColor.DARK_GREEN + " by " + ChatColor.GREEN + "%s";
    private String scoreboardBorderSize = ChatColor.GOLD + "BorderSize:";
    private String scoreboardFeastStartingIn = ChatColor.GOLD + "Feast in:";
    private String scoreBoardGameStartingIn = ChatColor.GOLD + "Starting in:";
    private String scoreboardInvincibleRemaining = ChatColor.GOLD + "Invincible:";
    private String scoreboardPlayersLength = ChatColor.GREEN + "Players:";
    private String scoreboardStagePreGame = ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Pregame";
    private HashMap<Integer, String> scoreboardStages = new HashMap<Integer, String>();
    private String selectKitInventoryTitle = ChatColor.DARK_RED + "Select Kit";
    private String spectatorHeadKills = ChatColor.GREEN + "Kills: " + ChatColor.BLUE + "%s";
    private String spectatorHeadKit = ChatColor.GREEN + "Kit: " + ChatColor.BLUE + "%s";
    private String spectatorInventoryFeastDescription = ChatColor.GREEN + "Click this to be teleported\n" + ChatColor.GREEN
            + "to the feast!";
    private String spectatorInventoryFeastName = ChatColor.BLUE + "Feast";
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

    public TranslationConfig() {
        super("translations");
        scoreboardStages.put(0, ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Invincibility");
        scoreboardStages.put(2 * 60, ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Fighting");
        scoreboardStages.put(25 * 60, ChatColor.DARK_AQUA + "Stage:" + ChatColor.AQUA + " Finishing Up");
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        for (int i : this.getScoreboardStages().keySet()) {
            ScoreboardManager.registerStage(i, getScoreboardStages().get(i));
        }
    }
}
