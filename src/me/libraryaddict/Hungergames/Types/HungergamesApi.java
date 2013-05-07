package me.libraryaddict.Hungergames.Types;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Interfaces.ChestManager;
import me.libraryaddict.Hungergames.Interfaces.FeastManager;
import me.libraryaddict.Hungergames.Managers.*;

public class HungergamesApi {
    private static AbilityConfigManager abilityConfigManager;
    private static AbilityManager abilityManager;
    private static ChatManager chatManager;
    private static ChestManager cm;
    private static ConfigManager config;
    private static FeastManager fm;
    private static Hungergames hg;
    private static KitSelectorManager icon;
    private static KitManager kits;
    private static MySqlManager mysql;
    private static PlayerManager pm;
    private static CommandManager commands;

    /**
     * @return AbilityConfigManager which is used to load the configs of
     *         abilitys
     */
    public static AbilityConfigManager getAbilityConfigManager() {
        if (abilityConfigManager == null)
            abilityConfigManager = new AbilityConfigManager();
        return abilityConfigManager;
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
     * @return AbilityManager which is used to load abilitys and handle them
     */
    public static AbilityManager getAbilityManager() {
        if (abilityManager == null)
            abilityManager = new AbilityManager();
        return abilityManager;
    }

    /**
     * @return ChatManager which is used for translations
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
        if (cm == null)
            cm = new LibsChestManager();
        return cm;
    }

    /**
     * @return Config manager which manages the settings inside config.yml
     */
    public static ConfigManager getConfigManager() {
        if (config == null)
            config = new ConfigManager();
        return config;
    }

    /**
     * @return FeastManager which generates the feast and the chests as well as
     *         getting the best 'Y' of the feast
     */
    public static FeastManager getFeastManager() {
        if (fm == null)
            fm = new LibsFeastManager();
        return fm;
    }

    /**
     * @return The main plugin itself, Hungergames
     */
    public static Hungergames getHungergames() {
        return hg;
    }

    /**
     * @return KitManager, Used for parsing items, kits and handling the kits
     *         themselves
     */
    public static KitManager getKitManager() {
        if (kits == null)
            kits = new KitManager();
        return kits;
    }

    /**
     * @return Kit Selector which handles the inventory which allows them to
     *         pick a kit
     */
    public static KitSelectorManager getKitSelector() {
        if (icon == null)
            icon = new KitSelectorManager();
        return icon;
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
     * @return Player Manager which is used to get the gamer, alive players and
     *         handle kills
     */
    public static PlayerManager getPlayerManager() {
        if (pm == null)
            pm = new PlayerManager();
        return pm;
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
     * @param Sets
     *            your own feast manager which implements 'FeastManager'
     */
    public static void setFeastManager(FeastManager manager) {
        fm = manager;
    }
}
