package me.libraryaddict.Hungergames.Managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import me.libraryaddict.Hungergames.Interfaces.ChestManager;
import me.libraryaddict.Hungergames.Types.RandomItem;

import org.bukkit.Material;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LibsChestManager implements ChestManager {

    public ArrayList<RandomItem> randomItems;

    public LibsChestManager() {
        randomItems = new ArrayList<RandomItem>();
        randomItems.add(new RandomItem(5, Material.DIAMOND, 0, 1, 4));
        randomItems.add(new RandomItem(5, Material.DIAMOND_BOOTS, 0, 1, 1));
        randomItems.add(new RandomItem(5, Material.DIAMOND_CHESTPLATE, 0, 1, 1));
        randomItems.add(new RandomItem(5, Material.DIAMOND_HELMET, 0, 1, 1));
        randomItems.add(new RandomItem(5, Material.DIAMOND_LEGGINGS, 0, 1, 1));
        randomItems.add(new RandomItem(5, Material.DIAMOND_SWORD, 0, 1, 1));
        randomItems.add(new RandomItem(5, Material.DIAMOND_PICKAXE, 0, 1, 1));
        randomItems.add(new RandomItem(20, Material.MUSHROOM_SOUP, 0, 1, 7));
        randomItems.add(new RandomItem(20, Material.LAVA_BUCKET, 0, 1, 1));
        randomItems.add(new RandomItem(20, Material.WATER_BUCKET, 0, 1, 1));
        randomItems.add(new RandomItem(20, Material.BUCKET, 0, 1, 1));
        randomItems.add(new RandomItem(20, Material.WEB, 0, 1, 2));
        randomItems.add(new RandomItem(20, Material.ENDER_PEARL, 0, 1, 6));
        randomItems.add(new RandomItem(20, Material.FLINT_AND_STEEL, 0, 1, 1));
        randomItems.add(new RandomItem(20, Material.GRILLED_PORK, 0, 1, 20));
        randomItems.add(new RandomItem(20, Material.COOKED_BEEF, 0, 1, 20));
        randomItems.add(new RandomItem(20, Material.COOKED_CHICKEN, 0, 1, 20));
        randomItems.add(new RandomItem(20, Material.BREAD, 0, 1, 20));
        randomItems.add(new RandomItem(20, Material.BOW, 0, 1, 1));
        randomItems.add(new RandomItem(20, Material.ARROW, 0, 1, 20));
        randomItems.add(new RandomItem(20, Material.TNT, 0, 1, 20));
        randomItems.add(new RandomItem(20, Material.EXP_BOTTLE, 0, 1, 10));
        randomItems.add(new RandomItem(10, Material.POTION, 16420, 1, 1)); // Poisons
        randomItems.add(new RandomItem(10, Material.POTION, 16425, 1, 1)); // Strength
        randomItems.add(new RandomItem(10, Material.POTION, 16418, 1, 1)); // Speed
                                                                           // II
        randomItems.add(new RandomItem(10, Material.POTION, 16426, 1, 1)); // Slowness
        randomItems.add(new RandomItem(10, Material.POTION, 16428, 1, 1)); // Damage
        randomItems.add(new RandomItem(10, Material.POTION, 16421, 1, 1)); // Healing
        randomItems.add(new RandomItem(10, Material.POTION, 16417, 1, 1)); // Regen
                                                                           // II
        randomItems.add(new RandomItem(10, Material.POTION, 16385, 1, 1)); // Regenm
                                                                           // I
        randomItems.add(new RandomItem(10, Material.POTION, 16451, 1, 1)); // Fire
        // resist
        randomItems.add(new RandomItem(1, Material.POTION, 16462, 1, 1)); // Invis
    }

    public void addRandomItem(RandomItem item) {
        randomItems.add(item);
    }

    public void clearRandomItems() {
        randomItems.clear();
    }

    private int countItems(Inventory inv) {
        int i = 0;
        for (ItemStack item : inv.getContents())
            if (item != null && item.getType() != Material.AIR)
                i++;
        return i;
    }

    public void fillChest(Inventory inv) {
        if (inv.getHolder() instanceof DoubleChest)
            fillChest(inv, true);
        else
            fillChest(inv, false);
    }

    private void fillChest(Inventory inv, boolean doubleChest) {
        inv.clear();
        if (randomItems.size() > 0)
            while (countItems(inv) < (doubleChest ? 10 : 4)) {
                Collections.shuffle(randomItems, new Random());
                for (RandomItem item : randomItems) {
                    if (item.hasChance()) {
                        inv.setItem(new Random().nextInt(inv.getSize()), item.getItemStack());
                    }
                }
            }
    }

    public ArrayList<RandomItem> getRandomItems() {
        return randomItems;
    }

    public void setRandomItems(ArrayList<RandomItem> items) {
        this.randomItems = items;
    }

}
