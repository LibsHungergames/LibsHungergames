package me.libraryaddict.Hungergames.Interfaces;

import org.bukkit.Location;

public interface FeastManager {

    public int getSpawnHeight(Location loc, int radius);

    public void generateSpawn(final Location loc, int lowestLevel, int radius);

    public void generateChests(Location loc, int height);
}
