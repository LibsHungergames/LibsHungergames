package me.libraryaddict.Hungergames.Managers;

import java.util.HashMap;
import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
    private static HashMap<String, Scoreboard> boards = new HashMap<String, Scoreboard>();

    public static Scoreboard getScoreboard(String scoreboardName) {
        if (!boards.containsKey(scoreboardName))
            resetScoreboard(scoreboardName);
        return boards.get(scoreboardName);
    }

    public static void resetScoreboard(String scoreboardName) {
        if (!boards.containsKey(scoreboardName))
            boards.put(scoreboardName, Bukkit.getScoreboardManager().getNewScoreboard());
        for (Objective obj : boards.get(scoreboardName).getObjectives()) {
            obj.unregister();
        }
    }

    public static Objective getObjective(Scoreboard board, DisplaySlot slot) {
        if (board.getObjective(slot.name()) == null)
            board.registerNewObjective(slot.name(), slot.name());
        board.getObjective(slot.name()).setDisplaySlot(slot);
        return board.getObjective(slot.name());
    }

    public static void setDisplayName(String scoreboardName, DisplaySlot slot, String string) {
        if (string.length() > 16)
            string = string.substring(0, 16);
        if (HungergamesApi.getConfigManager().displayScoreboards())
            getObjective(getScoreboard(scoreboardName), slot).setDisplayName(string);
    }

    public static void makeScore(String scoreboardName, DisplaySlot slot, String name, int score) {
        if (name.length() > 16)
            name = name.substring(0, 16);
        if (HungergamesApi.getConfigManager().displayScoreboards())
            getObjective(getScoreboard(scoreboardName), slot).getScore(Bukkit.getOfflinePlayer(name)).setScore(score);
    }

    public static void hideScore(String scoreboardName, DisplaySlot slot, String name) {
        if (name.length() > 16)
            name = name.substring(0, 16);
        if (HungergamesApi.getConfigManager().displayScoreboards())
            getScoreboard(scoreboardName).resetScores(Bukkit.getOfflinePlayer(name));
    }

    public static void updateStage() {
        Hungergames hg = HungergamesApi.getHungergames();
        ConfigManager config = HungergamesApi.getConfigManager();
        ChatManager cm = HungergamesApi.getChatManager();
        if (hg.currentTime < 0)
            setDisplayName("Main", DisplaySlot.SIDEBAR, cm.getScoreboardStagePreGame());
        else if (hg.currentTime < config.getInvincibilityTime())
            setDisplayName("Main", DisplaySlot.SIDEBAR, cm.getScoreboardInvincibleRemaining());
        else if (hg.currentTime < config.getTimeFeastStarts() - (5 * 60))
            setDisplayName("Main", DisplaySlot.SIDEBAR, cm.getScoreboardStageFighting());
        else if (hg.currentTime >= config.getTimeFeastStarts() - (5 * 60) && hg.currentTime < config.getTimeFeastStarts())
            setDisplayName("Main", DisplaySlot.SIDEBAR, cm.getScoreboardStagePreFeast());
        else
            setDisplayName("Main", DisplaySlot.SIDEBAR, cm.getScoreboardStageFeastHappened());
    }

}