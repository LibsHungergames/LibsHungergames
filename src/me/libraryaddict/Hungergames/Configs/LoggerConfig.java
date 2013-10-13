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
    private String generatingChunks = "[Hungergames] Generating chunks.. %s";
    private String kitAlreadyExists = "[Hungergames] Kit %s already exists!";
    private String loadAbilitiesPackage = "[Hungergames] Adding abilities from plugin %s in package %s";
    private String loadCommandsPackage = "[Hungergames] Adding commands from plugin %s in package %s";
    private String loadedSpawns = "[Hungergames] Loaded %s player spawns";
    private String loadingConfigFile = "[Hungergames] Loading config file %s.yml";
    private String loadingSpawns = "[Hungergames] Loading player spawns";
    private String noMapsFound = "[Hungergames] No maps found in %s";
    private String restoredCommandsMissingConfigValue = "[Hungergames] Restored missing config '%s' for command '%s'";
    private String successfullyLoadedMap = "[Hungergames] Successfully loaded map %s";

    public LoggerConfig() {
        super("errors");
    }

}
