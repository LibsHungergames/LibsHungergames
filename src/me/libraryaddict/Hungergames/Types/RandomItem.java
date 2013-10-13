package me.libraryaddict.Hungergames.Types;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RandomItem implements java.io.Serializable {
    private static final long serialVersionUID = 3915980470659471731L;
    private String addictionalData = "";
    private double chanceOfItemStackAppearing;
    // Chance is out of a hundred..
    // Goes to 0.01
    private short itemData;
    private Material itemType;
    private int minItems, maxItems;

    /**
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
        chanceOfItemStackAppearing = newChance;
        itemType = Material.getMaterial(newId);
        itemData = (short) newData;
        minItems = newMin;
        maxItems = newMax;
    }

    /**
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
        chanceOfItemStackAppearing = newChance;
        itemType = mat;
        itemData = (short) newData;
        minItems = newMin;
        maxItems = newMax;
    }

    /**
     * @return Randomized itemstack
     */
    public ItemStack getItemStack() {
        return HungergamesApi.getKitManager().parseItem(
                itemType + " " + itemData + " " + (new Random().nextInt((maxItems - minItems) + 1) + minItems) + " "
                        + addictionalData)[0];
    }

    /**
     * @return Is the chance of being selected true?
     */
    public boolean hasChance() {
        return (new Random().nextInt(10000) < chanceOfItemStackAppearing * 100);
    }

    public String toString() {
        return (chanceOfItemStackAppearing + " " + minItems + " " + maxItems + " " + itemType + " " + itemData + " " + addictionalData)
                .trim();
    }
}