package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PageInventory extends ClickInventory {

    protected ItemStack backAPage;
    protected int currentPage;
    protected boolean dynamicInventorySize = true;
    protected ItemStack forwardsAPage;
    private int invSize = 54;
    protected boolean pageDisplayedInTitle;
    protected HashMap<Integer, ItemStack[]> pages = new HashMap<Integer, ItemStack[]>();
    protected String title = "Inventory";
    private String titleFormat = "%Title% - Page %Page%";

    public PageInventory(Player player) {
        super(player);
    }

    public PageInventory(Player player, boolean dymanicInventory, int invSize) {
        super(player);
        dynamicInventorySize = dymanicInventory;
        this.invSize = invSize;
    }

    /**
     * Get the itemstack which is the backpage
     */
    public ItemStack getBackPage() {
        if (backAPage == null) {
            backAPage = HungergamesApi.getInventoryManager().generateItem(Material.SIGN, 0, ChatColor.RED + "Back",
                    new String[] { ChatColor.BLUE + "Click this to move back" });
        }
        return backAPage;
    }

    /**
     * Get the current page number
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Get the itemstack which is the next page
     */
    public ItemStack getForwardsPage() {
        if (forwardsAPage == null) {
            forwardsAPage = HungergamesApi.getInventoryManager().generateItem(Material.SIGN, 1, ChatColor.RED + "Forward",
                    new String[] { ChatColor.BLUE + "Click this to move forward" });
        }
        return forwardsAPage;
    }

    /**
     * Get the items in a page
     */
    public ItemStack[] getPage(int pageNumber) {
        if (pages.containsKey(pageNumber))
            return pages.get(pageNumber);
        return null;
    }

    /**
     * Get pages
     */
    public HashMap<Integer, ItemStack[]> getPages() {
        return pages;
    }

    protected String getPageTitle() {
        return (this.isPageDisplayedInTitle() ? titleFormat.replace("%Title%", getTitle()).replace("%Page%",
                (getCurrentPage() + 1) + "") : getTitle());
    }

    /**
     * Get page title
     */
    public String getTitle() {
        return title;
    }

    public boolean isPageDisplayedInTitle() {
        return this.pageDisplayedInTitle;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() == getPlayer()) {
            ItemStack item = event.getCurrentItem();
            if (event.getRawSlot() < currentInventory.getSize()) {
                if (item != null) {
                    if (item.equals(getBackPage())) {
                        setPage(getCurrentPage() - 1);
                        event.setCancelled(true);
                        return;
                    } else if (item.equals(getForwardsPage())) {
                        setPage(getCurrentPage() + 1);
                        event.setCancelled(true);
                        return;
                    }
                }
                if (!isModifiable()) {
                    event.setCancelled(true);
                }
            } else if (!this.isModifiable() && event.isShiftClick() && item != null && item.getType() != Material.AIR) {
                for (int slot = 0; slot < currentInventory.getSize(); slot++) {
                    ItemStack invItem = currentInventory.getItem(slot);
                    if (invItem == null || invItem.getType() == Material.AIR
                            || (invItem.isSimilar(item) && item.getAmount() < item.getMaxStackSize())) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Opens the inventory for use
     */
    public void openInventory() {
        if (isInventoryInUse())
            return;
        if (currentInventory == null) {
            ItemStack[] pageItems = pages.get(Math.max(getCurrentPage(), 0));
            currentInventory = Bukkit.createInventory(null, pageItems.length, getPageTitle());
            currentInventory.setContents(pageItems);
        }
        openInv();
    }

    /**
     * Sets the itemstack which is the back page
     */
    public void setBackPage(ItemStack newBack) {
        backAPage = newBack;
    }

    /**
     * Sets the itemstack which is the forwards page
     */
    public void setForwardsPage(ItemStack newForwards) {
        forwardsAPage = newForwards;
    }

    /**
     * Moves the inventory to a page
     */
    public void setPage(int newPage) {
        if (pages.containsKey(newPage)) {
            currentPage = newPage;
            if (isInventoryInUse()) {
                ItemStack[] pageItems = pages.get(getCurrentPage());
                if (pageItems.length != currentInventory.getSize()
                        || !currentInventory.getTitle().equalsIgnoreCase(getPageTitle())) {
                    currentInventory = Bukkit.createInventory(null, pageItems.length, getPageTitle());
                    currentInventory.setContents(pageItems);
                    openInv();
                } else {
                    currentInventory.setContents(pageItems);
                }
            }
        }
    }

    /**
     * Sets the items in a page
     */
    public void setPage(int pageNo, ItemStack... items) {
        if (items.length % 9 != 0) {
            items = Arrays.copyOf(items, (int) (Math.ceil((double) items.length / 9D) * 9D));
        }
        if (items.length > invSize) {
            throw new RuntimeException("A inventory size of " + items.length + " was passed when the max is " + invSize);
        }
        pages.put(pageNo, items);
    }

    public void setPageDisplayedInTitle(boolean displayPage) {
        if (this.isPageDisplayedInTitle() != displayPage) {
            this.pageDisplayedInTitle = displayPage;
            if (isInventoryInUse()) {
                setPage(getCurrentPage());
            }
        }
    }

    /**
     * @Title = %Title%
     * @Page = %Page%
     */
    public void setPageDisplayTitleFormat(String titleFormat) {
        this.titleFormat = titleFormat;
        if (isInventoryInUse()) {
            setPage(getCurrentPage());
        }
    }

    /**
     * Auto fills out the pages with these items
     */
    public void setPages(ArrayList<ItemStack> allItems) {
        setPages(allItems.toArray(new ItemStack[allItems.size()]));
    }

    /**
     * Auto fills out the pages with these items
     */
    public void setPages(ItemStack... allItems) {
        pages.clear();
        int invPage = 0;
        boolean usePages = allItems.length > invSize;
        ItemStack[] items = null;
        int currentSlot = 0;
        for (int currentItem = 0; currentItem < allItems.length; currentItem++) {
            if (items == null) {
                int size = invSize;
                if (dynamicInventorySize) {
                    size = allItems.length - currentItem;
                    if (usePages)
                        size += 9;
                }
                if (!dynamicInventorySize)
                    size = invSize;
                size = (int) (Math.ceil((double) size / 9)) * 9;
                items = new ItemStack[Math.min(invSize, size)];
            }
            ItemStack item = allItems[currentItem];
            items[currentSlot++] = item;
            if (currentSlot == items.length - (usePages ? 9 : 0) || currentItem + 1 == allItems.length) {
                if (usePages) {
                    if (invPage != 0) {
                        items[items.length - 9] = getBackPage();
                    }
                    if (currentItem + 1 < allItems.length) {
                        items[items.length - 1] = getForwardsPage();
                    }
                }
                pages.put(invPage, items);
                invPage++;
                currentSlot = 0;
                items = null;
            }
        }
        if (pages.keySet().size() < getCurrentPage())
            currentPage = pages.keySet().size() - 1;
        if (allItems.length == 0) {
            pages.put(0, new ItemStack[0]);
        }
        setPage(getCurrentPage());
    }

    /**
     * Sets the title of the next page opened
     */
    public void setTitle(String newTitle) {
        if (!getTitle().equals(newTitle)) {
            title = newTitle;
            if (isInventoryInUse()) {
                setPage(getCurrentPage());
            }
        }
    }

}
