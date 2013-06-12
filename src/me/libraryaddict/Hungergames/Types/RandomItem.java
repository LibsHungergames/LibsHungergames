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
    String addictionalData = "";

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
        chance = newChance;
        id = newId;
        data = (short) newData;
        min = newMin;
        max = newMax;
    }

    public RandomItem(String string) {
        try {
            String[] split = string.split(" ");
            chance = Double.parseDouble(split[0]);
            try {
                id = Material.getMaterial(split[3].toUpperCase()).getId();
            } catch (Exception ex) {
                id = Material.getMaterial(Integer.parseInt(split[3])).getId();
            }
            min = Integer.parseInt(split[1]);
            max = Integer.parseInt(split[2]);
            data = Short.parseShort(split[4]);
            addictionalData = string.substring(
                    split[0].length() + split[1].length() + split[2].length() + split[3].length() + split[4].length() + 4).trim();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String toString() {
        return (chance + " " + min + " " + max + " " + Material.getMaterial(id) + " " + data + " " + addictionalData).trim();
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
        chance = newChance;
        id = mat.getId();
        data = (short) newData;
        min = newMin;
        max = newMax;
    }

    /**
     * @return Randomized itemstack
     */
    public ItemStack getItemStack() {
        return HungergamesApi.getKitManager().parseItem(
                id + " " + data + " " + (new Random().nextInt((max - min) + 1) + min) + " " + addictionalData)[0];
    }

    /**
     * @return Is the chance of being selected true?
     */
    public boolean hasChance() {
        return (new Random().nextInt(10000) < chance * 100);
    }
}