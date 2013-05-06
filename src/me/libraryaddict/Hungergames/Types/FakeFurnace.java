package me.libraryaddict.Hungergames.Types;

import java.util.*;
import net.minecraft.server.v1_5_R3.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

public class FakeFurnace extends TileEntityFurnace {
    // For custom stuff
    private double burnSpeed;
    private ItemStack[] contents = new ItemStack[3];
    // Increases performance (or should at least)
    @SuppressWarnings("unused")
    private long lastCheck;
    // Call me paranoid, but this has to be checked
    private int lastID;
    // To access the chests
    public int link;
    private double meltSpeed;
    // I'm internally using "myCookTime" to not lose any precision, but for
    // displaying the progress I still have to use "cookTime"
    private double myCookTime;

    // New VTE
    public FakeFurnace() {
        link = 0;
        burnSpeed = 1.0D;
        meltSpeed = 1.0D;
        myCookTime = 0.0D;
        cookTime = 0;
        burnTime = 0;
        ticksForCurrentFuel = 0;
        lastID = 0;
        lastCheck = 0;
    }

    // Read from save

    public boolean a(EntityHuman entityhuman) // Derpnote
    {
        return true;
    }

    public void burn() {
        // Can't burn? Goodbye
        if (!canBurn()) {
            return;
        }
        ItemStack itemstack = getBurnResult(contents[0]);
        // Nothing in there? Then put something there.
        if (contents[2] == null) {
            contents[2] = itemstack.cloneItemStack();
        }
        // Burn ahead
        else if (contents[2].doMaterialsMatch(itemstack)) {
            contents[2].count += itemstack.count;
        }
        // And consume the ingredient item
        // Goddamn, you have container functions, use them! Notch!
        if (Item.byId[contents[0].id].t()) // Derpnote
        {
            contents[0] = new ItemStack(Item.byId[contents[0].id].s()); // Derpnote
        } else {
            contents[0].count--;
            // Let 0 be null
            if (contents[0].count <= 0) {
                contents[0] = null;
            }
        }
    }

    private boolean canBurn() {
        // No ingredient, no recipe
        if (contents[0] == null) {
            return false;
        }
        ItemStack itemstack = getBurnResult(contents[0]);
        // No recipe, no burning
        if (itemstack == null) {
            return false;
        }
        // Free space? Let's burn!
        else if (contents[2] == null) {
            return true;
        }
        // Materials don't match? Too bad.
        else if (!contents[2].doMaterialsMatch(itemstack)) {
            return false;
        }
        // As long as there is space, we can burn
        else if ((contents[2].count + itemstack.count <= getMaxStackSize())
                && (contents[2].count + itemstack.count <= contents[2].getMaxStackSize())) {
            return true;
        }
        return false;
    }

    // For compatibility
    public void g() // Derpnote
    {
        tick();
    }

    private ItemStack getBurnResult(ItemStack item) {
        if (item == null) {
            return null;
        }
        int i = item.id;
        // CUSTOM RECIPE HERE
        return RecipesFurnace.getInstance().getResult(i); // Derpnote
    }

    private double getBurnSpeed(ItemStack item) {
        if (item == null) {
            return 0.0D;
        }
        // CUSTOM FUEL HERE
        return 1.0D;
    }

    /*****
     * The following methods are only here because they interact with the
     * contents array, which is private
     *****/

    public ItemStack[] getContents() {
        return contents;
    }

    private int getFuelTime(ItemStack item) {
        if (item == null) {
            return 0;
        }
        int i = item.id;
        // CUSTOM FUEL HERE
        // Lava should melt 128 items, not 100
        if (i == Item.LAVA_BUCKET.id) {
            return 25600;
        } else {
            return fuelTime(item);
        }
    }

    public ItemStack getItem(int i) {
        return contents[i];
    }

    private double getMeltSpeed(ItemStack item) {
        if (item == null) {
            return 0.0D;
        }
        // CUSTOM RECIPE HERE
        return 1.0D;
    }

    // Compatibility
    public InventoryHolder getOwner() {
        return null;
    }

    public int getSize() {
        return contents.length;
    }

    public List<HumanEntity> getViewers() {
        return new ArrayList<HumanEntity>();
    }

    // This needs a little addition
    public boolean isBurning() {
        return super.isBurning() && (burnSpeed > 0.0D);
    }

    public boolean isFine() {
        return ((myCookTime > 0.0D) || (getFuelTime(contents[1]) > 0)) && canBurn();
    }

    public void onClose(CraftHumanEntity who) {
    }

    public void onOpen(CraftHumanEntity who) {
    }

    public void setItem(int i, ItemStack itemstack) {
        contents[i] = itemstack;
        if (itemstack != null && itemstack.count > getMaxStackSize()) {
            itemstack.count = getMaxStackSize();
        }
    }

    public ItemStack splitStack(int i, int j) {
        if (contents[i] != null) {
            ItemStack itemstack;
            if (contents[i].count <= j) {
                itemstack = contents[i];
                contents[i] = null;
                return itemstack;
            } else {
                itemstack = contents[i].a(j); // Derpnote
                if (contents[i].count == 0) {
                    contents[i] = null;
                }
                return itemstack;
            }
        } else {
            return null;
        }
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (contents[i] != null) {
            ItemStack itemstack = contents[i];
            contents[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public void tick() {
        int newID = contents[0] == null ? 0 : contents[0].id;
        // Has the item been changed?
        if (newID != lastID) {
            // Then reset the progress!
            myCookTime = 0.0D;
            lastID = newID;
            // And, most important: change the melt speed
            meltSpeed = getMeltSpeed(contents[0]);
        }
        // So, can we now finally burn?
        if (canBurn() && !isBurning() && (getFuelTime(contents[1]) > 0)) {
            // I have no idea what "ticksForCurrentFuel" is good for, but it
            // works fine like this
            burnTime = ticksForCurrentFuel = getFuelTime(contents[1]);
            // Before we remove the item: how fast does it burn?
            burnSpeed = getBurnSpeed(contents[1]);
            // If it's a container item (lava bucket), we only consume its
            // contents (not like evil Notch!)

            // If it's not a container, consume it! Om nom nom nom!
            {
                contents[1].count--;
                // Let 0 be null
                if (contents[1].count <= 0) {
                    contents[1] = null;
                }
            }
        }
        // Now, burning?
        if (isBurning()) {
            // Then move on
            burnTime--;
            // I'm using a double here because of the custom recipes.
            // The faster this fuel burns and the faster the recipe melts, the
            // faster we're done
            myCookTime += burnSpeed * meltSpeed;
            // Finished burning?
            if (myCookTime >= 200.0D) {
                myCookTime -= 200.0D;
                burn();
            }
        }
        // If it's not burning, we reset the burning progress!
        else {
            myCookTime = 0.0D;
        }
        // And for the display (I'm using floor rather than round to not cause
        // the client to do shit when we not really reached 200):
        cookTime = (int) Math.floor(myCookTime);
    }
}