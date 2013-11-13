package me.libraryaddict.Hungergames.Types;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

public class RandomItem implements ConfigurationSerializable {
    static {
        ConfigurationSerialization.registerClass(RandomItem.class, RandomItem.class.getSimpleName());
    }

    public static RandomItem deserialize(Map<String, Object> args) {
        Material type = null;
        Object obj = args.get("Item Type");
        if (obj instanceof Integer)
            type = Material.getMaterial((Integer) obj);
        else if (obj instanceof String)
            type = Material.getMaterial((String) obj);
        else
            throw new RuntimeException(obj + " is not a valid item type");
        short itemData = (short) (int) (Integer) args.get("Item Data");
        double chancesOfItemStackAppearing = (Double) args.get("Chances in 100 of itemstack appearing");
        int minItems = (Integer) args.get("Min Items");
        int maxItems = (Integer) args.get("Max Items");
        return new RandomItem(chancesOfItemStackAppearing, type, itemData, minItems, maxItems);
    }

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

    public RandomItem(Map<String, Object> args) {
        this((Double) args.get("chancesOfItemStackAppearing"), (Material) args.get("itemType"), (Short) args.get("itemData"),
                (Integer) args.get("minItems"), (Integer) args.get("maxItems"));

    }

    /**
     * @return Randomized itemstack
     */
    public ItemStack getItemStack() {
        int amount = Math.max((maxItems - minItems) + 1, 1);
        amount = new Random().nextInt(amount) + 1;
        return new ItemStack(itemType, amount, itemData);
    }

    /**
     * @return Is the chance of being selected true?
     */
    public boolean hasChance() {
        return (new Random().nextInt(10000) < chanceOfItemStackAppearing * 100);
    }

    @Override
    public Map<String, Object> serialize() {
        Map result = new LinkedHashMap();
        result.put("Item Type", itemType.name());
        result.put("Item Data", itemData);
        result.put("Chances in 100 of itemstack appearing", chanceOfItemStackAppearing);
        result.put("Min Items", minItems);
        result.put("Max Items", maxItems);
        return result;
    }

    public String toString() {
        return (chanceOfItemStackAppearing + " " + minItems + " " + maxItems + " " + itemType + " " + itemData).trim();
    }
}