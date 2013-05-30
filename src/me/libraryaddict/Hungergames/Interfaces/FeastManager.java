package me.libraryaddict.Hungergames.Interfaces;

import org.bukkit.Location;

public interface FeastManager {

    /**
     * 
     * @param loc
     * @param radius
     * @return The height the feast should spawn at
     */
    public int getSpawnHeight(Location loc, int radius);

    /**
     * Generates the platform the feast spawns on
     * @param loc
     * @param lowestLevel
     * @param radius
     */
    public void generatePlatform(final Location loc, int lowestLevel, int radius);

    /**
     * Generates the chests, the quartz and the enchanting table. As well as the tnt inside
     * @param loc
     * @param height
     */
    public void generateChests(Location loc, int height);
    
}
