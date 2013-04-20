package me.libraryaddict.Hungergames.Managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
    private static Scoreboard mainScoreboard;
    private static String dummyObjectiveName = ChatColor.RESET.toString();

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
        getSidebar().setDisplayName(string);
    }

    public static void makeScore(String name, int score) {
        getSidebar().getScore(Bukkit.getOfflinePlayer(name)).setScore(score);
    }

    public static void hideScore(String name) {
        mainScoreboard.resetScores(Bukkit.getOfflinePlayer(name));
    }
}