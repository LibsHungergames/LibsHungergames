package me.libraryaddict.Hungergames.Types;

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

    public Stats(UUID uuid, String player) {
        newStats = true;
        this.owningPlayer = player;
        this.uuid = uuid;
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
