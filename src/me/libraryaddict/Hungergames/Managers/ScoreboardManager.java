package me.libraryaddict.Hungergames.Managers;

import java.util.HashMap;
import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
    private static HashMap<DisplaySlot, Scoreboard> boards = new HashMap<DisplaySlot, Scoreboard>();

    public static Scoreboard getScoreboard(DisplaySlot slot) {
        if (!boards.containsKey(slot))
            resetScoreboard(slot);
        return boards.get(slot);
    }

    public static Objective getObjective(DisplaySlot slot) {
        return getScoreboard(slot).getObjective(slot.name());
    }

    public static void resetScoreboard(DisplaySlot slot) {
        if (!boards.containsKey(slot))
            boards.put(slot, Bukkit.getScoreboardManager().getNewScoreboard());
        if (boards.get(slot).getObjective(slot.name()) != null)
            boards.get(slot).getObjective(slot.name()).unregister();
        Objective objective = boards.get(slot).registerNewObjective(slot.name(), "dummy");
        objective.setDisplaySlot(slot);
    }

    public static void setDisplayName(DisplaySlot slot, String string) {
        getObjective(slot).setDisplayName(string);
    }

    public static void makeScore(DisplaySlot slot, String name, int score) {
        getObjective(slot).getScore(Bukkit.getOfflinePlayer(name)).setScore(score);
    }

    public static void hideScore(DisplaySlot slot, String name) {
        getScoreboard(slot).resetScores(Bukkit.getOfflinePlayer(name));
    }

    public static void updateStage() {
        Hungergames hg = HungergamesApi.getHungergames();
        ConfigManager config = HungergamesApi.getConfigManager();
        if (hg.currentTime < 0)
            setDisplayName(DisplaySlot.SIDEBAR, ChatColor.DARK_AQUA + "Stage: " + ChatColor.AQUA + "Pregame");
        else if (hg.currentTime < config.getInvincibilityTime())
            setDisplayName(DisplaySlot.SIDEBAR, ChatColor.DARK_AQUA + "Stage: " + ChatColor.AQUA + "Invincibility");
        else if (hg.currentTime < config.getTimeFeastStarts() - (5 * 60))
            setDisplayName(DisplaySlot.SIDEBAR, ChatColor.DARK_AQUA + "Stage: " + ChatColor.AQUA + "Fighting");
        else if (hg.currentTime >= config.getTimeFeastStarts() - (5 * 60) && hg.currentTime < config.getTimeFeastStarts())
            setDisplayName(DisplaySlot.SIDEBAR, ChatColor.DARK_AQUA + "Stage: " + ChatColor.AQUA + "Pre-Feast");
        else
            setDisplayName(DisplaySlot.SIDEBAR, ChatColor.DARK_AQUA + "Stage: " + ChatColor.AQUA + "Finishing up");
    }

}