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
import me.libraryaddict.Hungergames.Types.HGPageInventory;
import me.libraryaddict.Hungergames.Types.HGPageInventory.InventoryType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryManager {

    private ItemStack buyKitIcon;
    private MainConfig config = HungergamesApi.getConfigManager().getMainConfig();
    private KitManager kits = HungergamesApi.getKitManager();
    private ItemStack kitSelector = null;
    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private ItemStack[] specHeads = new ItemStack[0];

    private TranslationConfig tm = HungergamesApi.getConfigManager().getTranslationsConfig();

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

    public ItemStack generateItem(Material type, int dataValue, String name, String... lore) {
        return generateItem(type.getId(), dataValue, name, Arrays.asList(lore));
    }

    public ItemStack getBuyKit() {
        if (buyKitIcon == null) {
            ItemStack item = HungergamesApi.getConfigManager().getMainConfig().getBuyKitIcon();
            buyKitIcon = generateItem(item.getType(), item.getDurability(), HungergamesApi.getConfigManager()
                    .getTranslationsConfig().getItemBuyKitName(), HungergamesApi.getConfigManager().getTranslationsConfig()
                    .getItemBuyKitDescription());
            buyKitIcon.addEnchantment(EnchantmentManager.UNDROPPABLE, 1);
        }
        return buyKitIcon;
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

    public void openBuyKitInventory(Player p) {
        if (p.hasMetadata("HGPageInventory")) {
            ((HGPageInventory) p.getMetadata("HGPageInventory").get(0).value()).closeInventory();
        }
        HGPageInventory inv = new HGPageInventory(InventoryType.BUYKIT, p, config.isBuyKitMenuDymanic(),
                config.getBuyKitInventorySize());
        inv.setTitle(tm.getBuyKitMenuTitle());
        ItemStack kItem = HungergamesApi.getConfigManager().getMainConfig().getBuyKitItemBack();
        inv.setBackPage(generateItem(kItem.getType(), kItem.getDurability(), tm.getItemBuyKitInventoryBackName(),
                tm.getItemBuyKitBackDescription()));
        kItem = HungergamesApi.getConfigManager().getMainConfig().getBuyKitItemForwards();
        inv.setForwardsPage(generateItem(kItem.getType(), kItem.getDurability(), tm.getItemBuyKitInventoryForwardsName(),
                tm.getItemBuyKitForwardsDescription()));
        {
            ArrayList<ItemStack> nonOwned = new ArrayList<ItemStack>();
            ArrayList<Kit> allKits = kits.getKits();
            List<Kit> hisKits = kits.getPlayersKits(p);
            if (hisKits == null)
                hisKits = new ArrayList<Kit>();
            for (int currentKit = 0; currentKit < allKits.size(); currentKit++) {
                Kit kit = allKits.get(currentKit);
                if (kit.isFree() || kit.getPrice() <= 0 || kits.ownsKit(p, kit)) {
                    continue;
                }
                ItemStack item = kit.getIcon();
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.WHITE + kit.getName());
                List<String> lore = wrap(kit.getDescription());
                lore.add(0, String.format(tm.getBuyKitMenuItemPrice(), kit.getPrice()));
                meta.setLore(lore);
                item.setItemMeta(meta);
                if (item.getAmount() == 1)
                    item.setAmount(0);
                nonOwned.add(item);
            }
            if (nonOwned.isEmpty()) {
                p.sendMessage(tm.getBuyKitMenuOwnAllKits());
                return;
            }
            inv.setPages(nonOwned);
        }
        inv.openInventory();
    }

    public void openKitInventory(Player p) {
        if (p.hasMetadata("HGPageInventory"))
            return;
        HGPageInventory inv = new HGPageInventory(InventoryType.KIT, p, config.isKitSelectorSizeDynamic(),
                config.getKitSelectorInventorySize());
        inv.setTitle(tm.getSelectKitInventoryTitle());
        ItemStack kItem = HungergamesApi.getConfigManager().getMainConfig().getKitSelectorBack();
        inv.setBackPage(generateItem(kItem.getType(), kItem.getDurability(), tm.getItemKitSelectorBackName(),
                tm.getItemKitSelectorBackDescription()));
        kItem = HungergamesApi.getConfigManager().getMainConfig().getKitSelectorForward();
        inv.setForwardsPage(generateItem(kItem.getType(), kItem.getDurability(), tm.getItemKitSelectorForwardsName(),
                tm.getItemKitSelectorForwardsDescription()));
        {
            ArrayList<ItemStack> kitItems = new ArrayList<ItemStack>();
            ArrayList<Kit> allKits = kits.getKits();
            List<Kit> hisKits = kits.getPlayersKits(p);
            if (hisKits == null)
                hisKits = new ArrayList<Kit>();
            for (int currentKit = 0; currentKit < allKits.size(); currentKit++) {
                Kit kit = allKits.get(currentKit);
                if (kits.ownsKit(p, kit) || config.isDisplayUnusableKitsInSelector()) {
                    ItemStack item = kit.getIcon();
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.WHITE + kit.getName()
                            + (kits.ownsKit(p, kit) ? tm.getInventoryOwnKit() : tm.getInventoryDontOwnKit()));
                    meta.setLore(wrap(kit.getDescription()));
                    item.setItemMeta(meta);
                    if (item.getAmount() == 1)
                        item.setAmount(0);
                    kitItems.add(item);
                }
            }
            if (kitItems.isEmpty()) {
                p.sendMessage(tm.getKitSelectorNoKitsToShow());
                return;
            }
            inv.setPages(kitItems);
        }
        inv.openInventory();
    }

    public void openSpectatorInventory(Player p) {
        if (p.hasMetadata("HGPageInventory"))
            return;
        HGPageInventory inv = new HGPageInventory(InventoryType.SPECTATOR, p, true, 54);
        ItemStack item = HungergamesApi.getConfigManager().getMainConfig().getSpectatorItemBack();
        inv.setBackPage(generateItem(item.getType(), item.getDurability(), tm.getItemSpectatorInventoryBackName(),
                tm.getItemSpectatorInventoryBackDescription()));
        item = HungergamesApi.getConfigManager().getMainConfig().getSpectatorItemForwards();
        inv.setForwardsPage(generateItem(item.getType(), item.getDurability(), tm.getItemSpectatorInventoryForwardsName(),
                tm.getItemSpectatorInventoryForwardsDescription()));
        inv.setPages(specHeads);
        inv.setTitle(tm.getSpectatorInventoryTitle());
        inv.openInventory();
    }

    public void setHeads() {
        ArrayList<String> names = new ArrayList<String>();
        for (Gamer gamer : pm.getGamers()) {
            if (gamer.isAlive())
                names.add(gamer.getName());
        }
        if (LibsFeastManager.getFeastManager().getFeastLocation() != null
                && LibsFeastManager.getFeastManager().getFeastLocation().getY() > 0) {
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

    public void updateSpectatorHeads() {
        setHeads();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasMetadata("HGPageInventory")) {
                HGPageInventory inv = (HGPageInventory) p.getMetadata("HGPageInventory").get(0).value();
                if (inv.getType() == InventoryType.SPECTATOR) {
                    inv.setPages(specHeads);
                }
            }
        }
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
