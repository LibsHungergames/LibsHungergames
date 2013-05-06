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

    public static AbilityConfigManager getAbilityConfigManager() {
        if (abilityConfigManager == null)
            abilityConfigManager = new AbilityConfigManager();
        return abilityConfigManager;
    }

    public static AbilityManager getAbilityManager() {
        if (abilityManager == null)
            abilityManager = new AbilityManager();
        return abilityManager;
    }

    public static ChatManager getChatManager() {
        if (chatManager == null)
            chatManager = new ChatManager();
        return chatManager;
    }

    public static ChestManager getChestManager() {
        if (cm == null)
            cm = new LibsChestManager();
        return cm;
    }

    public static ConfigManager getConfigManager() {
        if (config == null)
            config = new ConfigManager();
        return config;
    }

    public static FeastManager getFeastManager() {
        if (fm == null)
            fm = new LibsFeastManager();
        return fm;
    }

    public static Hungergames getHungergames() {
        return hg;
    }

    public static KitManager getKitManager() {
        if (kits == null)
            kits = new KitManager();
        return kits;
    }

    public static KitSelectorManager getKitSelector() {
        if (icon == null)
            icon = new KitSelectorManager();
        return icon;
    }

    public static MySqlManager getMySqlManager() {
        if (mysql == null)
            mysql = new MySqlManager();
        return mysql;
    }

    public static PlayerManager getPlayerManager() {
        if (pm == null)
            pm = new PlayerManager();
        return pm;
    }

    public static void init(Hungergames hunger) {
        hg = hunger;
    }

    public static void setChestManager(ChestManager manager) {
        cm = manager;
    }

    public static void setFeastManager(FeastManager manager) {
        fm = manager;
    }
}
