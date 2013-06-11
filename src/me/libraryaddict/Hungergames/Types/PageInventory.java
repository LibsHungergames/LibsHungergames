package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.HashMap;
import me.libraryaddict.Hungergames.Hungergames;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public abstract class PageInventory extends ClickInventory {

    protected ItemStack backAPage = null;
    protected int currentPage = 0;
    protected boolean dynamicInventorySize;
    protected ItemStack forwardsAPage = null;
    protected Hungergames hg;
    protected boolean listenForClose = true;
    protected int maxInvSize = 54;
    protected HashMap<Integer, ItemStack[]> pages = new HashMap<Integer, ItemStack[]>();
    protected Player user;

    public PageInventory(Player player, boolean dymanicInventory) {
        dynamicInventorySize = dymanicInventory;
        hg = HungergamesApi.getHungergames();
        user = player;
        user.setMetadata(getClass().getSimpleName(), new FixedMetadataValue(hg, this));
    }

    public ItemStack[] generatePage(int itemsSize) {
        if (itemsSize > maxInvSize)
            itemsSize = maxInvSize;
        itemsSize = (int) (Math.ceil((double) itemsSize / 9)) * 9;
        return new ItemStack[itemsSize];
    }

    public ItemStack getBackPage() {
        if (backAPage == null) {
            ItemStack item = new ItemStack(Material.SUGAR_CANE_BLOCK);
            backAPage = HungergamesApi.getInventoryManager().generateItem(item.getType(), item.getDurability(),
                    ChatColor.RED + "Back", new String[] { ChatColor.BLUE + "Click this to move back" });
            backAPage.setAmount(0);
        }
        return backAPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public ItemStack getForwardsPage() {
        if (forwardsAPage == null) {
            ItemStack item = new ItemStack(Material.SUGAR_CANE_BLOCK);
            forwardsAPage = HungergamesApi.getInventoryManager().generateItem(item.getType(), item.getDurability(),
                    ChatColor.RED + "Forward", new String[] { ChatColor.BLUE + "Click this to move forward" });
            forwardsAPage.setAmount(0);
        }
        return forwardsAPage;
    }

    public ItemStack[] getPage(int pageNumber) {
        return pages.get(pageNumber);
    }

    public HashMap<Integer, ItemStack[]> getPages() {
        return pages;
    }

    public Player getPlayer() {
        return user;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (listenForClose && event.getPlayer() == user) {
            HandlerList.unregisterAll(this);
            if (user.hasMetadata(getClass().getSimpleName())
                    && user.getMetadata(getClass().getSimpleName()).get(0).value() == this)
                user.removeMetadata(getClass().getSimpleName(), hg);
        }
    }
    
    //@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getViewers().equals(inv.getViewers())) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                if (item.equals(getBackPage())) {
                    setPage(currentPage - 1);
                } else if (item.equals(getForwardsPage())) {
                    setPage(currentPage + 1);
                } else {
                    // Do whatever
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer() == user) {
            HandlerList.unregisterAll(this);
            if (user.hasMetadata(getClass().getSimpleName())
                    && user.getMetadata(getClass().getSimpleName()).get(0).value() == this)
                user.removeMetadata(getClass().getSimpleName(), hg);
        }
    }

    public void openInventory() {
        if (inv == null) {
            int size = maxInvSize;
            if (pages.size() > 0) {
                size = pages.values().iterator().next().length;
            }
            inv = Bukkit.createInventory(null, size, getTitle());
            setPage(currentPage);
        }
        user.openInventory(inv);
    }

    public void setBackPage(ItemStack newBack) {
        backAPage = newBack;
    }

    public void setForwardsPage(ItemStack newForwards) {
        forwardsAPage = newForwards;
    }

    public void setPage(int newPage) {
        if (inv == null) {
            int size = maxInvSize;
            if (pages.size() > 0) {
                size = pages.values().iterator().next().length;
            }
            inv = Bukkit.createInventory(null, size, getTitle());
        }
        if (pages.containsKey(newPage)) {
            currentPage = newPage;
            ItemStack[] pageItems = pages.get(currentPage);
            if (pageItems.length != inv.getSize()) {
                listenForClose = false;
                inv = Bukkit.createInventory(null, pageItems.length, getTitle());
                inv.setContents(pageItems);
                user.closeInventory();
                Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
                    public void run() {
                        user.openInventory(inv);
                        listenForClose = true;
                    }
                });
            } else
                inv.setContents(pageItems);
            // TODO Potentially display title for page no
        }
    }

    public void setPage(int pageNo, ItemStack[] items) {
        if (items.length % 9 == 0)
            pages.put(pageNo, items);
    }

    public void setPages(ArrayList<ItemStack> allItems) {
        setPages(allItems.toArray(new ItemStack[allItems.size()]));
    }

    public void setPages(ItemStack[] allItems) {
        pages.clear();
        int oldPage = currentPage;
        currentPage = 0;
        boolean usePages = allItems.length > maxInvSize;
        ItemStack[] items = null;
        int currentSlot = 0;
        for (int currentItem = 0; currentItem < allItems.length; currentItem++) {
            if (items == null) {
                int size = maxInvSize;
                if (dynamicInventorySize) {
                    size = allItems.length - currentItem;
                    if (usePages)
                        size += 9;
                }
                if (!dynamicInventorySize)
                    size = maxInvSize;
                items = generatePage(size);
            }
            ItemStack item = allItems[currentItem];
            items[currentSlot++] = item;
            if (currentSlot == items.length - (usePages ? 9 : 0) || currentItem + 1 == allItems.length) {
                if (usePages) {
                    if (currentPage != 0)
                        items[items.length - 9] = getBackPage();
                    if (currentItem + 1 < allItems.length)
                        items[items.length - 1] = getForwardsPage();
                }
                pages.put(currentPage, items);
                currentPage++;
                currentSlot = 0;
                items = null;
            }
        }
        currentPage = oldPage;
        if (pages.keySet().size() < oldPage)
            currentPage = pages.keySet().size() - 1;
        setPage(currentPage);
    }

}
