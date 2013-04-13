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

    public RandomItem(double newChance, int newId, int newData, int newMin, int newMax) {
        chance = newChance;
        id = newId;
        data = (short) newData;
        min = newMin;
        max = newMax;
    }

    public RandomItem(double newChance, Material mat, int newData, int newMin, int newMax) {
        chance = newChance;
        id = mat.getId();
        data = (short) newData;
        min = newMin;
        max = newMax;
    }

    public boolean hasChance() {
        return (new Random().nextInt(10000) < chance * 100);
    }

    public ItemStack getItemStack() {
        return new ItemStack(id, (new Random().nextInt((max - min) + 1) + min), data);
    }
}