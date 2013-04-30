package me.libraryaddict.Hungergames.Managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Types.Enchants;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Kit;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class KitManager {
    /**
     * Kits every player gets by default
     */
    public ConcurrentLinkedQueue<Kit> defaultKits = new ConcurrentLinkedQueue<Kit>();
    public ConcurrentHashMap<String, List<Kit>> hisKits = new ConcurrentHashMap<String, List<Kit>>();
    /**
     * Kits every player has
     */
    /**
     * List of every kit
     */
    public ArrayList<Kit> kits = new ArrayList<Kit>();
    public String defaultKit;
    private Hungergames hg = HungergamesApi.getHungergames();

    public KitManager() {
        hg.saveDefaultConfig();
        ConfigurationSection config = hg.getConfig();
        defaultKit = config.getString("DefaultKit");
        for (String string : config.getConfigurationSection("Kits").getKeys(false)) {
            if (config.contains("BadKits") && config.getStringList("BadKits").contains(string))
                continue;
            Kit kit = parseKit(config.getConfigurationSection("Kits." + string));
            kits.add(kit);
            if (kit.isFree())
                defaultKits.add(kit);
        }
        List<String> kitNames = new ArrayList<String>();
        for (Kit kit : kits)
            kitNames.add(kit.getName());
        Collections.sort(kitNames);
        ArrayList<Kit> newKit = new ArrayList<Kit>();
        for (int i = 0; i < kitNames.size(); i++) {
            Kit kit = getKitByName(kitNames.get(i));
            kit.setId(i);
            newKit.add(kit);
        }
        kits = newKit;
    }

    public void addKit(Kit newKit) {
        kits.add(newKit);
        if (newKit.isFree())
            defaultKits.add(newKit);
        List<String> kitNames = new ArrayList<String>();
        for (Kit kit : kits)
            kitNames.add(kit.getName());
        Collections.sort(kitNames);
        ArrayList<Kit> newKits = new ArrayList<Kit>();
        for (int i = 0; i < kitNames.size(); i++) {
            Kit kit = getKitByName(kitNames.get(i));
            kit.setId(i);
            newKits.add(kit);
        }
        kits = newKits;
    }

    public boolean setKit(Player p, String name) {
        Kit kit = getKitByName(name);
        if (kit == null)
            return false;
        Kit kita = getKitByPlayer(p.getName());
        if (kita != null)
            kita.removePlayer(p.getName());
        kit.addPlayer(p.getName());
        return true;
    }

    public Kit parseKit(ConfigurationSection path) {
        String desc = ChatColor.translateAlternateColorCodes('&', path.getString("Description"));
        String name = path.getString("Name");
        if (name == null)
            name = path.getName();
        name = ChatColor.translateAlternateColorCodes('&', name);
        ItemStack[] armor = new ItemStack[4];
        armor[3] = parseItem(path.getString("Helmet"))[0];
        armor[2] = parseItem(path.getString("Chestplate"))[0];
        armor[1] = parseItem(path.getString("Leggings"))[0];
        armor[0] = parseItem(path.getString("Boots"))[0];
        List<String> itemList = path.getStringList("Items");
        ArrayList<ItemStack> item = new ArrayList<ItemStack>();
        if (itemList != null)
            for (String string : itemList) {
                ItemStack[] itemstacks = parseItem(string);
                for (ItemStack itemstack : itemstacks)
                    if (itemstack != null)
                        item.add(itemstack);
            }
        ItemStack[] items = new ItemStack[item.size()];
        for (int n = 0; n < item.size(); n++)
            items[n] = item.get(n);
        List<String> abilityList = path.getStringList("Ability");
        String[] ability;
        if (abilityList != null) {
            ability = new String[abilityList.size()];
            for (int n = 0; n < abilityList.size(); n++)
                ability[n] = abilityList.get(n);
        } else
            ability = new String[0];
        ItemStack icon = new ItemStack(Material.STONE);
        if (path.contains("Icon")) {
            icon = this.parseItem(path.getString("Icon"))[0];
            if (icon == null)
                icon = new ItemStack(Material.STONE);
        }
        Kit kit = new Kit(name, icon, armor, items, desc, ability);
        if (path.getBoolean("Free", false) == true)
            kit.setFree(true);
        if (path.getInt("Price", -1) != -1)
            kit.setPrice(path.getInt("Price"));
        return kit;
    }

    /*
     * public boolean hasAbility(Player p, String ability) { if (hg.currentTime
     * < 0) return false; if (p == null) return false; Kit kit =
     * kitty.getKitByPlayer(p.getName()); if (kit == null ||
     * !kit.hasAbility(ability)) return false; return true; }
     */

    public boolean canFit(Inventory pInv, ItemStack[] items) {
        Inventory inv = Bukkit.createInventory(null, pInv.getContents().length);
        for (int i = 0; i < inv.getSize(); i++) {
            if (pInv.getItem(i) == null || pInv.getItem(i).getType() == Material.AIR)
                continue;
            inv.setItem(i, pInv.getItem(i).clone());
        }
        for (ItemStack i : items) {
            HashMap<Integer, ItemStack> item = inv.addItem(i);
            if (item != null && !item.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void addItem(Player p, ItemStack item) {
        int repeat = item.getAmount();
        item.setAmount(1);
        for (int i = 0; i < repeat; i++) {
            if (canFit(p.getInventory(), new ItemStack[] { item }))
                p.getInventory().addItem(item);
            else {
                if (item == null || item.getType() == Material.AIR)
                    continue;
                else if (item.hasItemMeta())
                    p.getWorld().dropItemNaturally(p.getLocation(), item.clone()).getItemStack().setItemMeta(item.getItemMeta());
                else
                    p.getWorld().dropItemNaturally(p.getLocation(), item);
            }
        }
    }

    /*
     * public boolean hasAbility(String player, String ability) { Kit kit =
     * kitty.getKitByPlayer(player); if (kit == null ||
     * !kit.hasAbility(ability)) return false; return true; }
     */

    private ItemStack[] parseItem(String string) {
        if (string == null)
            return new ItemStack[] { null };
        String[] args = string.split(" ");
        try {
            double amount = Integer.parseInt(args[2]);
            ItemStack[] items = new ItemStack[(int) Math.ceil(amount / 64)];
            if (items.length <= 0)
                return new ItemStack[] { null };
            for (int i = 0; i < items.length; i++) {
                int id = hg.isNumeric(args[0]) ? Integer.parseInt(args[0])
                        : (Material.getMaterial(args[0].toUpperCase()) == null ? Material.AIR : Material.getMaterial(args[0]
                                .toUpperCase())).getId();
                if (id == 0) {
                    System.out.print("Failed to recognise item ID " + args[0]);
                    return new ItemStack[] { null };
                }
                ItemStack item = new ItemStack(id, (int) amount, (short) Integer.parseInt(args[1]));
                String[] newArgs = Arrays.copyOfRange(args, 3, args.length);
                for (String argString : newArgs) {
                    if (argString.contains("Name=")) {
                        String name = ChatColor.translateAlternateColorCodes('&', argString.substring(5)).replaceAll("_", " ");
                        if (ChatColor.getLastColors(name).equals(""))
                            name = ChatColor.WHITE + name;
                        ItemMeta meta = item.getItemMeta();
                        String previous = meta.getDisplayName();
                        if (previous == null)
                            previous = "";
                        meta.setDisplayName(name + previous);
                        item.setItemMeta(meta);
                    } else if (argString.contains("Color=") && item.getType().name().contains("LEATHER")) {
                        String[] name = argString.substring(6).split(":");
                        int[] ids = new int[3];
                        for (int o = 0; o < 3; o++)
                            ids[o] = Integer.parseInt(name[o]);
                        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                        meta.setColor(Color.fromRGB(ids[0], ids[1], ids[2]));
                        item.setItemMeta(meta);
                    } else if (argString.equalsIgnoreCase("UniqueItem")) {
                        ItemMeta meta = item.getItemMeta();
                        String previous = meta.getDisplayName();
                        if (previous == null)
                            previous = "";
                        meta.setDisplayName(previous + "UniqueIdentifier");
                        item.setItemMeta(meta);
                    }
                }
                for (int n = 0; n < newArgs.length; n++) {
                    Enchantment ench = Enchantment.getByName(newArgs[n]);
                    if (ench == null)
                        ench = Enchantment.getByName(newArgs[n].replace("_", " "));
                    if (ench == null)
                        ench = Enchantment.getByName(newArgs[n].replace("_", " ").toUpperCase());
                    if (ench == null)
                        continue;
                    item.addUnsafeEnchantment(ench, Integer.parseInt(newArgs[n + 1]));
                    n++;
                }
                item = Enchants.updateEnchants(item);
                amount = amount - 64;
                items[i] = item;
            }
            return items;
        } catch (Exception ex) {
            System.out.print("Error while parsing itemstack line " + string + ", " + ex.getMessage());
        }
        return new ItemStack[] { null };
    }

    public String toReadable(String string) {
        String[] names = string.split("_");
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].substring(0, 1) + names[i].substring(1).toLowerCase();
        }
        return (StringUtils.join(names, " "));
    }

    public Kit getKitByName(String name) {
        for (Kit kit : kits)
            if (ChatColor.stripColor(kit.getName()).equalsIgnoreCase(name))
                return kit;
            else if (hg.isNumeric(name) && Integer.parseInt(name) == kit.getId())
                return kit;
        return null;
    }

    public Kit getKitByPlayer(String name) {
        for (Kit kit : kits)
            if (kit.getPlayers().contains(name))
                return kit;
        return null;
    }

    public void showKits(Player p) {
        List<String> hisKits = new ArrayList<String>();
        List<String> otherKits = new ArrayList<String>();
        String currentKit = "None";
        if (getKitByPlayer(p.getName()) != null)
            currentKit = getKitByPlayer(p.getName()).getName();
        for (Kit kit : kits)
            if (ownsKit(p, kit))
                hisKits.add(kit.getName());
            else
                otherKits.add(kit.getName());
        Collections.sort(hisKits, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(otherKits, String.CASE_INSENSITIVE_ORDER);
        if (getKitByPlayer(p.getName()) != null)
            p.sendMessage(ChatColor.GREEN + "Your current kit: " + ChatColor.RESET + currentKit);
        if (hisKits.size() == 0)
            p.sendMessage(ChatColor.GREEN + "Your kits: " + ChatColor.RESET + "No kits available..");
        else {
            String list = StringUtils.join(hisKits, ", ");
            p.sendMessage(ChatColor.GREEN + "Your kits: " + ChatColor.RESET + list + ".");
        }
        if (otherKits.size() == 0)
            p.sendMessage(ChatColor.GREEN + "Other kits: " + ChatColor.RESET + "No kits available..");
        else {
            String list = StringUtils.join(otherKits, ", ");
            p.sendMessage(ChatColor.GREEN + "Other kits: " + ChatColor.RESET + list + ".");
        }
        p.sendMessage(ChatColor.GREEN + "To view the information on a kit, Use /kitinfo <Kit>");
    }

    public boolean ownsKit(Player player, Kit kit) {
        if (defaultKits.contains(kit))
            return true;
        if (player.hasPermission(kit.getPermission()))
            return true;
        return hisKits.containsKey(player.getName()) && hisKits.get(player.getName()).contains(kit);
    }

    public void sendDescription(CommandSender p, String name) {
        Kit kit = getKitByName(name);
        if (kit == null) {
            p.sendMessage(ChatColor.RED + "This kit does not exist!");
            return;
        }
        p.sendMessage(ChatColor.DARK_AQUA + "Name: " + ChatColor.AQUA + kit.getName());
        p.sendMessage(ChatColor.DARK_AQUA + "Kit Id: " + ChatColor.AQUA + kit.getId());
        p.sendMessage(ChatColor.AQUA + kit.getDescription());
        if (kit.isFree())
            p.sendMessage(ChatColor.DARK_AQUA + "Price: " + ChatColor.AQUA + "Free");
        else if (kit.getPrice() == -1)
            p.sendMessage(ChatColor.DARK_AQUA + "Price: " + ChatColor.AQUA + "Unbuyable");
        else
            p.sendMessage(ChatColor.DARK_AQUA + "Price: " + ChatColor.AQUA + "$" + kit.getPrice());
        p.sendMessage(ChatColor.AQUA + "Use /kititems " + kit.getName() + " to view the items given with this kit");
        p.sendMessage(ChatColor.AQUA + "Use /buykit " + kit.getName() + " to purchase a kit");
    }

    private String itemToName(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return "No Item";
        String name = (item.getAmount() > 1 ? item.getAmount() + " " : "")
                + (item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? ChatColor.stripColor(item.getItemMeta()
                        .getDisplayName()) : this.toReadable(item.getType().name())) + (item.getAmount() > 1 ? "s" : "");
        ArrayList<String> enchants = new ArrayList<String>();
        for (Enchantment enchant : item.getEnchantments().keySet()) {
            String eName = Enchants.getReadableName(enchant);
            enchants.add(this.toReadable((eName.contains("%no%") ? eName.replace("%no%", "" + item.getEnchantmentLevel(enchant))
                    : eName + " " + item.getEnchantmentLevel(enchant))));
        }
        Collections.sort(enchants);
        if (enchants.size() > 0)
            name += " with enchant" + (enchants.size() > 1 ? "s" : "") + ": " + StringUtils.join(enchants, ", ");
        return name;
    }

    public void sendKitItems(CommandSender p, String name) {
        Kit kit = getKitByName(name);
        if (kit == null) {
            p.sendMessage(ChatColor.RED + "This kit does not exist!");
            return;
        }
        p.sendMessage(ChatColor.DARK_AQUA + "Kit Name: " + ChatColor.AQUA + kit.getName());
        p.sendMessage(ChatColor.DARK_AQUA + "Helmet: " + ChatColor.AQUA + itemToName(kit.getArmor()[3]));
        p.sendMessage(ChatColor.DARK_AQUA + "Chestplate: " + ChatColor.AQUA + itemToName(kit.getArmor()[2]));
        p.sendMessage(ChatColor.DARK_AQUA + "Leggings: " + ChatColor.AQUA + itemToName(kit.getArmor()[1]));
        p.sendMessage(ChatColor.DARK_AQUA + "Boots: " + ChatColor.AQUA + itemToName(kit.getArmor()[0]));
        ArrayList<String> items = new ArrayList<String>();
        for (ItemStack item : kit.getItems())
            items.add(itemToName(item));
        Collections.sort(items);
        p.sendMessage(ChatColor.DARK_AQUA + "Other items: " + ChatColor.AQUA
                + (items.size() > 0 ? StringUtils.join(items, ", ") + "." : "No other items to display"));
    }
}
