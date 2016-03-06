package me.libraryaddict.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager {
    private static FakeScoreboard mainScoreboard = new FakeScoreboard();
    private static boolean useOfflinePlayer = true;
    private static boolean useOfflineTeams = true;

    static {
        try {
            Team.class.getDeclaredMethod("addEntry", String.class);
            useOfflineTeams = false;
        } catch (Exception ex) {

        }
        try {
            Objective.class.getDeclaredMethod("getScore", String.class);
            useOfflinePlayer = false;
        } catch (Exception ex) {

        }
    }

    public static void addToTeam(String player, String teamName, String teamPrefix, boolean seeFriendlyInvis) {
        addToTeam(player, teamName, teamPrefix, null, seeFriendlyInvis);
    }

    public static void addToTeam(String player, String teamName, String teamPrefix, String teamSuffix, boolean seeFriendlyInvis) {
        for (FakeTeam team : mainScoreboard.getFakeTeams()) {
            team.removePlayer(player);
        }
        FakeTeam team = mainScoreboard.getFakeTeam(teamName);
        if (team == null) {
            team = mainScoreboard.createFakeTeam(teamName);
            if (teamPrefix != null)
                team.setPrefix(teamPrefix);
            if (teamSuffix != null)
                team.setSuffix(teamSuffix);
            team.setSeeInvisiblePlayers(seeFriendlyInvis);
        } else {
            if (teamPrefix != null)
                team.setPrefix(teamPrefix);
            if (teamSuffix != null)
                team.setSuffix(teamSuffix);
            team.setSeeInvisiblePlayers(seeFriendlyInvis);
        }
        team.addPlayer(player);
        for (Player p : Bukkit.getOnlinePlayers()) {
            addToTeam(p, player, teamName, teamPrefix, teamSuffix, seeFriendlyInvis);
        }
    }

    public static void clearScoreboard(Player player) {
        Scoreboard board = player.getScoreboard();
        for (Objective obj : board.getObjectives()) {
            obj.unregister();
        }
        if (useOfflineTeams) {
            for (OfflinePlayer p : board.getPlayers()) {
                board.resetScores(p);
            }
        } else {
            for (String p : board.getEntries()) {
                board.resetScores(p);
            }
        }
        for (Team team : board.getTeams()) {
            team.unregister();
        }
    }

    public static void clearScoreboard(Player player, DisplaySlot slot) {
        Scoreboard board = player.getScoreboard();
        for (Objective obj : board.getObjectives()) {
            if (obj.getDisplaySlot() == slot) {
                obj.unregister();
            }
        }
    }

    public static void clearScoreboards() {
        mainScoreboard = new FakeScoreboard();
        for (Player player : Bukkit.getOnlinePlayers()) {
            clearScoreboard(player);
        }
    }

    public static void addToTeam(Player observer, String player, String teamName, String teamPrefix, String teamSuffix,
                                 boolean seeFriendlyInvis) {
        Scoreboard board = observer.getScoreboard();
        boolean addToTeam = false;
        Team team = board.getTeam(teamName);
        if (team == null || !team.hasEntry(player)) {
            removeFromTeam(observer, player);
            addToTeam = true;
        }
        if (team == null) {
            team = board.registerNewTeam(teamName);
            if (teamPrefix != null)
                team.setPrefix(teamPrefix);
            if (teamSuffix != null)
                team.setSuffix(teamSuffix);
            team.setCanSeeFriendlyInvisibles(seeFriendlyInvis);
            team.setAllowFriendlyFire(!seeFriendlyInvis);
        } else {
            if (teamPrefix == null) {
                teamPrefix = "";
            }
            if (teamSuffix == null) {
                teamSuffix = "";
            }
            if (!teamPrefix.equals(team.getPrefix())) {
                team.setPrefix(teamPrefix);
            }
            if (!teamSuffix.equals(team.getSuffix())) {
                team.setSuffix(teamSuffix);
            }
            if (seeFriendlyInvis != team.canSeeFriendlyInvisibles()) {
                team.setCanSeeFriendlyInvisibles(seeFriendlyInvis);
                team.setAllowFriendlyFire(!seeFriendlyInvis);
            }
        }
        if (addToTeam) {
            if (useOfflineTeams) {
                team.addPlayer(Bukkit.getOfflinePlayer(player));
            } else {
                team.addEntry(player);
            }
        }
    }

    public static void addToTeam(Player observer, String player, String teamName, String teamPrefix, boolean seeFriendlyInvis) {
        addToTeam(observer, player, teamName, teamPrefix, null, seeFriendlyInvis);
    }

    public static void removeFromTeam(String player) {
        OfflinePlayer pl = (useOfflineTeams ? Bukkit.getOfflinePlayer(player) : null);
        for (Player p : Bukkit.getOnlinePlayers()) {
            Scoreboard board = p.getScoreboard();
            for (Team team : board.getTeams()) {
                if (useOfflineTeams) {
                    if (team.hasPlayer(pl)) {
                        team.removePlayer(pl);
                    }
                } else {
                    if (team.hasEntry(player)) {
                        team.removeEntry(player);
                    }
                }
            }
        }
        for (FakeTeam team : mainScoreboard.getFakeTeams()) {
            team.removePlayer(player);
        }
    }

    public static void removeFromTeam(Player observer, String player) {
        Scoreboard board = observer.getScoreboard();
        OfflinePlayer pl = (useOfflineTeams ? Bukkit.getOfflinePlayer(player) : null);
        for (Team team : board.getTeams()) {
            if (useOfflineTeams) {
                if (team.hasPlayer(pl)) {
                    team.removePlayer(pl);
                }
            } else {
                if (team.hasEntry(player)) {
                    team.removeEntry(player);
                }
            }
        }
    }

    private static Objective getObjective(Scoreboard board, DisplaySlot slot) {
        if (board.getObjective(slot.name()) == null) {
            Objective obj = board.registerNewObjective(slot.name(), slot.name());
            obj.setDisplaySlot(slot);
        }
        return board.getObjective(slot.name());
    }

    public static void hideScore(DisplaySlot slot, String name) {
        mainScoreboard.hideScore(slot, name);
        for (Player player : Bukkit.getOnlinePlayers()) {
            hideScore(player, slot, name);
        }
    }

    public static void hideScore(Player player, DisplaySlot slot, String name) {
        if (name.length() > 16)
            name = name.substring(0, 16);
        if (useOfflinePlayer) {
            player.getScoreboard().resetScores(Bukkit.getOfflinePlayer(name));
        } else {
            player.getScoreboard().resetScores(name);
        }
    }

    public static void makeScore(DisplaySlot slot, String name, int score) {
        mainScoreboard.makeScore(slot, name, score);
        for (Player player : Bukkit.getOnlinePlayers()) {
            makeScore(player, slot, name, score);
        }
    }

    public static void makeScore(Player player, DisplaySlot slot, String name, int score) {
        if (name.length() > 16) {
            name = name.substring(0, 16);
        }
        Objective obj = getObjective(player.getScoreboard(), slot);
        Score c = useOfflinePlayer ? obj.getScore(Bukkit.getOfflinePlayer(name)) : obj.getScore(name);
        if (!useOfflineTeams && score == 0 && slot == DisplaySlot.SIDEBAR && !c.isScoreSet()) {
            c.setScore(1);
        }
        if (c.getScore() != score) {
            c.setScore(score);
        }
    }

    public static void registerScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        mainScoreboard.setupObjectives(player);
    }

    public static void removeFromTeam(String player, String teamName) {
        FakeTeam team = mainScoreboard.getFakeTeam(teamName);
        if (team != null) {
            team.removePlayer(player);
        }
        OfflinePlayer pl = useOfflineTeams ? Bukkit.getOfflinePlayer(player) : null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            Scoreboard board = p.getScoreboard();
            if (board.getTeam(teamName) != null) {
                if (useOfflineTeams) {
                    if (board.getTeam(teamName).hasPlayer(pl)) {
                        board.getTeam(teamName).removePlayer(pl);
                    }
                } else if (board.getTeam(teamName).hasEntry(player)) {
                    board.getTeam(teamName).removeEntry(player);
                }
            }
        }
    }

    public static void setDisplayName(DisplaySlot slot, String string) {
        mainScoreboard.setDisplayName(slot, string);
        for (Player player : Bukkit.getOnlinePlayers())
            setDisplayName(player, slot, string);
    }

    public static void setDisplayName(Player player, DisplaySlot slot, String string) {
        getObjective(player.getScoreboard(), slot).setDisplayName(string);
    }

    public static void addToTeam(Player player, String teamName, String teamPrefix, boolean seeFriendlyInvis) {
        addToTeam(player.getName(), teamName, teamPrefix, seeFriendlyInvis);
    }

    public static void addToTeam(Player p, Player p2, String teamName, String teamPrefix, boolean seeFriendlyInvis) {
        addToTeam(p, p2.getName(), teamName, teamPrefix, seeFriendlyInvis);
    }

    public static void removeFromTeam(Player player) {
        removeFromTeam(player.getName());
    }

}