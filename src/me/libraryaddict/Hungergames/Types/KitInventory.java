package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.libraryaddict.Hungergames.Managers.KitManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

    public KitInventory(Player player) {
        user = player;
        Bukkit.getPluginManager().registerEvents(this, HungergamesApi.getHungergames());
        title = HungergamesApi.getChatManager().getInventoryWindowSelectKitTitle();
    }

    public ItemStack getBackPage() {
        if (backAPage == null)
            backAPage = HungergamesApi.getKitSelector().generateItem(Material.SUGAR_CANE_BLOCK, 0, ChatColor.RED + "Back", null);
        return backAPage;
    }

    public ItemStack getForwardsPage() {
        if (forwardsAPage == null)
            forwardsAPage = HungergamesApi.getKitSelector().generateItem(Material.SUGAR_CANE_BLOCK, 0,
                    ChatColor.RED + "Forwards", null);
        return forwardsAPage;
    }

    public void openInventory() {
        if (inv == null) {
            int size = 54;
            if (pages.size() != 1)
                size = 54;
            else {
                size = pages.values().iterator().next().length;
            }
            inv = Bukkit.createInventory(null, size, title);
            setPage(currentPage);
        }
        user.openInventory(inv);
    }

    public void addKits() {
        KitManager kits = HungergamesApi.getKitManager();
        // TODO Turn this into a config option
        boolean usePages = kits.getKits().size() > 54;
        boolean dymanicInventorySize = true;
        // Max chest size is 54. So we need to make the pages thingy use the
        // bottom row. So 54-9
        // Then account for the 1 being a zero. Its 54-10
        ItemStack[] items = null;
        int currentSlot = 0;
        ArrayList<Kit> allKits = kits.getKits();
        for (int currentKit = 0; currentKit < allKits.size(); currentKit++) {
            if (items == null) {
                // Get the inventory size.
                // If there are pages. Then its 54. Else its according to the
                // kits
                // Alternately
                int size = 54;
                // If the inv size is according to how much needs to fit in
                if (dymanicInventorySize)
                    size = allKits.size() - currentKit;
                // If its over 54. Then we set it to 54.
                if (usePages || size > 54)
                    size = 54;
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
            if ((currentSlot > 54 - 9 && usePages) || currentKit + 1 == allKits.size()) {
                // Now create the next page and back a page.
                // Check if I make a 'back a page'
                if (currentPage != 0)
                    items[53 - 8] = getBackPage();
                // Check if I can make forwards page
                if (currentKit + 1 < allKits.size())
                    items[53] = getForwardsPage();
                pages.put(currentPage, items);
                currentPage++;
                currentSlot = 0;
                items = null;
            }
        }
        Iterator<Integer> itel = pages.keySet().iterator();
        while (itel.hasNext()) {
            int no = itel.next();
            System.out.print(no + "   " + pages.get(no).length);
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
            inv.setContents(pages.get(currentPage));
            // TODO Add check if he wants the title changed
            // But it requires me to close inventory and reopen. So guess not
        } else
            System.out.print("Page no foundee");
    }

    public ItemStack[] generatePage(int itemsSize) {
        int size = (int) (Math.ceil((double) itemsSize / 9)) * 9;
        if (size > 54)
            size = 54;
        return new ItemStack[size];
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // TODO Handle the click
        if (event.getView().getTitle() != null && event.getView().getTitle().equals(title)) {
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
        if (event.getPlayer() == user)
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
