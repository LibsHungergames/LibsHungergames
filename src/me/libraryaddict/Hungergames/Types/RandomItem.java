package me.libraryaddict.Hungergames.Types;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RandomItem {
    double chance;
    // Chance is out of a hundred..
    // Goes to 0.01
    short data;
    int min, max, id;

    /**
     * 
     * @param Chance
     *            of being used, out of a hundred. 0 = no chance
     * @param ID
     *            of the item
     * @param Datavalue
     *            of the item
     * @param Min
     *            amount of the item
     * @param Max
     *            amount of the item
     */
    public RandomItem(double newChance, int newId, int newData, int newMin, int newMax) {
        chance = newChance;
        id = newId;
        data = (short) newData;
        min = newMin;
        max = newMax;
    }

    /**
     * 
     * @param Chance
     *            of being used, out of a hundred. 0 = no chance
     * @param Material
     *            of the item
     * @param Datavalue
     *            of the item
     * @param Min
     *            amount of the item
     * @param Max
     *            amount of the item
     */
    public RandomItem(double newChance, Material mat, int newData, int newMin, int newMax) {
        chance = newChance;
        id = mat.getId();
        data = (short) newData;
        min = newMin;
        max = newMax;
    }

    /**
     * 
     * @return Randomized itemstack
     */
    public ItemStack getItemStack() {
        return new ItemStack(id, (new Random().nextInt((max - min) + 1) + min), data);
    }

    /**
     * 
     * @return Is the chance of being selected true?
     */
    public boolean hasChance() {
        return (new Random().nextInt(10000) < chance * 100);
    }
}