package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.TranslationManager;
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
import org.bukkit.metadata.FixedMetadataValue;

public class KitInventory implements Listener {

    private ItemStack backAPage = null;
    private int currentPage = 0;
    private boolean dynamicInventorySize = HungergamesApi.getConfigManager().isKitSelectorDynamicSize();
    private ItemStack forwardsAPage = null;
    private Hungergames hg;
    private Inventory inv;
    private boolean listenForClose = true;
    private int maxInvSize = HungergamesApi.getConfigManager().getKitSelectorInventorySize();
    private HashMap<Integer, ItemStack[]> pages = new HashMap<Integer, ItemStack[]>();
    private String title;
    private Player user;

    public KitInventory(Player player) {
        hg = HungergamesApi.getHungergames();
        user = player;
        title = HungergamesApi.getTranslationManager().getInventoryWindowSelectKitTitle();
        user.setMetadata("KitInventory", new FixedMetadataValue(hg, this));
        Bukkit.getPluginManager().registerEvents(this, hg);
    }

    public ItemStack[] generatePage(int itemsSize) {
        if (itemsSize > maxInvSize)
            itemsSize = maxInvSize;
        itemsSize = (int) (Math.ceil((double) itemsSize / 9)) * 9;
        return new ItemStack[itemsSize];
    }

    public ItemStack getBackPage() {
        if (backAPage == null) {
            TranslationManager chat = HungergamesApi.getTranslationManager();
            ItemStack item = HungergamesApi.getConfigManager().getKitSelectorBack();
            backAPage = HungergamesApi.getKitSelector().generateItem(item.getType(), item.getDurability(),
                    chat.getItemKitSelectorBackName(), chat.getItemKitSelectorBackDescription());
            backAPage.setAmount(0);
        }
        return backAPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public ItemStack getForwardsPage() {
        if (forwardsAPage == null) {
            TranslationManager chat = HungergamesApi.getTranslationManager();
            ItemStack item = HungergamesApi.getConfigManager().getKitSelectorForward();
            forwardsAPage = HungergamesApi.getKitSelector().generateItem(item.getType(), item.getDurability(),
                    chat.getItemKitSelectorForwardsName(), chat.getItemKitSelectorForwardsDescription());
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

    public String getTitle() {
        return title;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (listenForClose && event.getPlayer() == user) {
            HandlerList.unregisterAll(this);
            if (user.hasMetadata("KitInventory") && user.getMetadata("KitInventory").get(0).value() == this)
                user.removeMetadata("KitInventory", hg);
        }
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

    public void openInventory() {
        if (inv == null) {
            int size = maxInvSize;
            if (pages.size() > 0) {
                size = pages.values().iterator().next().length;
            }
            inv = Bukkit.createInventory(null, size, title);
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

    public void setKits() {
        pages.clear();
        KitManager kits = HungergamesApi.getKitManager();
        boolean usePages = kits.getKits().size() > maxInvSize;
        ItemStack[] items = null;
        int currentSlot = 0;
        ArrayList<Kit> allKits = kits.getKits();
        for (int currentKit = 0; currentKit < allKits.size(); currentKit++) {
            if (items == null) {
                int size = maxInvSize;
                if (dynamicInventorySize) {
                    size = allKits.size() - currentKit;
                    if (usePages)
                        size += 9;
                }
                if (!dynamicInventorySize)
                    size = maxInvSize;
                items = generatePage(size);
            }
            Kit kit = allKits.get(currentKit);
            ItemStack item = kit.getIcon();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + kit.getName());
            meta.setLore(wrap(kit.getDescription()));
            item.setItemMeta(meta);
            if (item.getAmount() == 1)
                item.setAmount(0);
            items[currentSlot++] = item;
            if (currentSlot == items.length - (usePages ? 9 : 0) || currentKit + 1 == allKits.size()) {
                if (usePages) {
                    if (currentPage != 0)
                        items[items.length - 9] = getBackPage();
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

    public void setPage(int newPage) {
        if (pages.containsKey(newPage)) {
            currentPage = newPage;
            ItemStack[] pageItems = pages.get(currentPage);
            if (pageItems.length != inv.getSize()) {
                listenForClose = false;
                inv = Bukkit.createInventory(null, pageItems.length, title);
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

    public void setTitle(String newTitle) {
        title = newTitle;
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

}
