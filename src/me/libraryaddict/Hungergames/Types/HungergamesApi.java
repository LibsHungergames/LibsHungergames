package me.libraryaddict.Hungergames.Types;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Interfaces.ChestManager;
import me.libraryaddict.Hungergames.Managers.*;

public class HungergamesApi {
    private static AbilityConfigManager abilityConfigManager;
    private static AbilityManager abilityManager;
    private static ChatManager chatManager;
    private static ChestManager chestManager;
    private static CommandManager commandManager;
    private static ConfigManager configManager;
    private static GenerationManager generationManager;
    private static Hungergames hungergames;
    private static InventoryManager inventoryManager;
    private static KitManager kitManager;
    private static MySqlManager mySqlManager;
    private static NameManager nameManager;
    private static PlayerManager playerManager;
    private static ReflectionManager reflectionManager;

    /**
     * @return AbilityConfigManager which is used to load the configs of abilitys
     */
    public static AbilityConfigManager getAbilityConfigManager() {
        if (abilityConfigManager == null)
            abilityConfigManager = new AbilityConfigManager();
        return abilityConfigManager;
    }

    /**
     * @return AbilityManager which is used to load abilitys and handle them
     */
    public static AbilityManager getAbilityManager() {
        if (abilityManager == null)
            abilityManager = new AbilityManager();
        return abilityManager;
    }

    /**
     * @Return ChatManager which currently controls their messaging
     */
    public static ChatManager getChatManager() {
        if (chatManager == null)
            chatManager = new ChatManager();
        return chatManager;
    }

    /**
     * @return ChestManager which fills chests its given
     */
    public static ChestManager getChestManager() {
        if (chestManager == null)
            chestManager = new LibsChestManager();
        return chestManager;
    }

    /**
     * @return CommandManager manages the names of commands and registers them
     */
    public static CommandManager getCommandManager() {
        if (commandManager == null)
            commandManager = new CommandManager();
        return commandManager;
    }

    /**
     * @return Config manager which manages the settings inside config.yml
     */
    public static ConfigManager getConfigManager() {
        if (configManager == null)
            configManager = new ConfigManager();
        return configManager;
    }

    public static GenerationManager getGenerationManager() {
        if (generationManager == null)
            generationManager = new GenerationManager();
        return generationManager;
    }

    /**
     * @return The main plugin itself, Hungergames
     */
    public static Hungergames getHungergames() {
        return hungergames;
    }

    /**
     * @return Kit Selector which handles the inventory which allows them to pick a kit
     */
    public static InventoryManager getInventoryManager() {
        if (inventoryManager == null)
            inventoryManager = new InventoryManager();
        return inventoryManager;
    }

    /**
     * @return KitManager, Used for parsing items, kits and handling the kits themselves
     */
    public static KitManager getKitManager() {
        if (kitManager == null)
            kitManager = new KitManager();
        return kitManager;
    }

    /**
     * @return MySql manager which handles the mysql details and connections
     */
    public static MySqlManager getMySqlManager() {
        if (mySqlManager == null)
            mySqlManager = new MySqlManager();
        return mySqlManager;
    }

    /**
     * @return NameManager - Manages the names of itemstacks
     */
    public static NameManager getNameManager() {
        if (nameManager == null)
            nameManager = new NameManager();
        return nameManager;
    }

    /**
     * @return Player Manager which is used to get the gamer, alive players and handle kills
     */
    public static PlayerManager getPlayerManager() {
        if (playerManager == null)
            playerManager = new PlayerManager();
        return playerManager;
    }

    public static ReflectionManager getReflectionManager() {
        if (reflectionManager == null)
            reflectionManager = new ReflectionManager();
        return reflectionManager;
    }

    /**
     * @param Used
     *            by hungergames to set itself in the api
     */
    public static void init(Hungergames hunger) {
        hungergames = hunger;
    }

    /**
     * @param Sets
     *            your own chest manager which implements 'ChestManager'
     */
    public static void setChestManager(ChestManager manager) {
        chestManager = manager;
    }

    /**
     * Set the inventory manager, perhaps you want to do your own one..
     */
    public static void setInventoryManager(InventoryManager newInv) {
        inventoryManager = newInv;
    }
}
