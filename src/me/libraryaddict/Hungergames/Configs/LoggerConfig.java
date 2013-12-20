package me.libraryaddict.Hungergames.Configs;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LoggerConfig extends BaseConfig {
    private String abilityAlreadyExists = "[Hungergames] Ability %s already exists!";
    private String abilityMissingConfigValue = "[Hungergames] Added ability '%s' missing config value '%s'";
    private String addedMissingConfigValue = "[Hungergames] Added missing config value '%s' for config '%s'";
    private String changedWorldHeight = "[Hungergames] Changed the worlds height to %s";
    private String chunksGenerated = "[Hungergames] Generated %s chunks";
    private String creatingConfigFile = "[Hungergames] Creating config file %s.yml";
    private String dependencyNotFound = "[Hungergames] Dependency %s not found";
    private String disabledEnd = "[Hungergames] Disabled the end";
    private String disabledNether = "[Hungergames] Disabled the nether";
    private String disabledSpawnRadius = "[Hungergames] Changed spawn radius to 0";
    private String errorAbilityDoesntExist = "[Hungergames] %s's kit attempted to use the ability %s which does not exist";
    private String errorWhenCheckingForUpdate = "[Hungergames] Error while checking for a update - %s";
    private String errorWhileLoadingAbility = "[Hungergames] Error while loading ability %s - %s";
    private String errorWhileLoadingCommand = "[Hungergames] Error while loading command %s - %s";
    private String errorWhileLoadingConfig = "[Hungergames] Error while loading config %s.yml - %s";
    private String errorWhileLoadingConfigValue = "[Hungergames] Error while loading config %s.yml, %s threw a error - %s";
    private String errorWhileLoadingSpawns = "[Hungergames] Error while loading spawns, Spawn %s is missing config value %s";
    private String errorWhileParsingItemStack = "[Hungergames] Error while parsing itemstack line %s, %s";
    private String generatingChunks = "[Hungergames] Generating chunks.. %s";
    private String kitAlreadyExists = "[Hungergames] Kit %s already exists!";
    private String loadAbilitiesPackage = "[Hungergames] Adding abilities from plugin %s in package %s";
    private String loadCommandsPackage = "[Hungergames] Adding commands from plugin %s in package %s";
    private String loadedSpawnOutsideBorder = "[Hungergames] You may be interested to know that the spawn %s is outside the border."
            + " Try looking at turning 'forcedCords' in the main config off";
    private String loadedSpawns = "[Hungergames] Loaded %s player spawns";
    private String loadedSpawnsConfig = "[Hungergames] Loaded %s spawns";
    private String loadingConfigFile = "[Hungergames] Loading config file %s.yml";
    private String loadingSpawns = "[Hungergames] Loading player spawns";
    private String loadSpawnsConfig = "[Hungergames] Loading the spawns";
    private String loadSpawnsConfigError = "[Hungergames] Error while loading spawns, spawn %s is missing configuration %s";
    private String loadSpawnsConfigNotFound = "[Hungergames] Spawns config not found";
    private String mapConfigChangedBorderCloseInRate = "[Hungergames] Map config - Changed border close in rate to %s";
    private String mapConfigChangedBorderSize = "[Hungergames] Map config - Changed border size to %s";
    private String mapConfigChangedRoundedBorder = "[Hungergames] Map config - Changed rounded border to %s";
    private String mapConfigChangedTimeOfDay = "[Hungergames] Map config - Changed time of day game starts as %s";
    private String mapConfigLoaded = "[Hungergames] Successfully loaded map config";
    private String mapConfigNotFound = "[Hungergames] Map config not found";
    private String mapConfigNowLoading = "[Hungergames] Now loading map config";
    private String metricsMessage = "[Hungergames] Dangit. Think you can opt back into metrics for me? I do want to see how popular my plugin is..";
    private String mySqlClosing = "[%s] Disconnecting from MySQL database...";
    private String mySqlClosingError = "[%s] Error while closing the connection...";
    private String mySqlConnecting = "[%s] Connecting to MySQL database..";
    private String mySqlConnectingError = "[%s] Error while connecting to MySQL. %s";
    private String mySqlErrorLoadPlayer = "[PlayerJoinThread] Error while loading player %s - %s";
    private String noMapsFound = "[Hungergames] No maps found in %s";
    private String NowAttemptingToLoadAMap = "[Hungergames] Now attempting to load a map from the path %s";
    private String restoredCommandsMissingConfigValue = "[Hungergames] Restored missing config '%s' for command '%s'";
    private String shutdownCancelled = "[Hungergames] Shutdown event was cancelled by some plugin!";
    private String shuttingDown = "[Hungergames] Hungergames is now shutting the server down!";
    private String successfullyLoadedMap = "[Hungergames] Successfully loaded map %s";
    private String unrecognisedItemId = "[Hungergames] Failed to recognise item ID %s";

    public LoggerConfig() {
        super("errors");
    }

}
