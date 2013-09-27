package me.libraryaddict.Hungergames.Types;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Interfaces.ChestManager;
import me.libraryaddict.Hungergames.Managers.*;

public class HungergamesApi {
    private static AbilityConfigManager abilityConfigManager;
    private static AbilityManager abilityManager;
    private static ChatManager chat;
    private static TranslationManager chatManager;
    private static ChestManager cm;
    private static CommandManager commands;
    private static ConfigManager config;
    private static GenerationManager gen;
    private static Hungergames hg;
    private static InventoryManager icon;
    private static KitManager kits;
    private static MySqlManager mysql;
    private static NameManager name;
    private static PlayerManager pm;
    private static ReflectionManager rm;

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
        if (chat == null)
            chat = new ChatManager();
        return chat;
    }

    /**
     * @return ChestManager which fills chests its given
     */
    public static ChestManager getChestManager() {
        if (cm == null)
            cm = new LibsChestManager();
        return cm;
    }

    /**
     * @return CommandManager manages the names of commands and registers them
     */
    public static CommandManager getCommandManager() {
        if (commands == null)
            commands = new CommandManager();
        return commands;
    }

    /**
     * @return Config manager which manages the settings inside config.yml
     */
    public static ConfigManager getConfigManager() {
        if (config == null)
            config = new ConfigManager();
        return config;
    }

    public static GenerationManager getGenerationManager() {
        if (gen == null)
            gen = new GenerationManager();
        return gen;
    }

    /**
     * @return The main plugin itself, Hungergames
     */
    public static Hungergames getHungergames() {
        return hg;
    }

    /**
     * @return Kit Selector which handles the inventory which allows them to pick a kit
     */
    public static InventoryManager getInventoryManager() {
        if (icon == null)
            icon = new InventoryManager();
        return icon;
    }

    /**
     * @return KitManager, Used for parsing items, kits and handling the kits themselves
     */
    public static KitManager getKitManager() {
        if (kits == null)
            kits = new KitManager();
        return kits;
    }

    /**
     * @return MySql manager which handles the mysql details and connections
     */
    public static MySqlManager getMySqlManager() {
        if (mysql == null)
            mysql = new MySqlManager();
        return mysql;
    }

    /**
     * @return NameManager - Manages the names of itemstacks
     */
    public static NameManager getNameManager() {
        if (name == null)
            name = new NameManager();
        return name;
    }

    /**
     * @return Player Manager which is used to get the gamer, alive players and handle kills
     */
    public static PlayerManager getPlayerManager() {
        if (pm == null)
            pm = new PlayerManager();
        return pm;
    }

    public static ReflectionManager getReflectionManager() {
        if (rm == null)
            rm = new ReflectionManager();
        return rm;
    }

    /**
     * @return TranslationManager which is used for translations
     */
    public static TranslationManager getTranslationManager() {
        if (chatManager == null)
            chatManager = new TranslationManager();
        return chatManager;
    }

    /**
     * @param Used
     *            by hungergames to set itself in the api
     */
    public static void init(Hungergames hunger) {
        hg = hunger;
    }

    /**
     * @param Sets
     *            your own chest manager which implements 'ChestManager'
     */
    public static void setChestManager(ChestManager manager) {
        cm = manager;
    }

    /**
     * Set the inventory manager, perhaps you want to do your own one..
     */
    public static void setInventoryManager(InventoryManager newInv) {
        icon = newInv;
    }
}
