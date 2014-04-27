package me.libraryaddict.Hungergames.Managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.libraryaddict.Hungergames.Configs.MainConfig;
import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Listeners.LibsFeastManager;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Kit;
import me.libraryaddict.Hungergames.Types.PageInventory;
import me.libraryaddict.Hungergames.Types.PageInventory.InventoryType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryManager {

    private ItemStack kitSelector = null;
    private ItemStack[] specHeads = new ItemStack[0];
    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private TranslationConfig tm = HungergamesApi.getConfigManager().getTranslationsConfig();
    private KitManager kits = HungergamesApi.getKitManager();
    private MainConfig config = HungergamesApi.getConfigManager().getMainConfig();

    public void setHeads() {
        ArrayList<String> names = new ArrayList<String>();
        for (Gamer gamer : pm.getGamers()) {
            if (gamer.isAlive())
                names.add(gamer.getName());
        }
        if (LibsFeastManager.getFeastManager().getFeastLocation().getY() > 0) {
            names.add("Feast");
        }
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        ArrayList<ItemStack> heads = new ArrayList<ItemStack>();
        for (String name : names) {
            if (name.equals("Feast")) {
                ItemStack head = new ItemStack(Material.CAKE, 0);
                ItemMeta meta = head.getItemMeta();
                meta.setDisplayName(tm.getSpectatorInventoryFeastName());
                meta.setLore(Arrays.asList(tm.getSpectatorInventoryFeastDescription().split("\n")));
                head.setItemMeta(meta);
                heads.add(head);
            } else {
                Gamer gamer = pm.getGamer(name);
                ItemStack head = new ItemStack(Material.SKULL_ITEM, 0, (short) 3);
                Kit kit = kits.getKitByPlayer(gamer.getPlayer());
                if (kit != null && kit.getIcon() != null)
                    head = kit.getIcon();
                head.setAmount(0);
                ItemMeta meta = head.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + name);
                List<String> lore = new ArrayList<String>();
                lore.add(String.format(tm.getSpectatorHeadKills(), gamer.getKills()));
                lore.add(String.format(tm.getSpectatorHeadKit(),
                        (kit == null ? tm.getMessagePlayerShowKitsNoKit() : kit.getName())));
                meta.setLore(lore);
                head.setItemMeta(meta);
                heads.add(head);
            }
        }
        specHeads = heads.toArray(new ItemStack[heads.size()]);
    }

    public ItemStack generateItem(int id, int dataValue, String name, List<String> lore) {
        ItemStack item = new ItemStack(id, 1, (short) dataValue);
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.setDisplayName(ChatColor.WHITE + name);
        }
        if (lore != null && lore.size() > 0) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack generateItem(int type, int dataValue, String name, String[] lore) {
        return generateItem(type, dataValue, name, Arrays.asList(lore));
    }

    public ItemStack generateItem(Material type, int dataValue, String name, List<String> lore) {
        return generateItem(type.getId(), dataValue, name, lore);
    }

    public ItemStack generateItem(Material type, int dataValue, String name, String[] lore) {
        return generateItem(type.getId(), dataValue, name, Arrays.asList(lore));
    }

    public ItemStack getKitSelector() {
        if (kitSelector == null) {
            ItemStack item = HungergamesApi.getConfigManager().getMainConfig().getKitSelectorIcon();
            kitSelector = generateItem(item.getType(), item.getDurability(), HungergamesApi.getConfigManager()
                    .getTranslationsConfig().getItemKitSelectorName(), HungergamesApi.getConfigManager().getTranslationsConfig()
                    .getItemKitSelectorDescription());
            kitSelector.addEnchantment(EnchantmentManager.UNDROPPABLE, 1);
        }
        return kitSelector;
    }

    public void openKitInventory(Player p) {
        if (p.hasMetadata("PageInventory"))
            return;
        PageInventory inv = new PageInventory(InventoryType.KIT, p, config.isKitSelectorSizeDynamic(),
                config.getKitSelectorInventorySize());
        {
            ArrayList<ItemStack> kitItems = new ArrayList<ItemStack>();
            ArrayList<ItemStack> nonOwned = new ArrayList<ItemStack>();
            ArrayList<Kit> allKits = kits.getKits();
            List<Kit> hisKits = kits.getPlayersKits(p);
            if (hisKits == null)
                hisKits = new ArrayList<Kit>();
            boolean sortOwned = HungergamesApi.getConfigManager().getMainConfig().isSortKitGuiByOwned();
            for (int currentKit = 0; currentKit < allKits.size(); currentKit++) {
                Kit kit = allKits.get(currentKit);
                ItemStack item = kit.getIcon();
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.WHITE + kit.getName()
                        + (kits.ownsKit(p, kit) ? tm.getInventoryOwnKit() : tm.getInventoryDontOwnKit()));
                meta.setLore(wrap(kit.getDescription()));
                item.setItemMeta(meta);
                if (item.getAmount() == 1)
                    item.setAmount(0);
                if (sortOwned && !kits.ownsKit(p, kit)) {
                    nonOwned.add(item);
                } else {
                    kitItems.add(item);
                }
            }
            if (!nonOwned.isEmpty()) {
                kitItems.add(null);
            }
            kitItems.addAll(nonOwned);
            inv.setPages(kitItems);
        }
        inv.setTitle(tm.getSelectKitInventoryTitle());
        ItemStack item = HungergamesApi.getConfigManager().getMainConfig().getKitSelectorBack();
        inv.setBackPage(HungergamesApi.getInventoryManager().generateItem(item.getType(), item.getDurability(),
                tm.getItemKitSelectorBackName(), tm.getItemKitSelectorBackDescription()));
        item = HungergamesApi.getConfigManager().getMainConfig().getKitSelectorForward();
        inv.setForwardsPage(HungergamesApi.getInventoryManager().generateItem(item.getType(), item.getDurability(),
                tm.getItemKitSelectorForwardsName(), tm.getItemKitSelectorForwardsDescription()));
        inv.openInventory();
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

    public void openSpectatorInventory(Player p) {
        if (p.hasMetadata("PageInventory"))
            return;
        PageInventory inv = new PageInventory(InventoryType.SPECTATOR, p, true, 54);
        inv.setPages(specHeads);
        inv.setTitle(tm.getSpectatorInventoryTitle());
        ItemStack item = HungergamesApi.getConfigManager().getMainConfig().getSpectatorItemBack();
        inv.setBackPage(HungergamesApi.getInventoryManager().generateItem(item.getType(), item.getDurability(),
                tm.getItemSpectatorInventoryBackName(), tm.getItemSpectatorInventoryBackDescription()));
        item = HungergamesApi.getConfigManager().getMainConfig().getSpectatorItemForwards();
        inv.setForwardsPage(HungergamesApi.getInventoryManager().generateItem(item.getType(), item.getDurability(),
                tm.getItemSpectatorInventoryForwardsName(), tm.getItemSpectatorInventoryForwardsDescription()));
        inv.openInventory();
    }

    public void updateSpectatorHeads() {
        setHeads();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasMetadata("PageInventory")) {
                PageInventory inv = (PageInventory) p.getMetadata("PageInventory").get(0).value();
                if (inv.getType() == InventoryType.SPECTATOR) {
                }
            }
        }
    }
}
