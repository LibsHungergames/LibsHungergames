package me.libraryaddict.Hungergames.Managers;

import java.util.HashMap;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
    private static HashMap<String, Scoreboard> boards = new HashMap<String, Scoreboard>();
    private static HashMap<Integer, String> stages = new HashMap<Integer, String>();

    public static void doStage() {
        if (stages.containsKey(HungergamesApi.getHungergames().currentTime)) {
            setDisplayName("Main", DisplaySlot.SIDEBAR, stages.get(HungergamesApi.getHungergames().currentTime));
        }
    }

    public static Objective getObjective(Scoreboard board, DisplaySlot slot) {
        if (board.getObjective(slot.name()) == null) {
            board.registerNewObjective(slot.name(), slot.name());
            board.getObjective(slot.name()).setDisplaySlot(slot);
        }
        return board.getObjective(slot.name());
    }

    public static Scoreboard getScoreboard(String scoreboardName) {
        if (!boards.containsKey(scoreboardName))
            resetScoreboard(scoreboardName);
        return boards.get(scoreboardName);
    }

    public static void hideScore(String scoreboardName, DisplaySlot slot, String name) {
        if (name.length() > 16)
            name = name.substring(0, 16);
        if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()) {
            Scoreboard board = getScoreboard(scoreboardName);
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            if (board.getPlayers().contains(player))
                board.resetScores(player);
        }
    }

    public static void makeScore(String scoreboardName, DisplaySlot slot, String name, int score) {
        if (name.length() > 16)
            name = name.substring(0, 16);
        if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()) {
            Score scoreboard = getObjective(getScoreboard(scoreboardName), slot).getScore(Bukkit.getOfflinePlayer(name));
            if (scoreboard.getScore() != score)
                scoreboard.setScore(score);
        }
    }

    public static void registerStage(int timeToActivate, String display) {
        stages.put(timeToActivate, display);
    }

    public static void resetScoreboard(String scoreboardName) {
        if (!boards.containsKey(scoreboardName)) {
            boards.put(scoreboardName, Bukkit.getScoreboardManager().getNewScoreboard());
            boards.get(scoreboardName).registerNewTeam("Spectators").setCanSeeFriendlyInvisibles(true);
            boards.get(scoreboardName).getTeam("Spectators").setPrefix(ChatColor.DARK_GRAY + "");
        } else
            for (Objective obj : boards.get(scoreboardName).getObjectives()) {
                obj.unregister();
            }
    }

    public static void setDisplayName(String scoreboardName, DisplaySlot slot, String string) {
        if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()) {
            getObjective(getScoreboard(scoreboardName), slot).setDisplayName(string);
        }
    }

    /*  public static void updatteStage() {
          Hungergames hg = HungergamesApi.getHungergames();
          ConfigManager config = HungergamesApi.getConfigManager();
          TranslationManager cm = HungergamesApi.getTranslationManager();
          if (hg.currentTime < 0)
              setDisplayName("Main", DisplaySlot.SIDEBAR, cm.getScoreboardStagePreGame());
          else if (hg.currentTime < config.getInvincibilityTime())
              setDisplayName("Main", DisplaySlot.SIDEBAR, cm.getScoreboardStageInvincibility());
          else if (hg.currentTime < config.getTimeFeastStarts() - (5 * 60))
              setDisplayName("Main", DisplaySlot.SIDEBAR, cm.getScoreboardStageFighting());
          else if (hg.currentTime >= config.getTimeFeastStarts() - (5 * 60) && hg.currentTime < config.getTimeFeastStarts())
              setDisplayName("Main", DisplaySlot.SIDEBAR, cm.getScoreboardStagePreFeast());
          else if (hg.currentTime >= config.getTimeFeastStarts() && hg.currentTime <= config.getTimeFeastStarts() + (5 * 60))
              setDisplayName("Main", DisplaySlot.SIDEBAR, cm.getScoreboardStageFeastHappening());
          else
              setDisplayName("Main", DisplaySlot.SIDEBAR, cm.getScoreboardStageFeastHappened());
      }*/
}