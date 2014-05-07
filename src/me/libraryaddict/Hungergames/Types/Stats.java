package me.libraryaddict.Hungergames.Types;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Stats {
    private boolean hasChanged;
    private int killsBest;
    private int killsCurrent;
    private int killsTotal;
    private int lossses;
    private boolean newStats;
    private String owningPlayer;
    private UUID uuid;
    private int wins;

    public Stats clone() {
        Stats stats = new Stats(getUuid(), getOwningPlayer());
        try {
            for (Field field : getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(stats, field.get(this));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stats;
    }

    public Stats(UUID uuid, String player) {
        newStats = true;
        this.owningPlayer = player;
        this.uuid = uuid;
    }

    public Stats(UUID uuid, String player, ResultSet rs) throws SQLException {
        this.owningPlayer = player;
        this.uuid = uuid;
        this.killsTotal = rs.getInt("Kills");
        this.killsBest = rs.getInt("Killstreak");
        this.wins = rs.getInt("Wins");
        this.lossses = rs.getInt("Losses");
    }

    public Stats(UUID uuid, String player, int kills, int killstreak, int wins, int losses) {
        this.uuid = uuid;
        this.owningPlayer = player;
        this.killsTotal = kills;
        this.killsBest = killstreak;
        this.wins = wins;
        this.lossses = losses;
    }

    public void addKill() {
        killsCurrent++;
        killsTotal++;
        killsBest = Math.max(getKillsBest(), getKillsCurrent());
        hasChanged = true;
    }

    public void addLoss() {
        lossses++;
        hasChanged = true;
    }

    public void addWin() {
        wins++;
        hasChanged = true;
    }

    public String getPlayer() {
        return owningPlayer;
    }

    public boolean hasChanged() {
        return owningPlayer != null && hasChanged;
    }
}
