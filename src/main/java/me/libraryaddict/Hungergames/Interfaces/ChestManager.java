package me.libraryaddict.Hungergames.Interfaces;

import java.util.ArrayList;

import me.libraryaddict.Hungergames.Types.RandomItem;

import org.bukkit.inventory.Inventory;

public interface ChestManager {
    /**
     * Fills the chest with items
     * 
     * @param inv
     */
    public void fillChest(Inventory inv);

    /**
     * Sets the random items in the feast generator
     */
    public void setRandomItems(ArrayList<RandomItem> items);

    /**
     * Clears the random items to stick in
     */
    public void clearRandomItems();

    /**
     * Adds a random item to put in the feast
     */
    public void addRandomItem(RandomItem item);

    /**
     * Gets the random items
     * 
     * @return
     */
    public ArrayList<RandomItem> getRandomItems();
}
