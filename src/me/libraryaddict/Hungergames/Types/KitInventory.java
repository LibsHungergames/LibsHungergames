package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.libraryaddict.Hungergames.Managers.ChatManager;
import me.libraryaddict.Hungergames.Managers.KitManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitInventory implements Listener {

    private Player user;
    private String title;
    private Inventory inv;
    private int currentPage = 0;
    private HashMap<Integer, ItemStack[]> pages = new HashMap<Integer, ItemStack[]>();
    private ItemStack backAPage = null;
    private ItemStack forwardsAPage = null;
    private int maxInvSize = HungergamesApi.getConfigManager().getKitSelectorInventorySize();
    private boolean listenForClose = true;
    private boolean dymanicInventorySize = HungergamesApi.getConfigManager().isKitSelectorDymanicSize();

    public KitInventory(Player player) {
        user = player;
        Bukkit.getPluginManager().registerEvents(this, HungergamesApi.getHungergames());
        title = HungergamesApi.getChatManager().getInventoryWindowSelectKitTitle();
    }

    public ItemStack getBackPage() {
        if (backAPage == null) {
            ChatManager chat = HungergamesApi.getChatManager();
            ItemStack item = HungergamesApi.getConfigManager().getKitSelectorBack();
            backAPage = HungergamesApi.getKitSelector().generateItem(item.getType(), item.getDurability(),
                    chat.getItemKitSelectorBackName(), chat.getItemKitSelectorBackDescription());
            backAPage.setAmount(0);
        }
        return backAPage;
    }

    public ItemStack getForwardsPage() {
        if (forwardsAPage == null) {
            ChatManager chat = HungergamesApi.getChatManager();
            ItemStack item = HungergamesApi.getConfigManager().getKitSelectorForward();
            forwardsAPage = HungergamesApi.getKitSelector().generateItem(item.getType(), item.getDurability(),
                    chat.getItemKitSelectorForwardsName(), chat.getItemKitSelectorForwardsDescription());
            forwardsAPage.setAmount(0);
        }
        return forwardsAPage;
    }

    public void setForwardsPage(ItemStack newForwards) {
        forwardsAPage = newForwards;
    }

    public void setBackPage(ItemStack newBack) {
        backAPage = newBack;
    }

    public void openInventory() {
        user.openInventory(inv);
    }

    public void setKits() {
        pages.clear();
        KitManager kits = HungergamesApi.getKitManager();
        boolean usePages = kits.getKits().size() > maxInvSize;
        // Max chest size is maxInvSize. So we need to make the pages thingy use
        // the
        // bottom row. So maxInvSize-9
        // Then account for the 1 being a zero. Its maxInvSize-10
        ItemStack[] items = null;
        int currentSlot = 0;
        ArrayList<Kit> allKits = kits.getKits();
        for (int currentKit = 0; currentKit < allKits.size(); currentKit++) {
            if (items == null) {
                // Get the inventory size.
                // If there are pages. Then its maxInvSize. Else its according
                // to the
                // kits
                // Alternately
                int size = maxInvSize;
                // If the inv size is according to how much needs to fit in
                if (dymanicInventorySize) {
                    size = allKits.size() - currentKit;
                    size += (usePages ? 9 : 0);
                    // Size is now how many kits are left
                }

                // If its over maxInvSize. Then we set it to maxInvSize.
                if (usePages && !dymanicInventorySize)
                    size = maxInvSize;
                items = generatePage(size);
            }
            // Set the current slot
            Kit kit = allKits.get(currentKit);
            ItemStack item = kit.getIcon();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + kit.getName());
            meta.setLore(wrap(kit.getDescription()));
            item.setItemMeta(meta);
            if (item.getAmount() == 1)
                item.setAmount(0);
            items[currentSlot++] = item;
            // If its the last page, Or no more kits can fit in current page
            if ((currentSlot > items.length - (1 + (usePages ? 9 : 0)) && usePages) || currentKit + 1 == allKits.size()) {
                // Now create the next page and back a page.
                // Check if I make a 'back a page'
                if (usePages) {
                    if (currentPage != 0)
                        items[items.length - 9] = getBackPage();
                    // Check if I can make forwards page
                    if (currentKit + 1 < allKits.size())
                        items[items.length - 1] = getForwardsPage();
                }
                pages.put(currentPage, items);
                currentPage++;
                currentSlot = 0;
                items = null;
            }
        }
        currentPage = 0;
    }

    private List<String> wrap(String string) {
        String[] split = string.split(" ");
        string = "";
        ChatColor color = ChatColor.BLUE;
        ArrayList<String> newString = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            if (string.length() > 20 || string.endsWith(".") || string.endsWith("!")) {
                newString.add(color + string);
                if (string.endsWith(".") || string.endsWith("!"))
                    newString.add("");
                string = "";
            }
            string += (string.length() == 0 ? "" : " ") + split[i];
        }
        newString.add(color + string);
        return newString;
    }

    public void setPage(int newPage) {
        if (pages.containsKey(newPage)) {
            currentPage = newPage;
            ItemStack[] pageItems = pages.get(currentPage);
            if (pageItems.length != inv.getSize()) {
                listenForClose = false;
                inv = Bukkit.createInventory(null, pageItems.length, title);
                inv.setContents(pageItems);
                user.closeInventory();
                Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                    public void run() {
                        user.openInventory(inv);
                        listenForClose = true;
                    }
                });
            } else
                inv.setContents(pageItems);
            // TODO Potentially display title for page
        }
    }

    public ItemStack[] generatePage(int itemsSize) {
        int size = (int) (Math.ceil((double) itemsSize / 9)) * 9;
        if (size > maxInvSize)
            size = maxInvSize;
        return new ItemStack[size];
    }

    @EventHandler
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
                    Kit kit = HungergamesApi.getKitManager().getKitByName(
                            ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                    if (kit != null)
                        Bukkit.dispatchCommand((CommandSender) event.getWhoClicked(), "kit " + kit.getName());
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (listenForClose && event.getPlayer() == user)
            HandlerList.unregisterAll(this);
    }

    public void setPage(int pageNo, ItemStack[] items) {
        if (items.length % 9 == 0)
            pages.put(pageNo, items);
    }

    public String getTitle() {
        return title;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public Player getPlayer() {
        return user;
    }

    public HashMap<Integer, ItemStack[]> getPages() {
        return pages;
    }

    public ItemStack[] getPage(int pageNumber) {
        return pages.get(pageNumber);
    }

}
