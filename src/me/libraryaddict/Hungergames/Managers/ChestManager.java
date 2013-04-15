package me.libraryaddict.Hungergames.Managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import me.libraryaddict.Hungergames.Types.RandomItem;

import org.bukkit.Material;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestManager {

    public ArrayList<RandomItem> randomItems;

    public ChestManager() {
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
        randomItems.add(new RandomItem(16, Material.POTION, 16420, 1, 2)); // Poisons 
        randomItems.add(new RandomItem(16, Material.POTION, 16425, 1, 2)); // Strength
        randomItems.add(new RandomItem(16, Material.POTION, 16418, 1, 2)); // Speed
                                                                           // II
        randomItems.add(new RandomItem(16, Material.POTION, 16426, 1, 2)); // Slowness
        randomItems.add(new RandomItem(16, Material.POTION, 16428, 1, 2)); // Damage
        randomItems.add(new RandomItem(16, Material.POTION, 16421, 1, 4)); // Healing
        randomItems.add(new RandomItem(16, Material.POTION, 16417, 1, 2)); // Regen
                                                                           // II
        randomItems.add(new RandomItem(13, Material.POTION, 16385, 1, 2)); // Regenm
                                                                           // I
        randomItems.add(new RandomItem(13, Material.POTION, 16451, 1, 2)); // Fire
        // resist
        randomItems.add(new RandomItem(4, Material.POTION, 16462, 1, 2)); // Invis
    }

    private int countItems(Inventory inv) {
        int i = 0;
        for (ItemStack item : inv.getContents())
            if (item != null && item.getType() != Material.AIR)
                i++;
        return i;
    }

    private void fillChest(Inventory inv, boolean doubleChest) {
        inv.clear();
        while (countItems(inv) < (doubleChest ? 12 : 6)) {
            Collections.shuffle(randomItems, new Random());
            for (RandomItem item : randomItems) {
                if (item.hasChance()) {
                    inv.setItem(new Random().nextInt(inv.getSize()), item.getItemStack());
                }
            }
        }
    }

    public void fillChest(Inventory inv) {
        if (inv.getHolder() instanceof DoubleChest)
            fillChest(inv, true);
        else
            fillChest(inv, false);
    }

}
