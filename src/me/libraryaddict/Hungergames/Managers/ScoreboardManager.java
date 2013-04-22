package me.libraryaddict.Hungergames.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
    private static Scoreboard mainScoreboard;
    private static String dummyObjectiveName = ChatColor.RESET.toString();
    private static Set<String> previousKillers = new HashSet<String>();

    public static Scoreboard getMainScoreboard() {
        if (mainScoreboard == null)
            resetScoreboard();
        return mainScoreboard;
    }

    // Make sure to update each player's scoreboard with this scoreboard if you
    // reset.
    private static void resetScoreboard() {
        mainScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective objective = mainScoreboard.registerNewObjective(dummyObjectiveName, "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private static Objective getSidebar() {
        return getMainScoreboard().getObjective(dummyObjectiveName);
    }

    public static void setDisplayName(String string) {
        if (HungergamesApi.getConfigManager().displayScoreboards())
            getSidebar().setDisplayName(string);
    }

    public static void makeScore(String name, int score) {
        if (HungergamesApi.getConfigManager().displayScoreboards())
            getSidebar().getScore(Bukkit.getOfflinePlayer(name)).setScore(score);
    }

    public static void hideScore(String name) {
        if (HungergamesApi.getConfigManager().displayScoreboards())
            mainScoreboard.resetScores(Bukkit.getOfflinePlayer(name));
    }

    private static String getLowest(HashMap<String, Integer> hashmap) {
        int lowest = 1000;
        String lowestName = null;
        for (String name : hashmap.keySet())
            if (hashmap.get(name) < lowest) {
                lowestName = name;
                lowest = hashmap.get(name);
            }
        return lowestName;
    }

    public static void updateStage() {
        Hungergames hg = HungergamesApi.getHungergames();
        ConfigManager config = HungergamesApi.getConfigManager();
        if (hg.currentTime < 0)
            setDisplayName(ChatColor.DARK_AQUA + "Stage: " + ChatColor.AQUA + "Pregame");
        else if (hg.currentTime < config.getInvincibilityTime())
            setDisplayName(ChatColor.DARK_AQUA + "Stage: " + ChatColor.AQUA + "Invincibility");
        else if (hg.currentTime < config.getTimeFeastStarts() - (5 * 60))
            setDisplayName(ChatColor.DARK_AQUA + "Stage: " + ChatColor.AQUA + "Fighting");
        else if (hg.currentTime >= config.getTimeFeastStarts() - (5 * 60) && hg.currentTime < config.getTimeFeastStarts())
            setDisplayName(ChatColor.DARK_AQUA + "Stage: " + ChatColor.AQUA + "Pre-Feast");
        else
            setDisplayName(ChatColor.DARK_AQUA + "Stage: " + ChatColor.AQUA + "Finishing up");
    }

    public static void updateKills() {
        HashMap<String, Integer> topKillers = new HashMap<String, Integer>();
        for (Gamer gamer : HungergamesApi.getPlayerManager().getAliveGamers()) {
            if (gamer.getKills() == 0)
                continue;
            if (topKillers.keySet().size() < 3) {
                topKillers.put(gamer.getName(), gamer.getKills());
                continue;
            }
            String replace = getLowest(topKillers);
            if (replace == null)
                continue;
            if (topKillers.get(replace) < gamer.getKills()) {
                topKillers.remove(replace);
                topKillers.put(gamer.getName(), gamer.getKills());
            }
        }
        for (String name : previousKillers)
            hideScore(name);
        for (String name : topKillers.keySet())
            makeScore(ChatColor.DARK_RED + name, topKillers.get(name));
        previousKillers = topKillers.keySet();
    }
}