package me.libraryaddict.Hungergames.Managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Configs.LoggerConfig;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Kit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class KitManager {
    private LoggerConfig cm = HungergamesApi.getConfigManager().getLoggerConfig();
    public String defaultKitName;
    private ConcurrentLinkedQueue<Kit> defaultKits = new ConcurrentLinkedQueue<Kit>();
    private Hungergames hg = HungergamesApi.getHungergames();
    private ConcurrentHashMap<String, List<Kit>> hisKits = new ConcurrentHashMap<String, List<Kit>>();
    private ArrayList<Kit> kits = new ArrayList<Kit>();

    public KitManager() {
        File file = new File(hg.getDataFolder().toString() + "/kits.yml");
        ConfigurationSection config;
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            hg.saveResource("kits.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
        defaultKitName = config.getString("DefaultKit", null);
        for (String string : config.getConfigurationSection("Kits").getKeys(false)) {
            if (config.contains("BadKits") && config.getStringList("BadKits").contains(string))
                continue;
            Kit kit = parseKit(config.getConfigurationSection("Kits." + string));
            addKit(kit);
        }
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

    public void addKit(final Kit newKit) {
        if (getKitByName(newKit.getName()) != null) {
            System.out.print(String.format(cm.getKitAlreadyExists(), newKit.getName()));
            return;
        }
        kits.add(newKit);
        if (newKit.isFree())
            defaultKits.add(newKit);
        List<String> kitNames = new ArrayList<String>();
        for (Kit kit : kits)
            kitNames.add(ChatColor.stripColor(kit.getName()));
        Collections.sort(kitNames, String.CASE_INSENSITIVE_ORDER);
        ArrayList<Kit> newKits = new ArrayList<Kit>();
        for (int i = 0; i < kitNames.size(); i++) {
            Kit kit = getKitByName(kitNames.get(i));
            kit.setId(i);
            newKits.add(kit);
        }
        kits = newKits;
    }

    public boolean addKitToPlayer(Player player, Kit kit) {
        if (!HungergamesApi.getConfigManager().getMainConfig().isMysqlEnabled())
            return false;
        if (!hisKits.containsKey(player.getName()))
            hisKits.put(player.getName(), new ArrayList<Kit>());
        if (!hisKits.get(player.getName()).contains(kit))
            hisKits.get(player.getName()).add(kit);
        return true;
    }

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

    public Kit getKitByName(String name) {
        name = ChatColor.stripColor(name);
        for (Kit kit : kits)
            if (ChatColor.stripColor(kit.getName()).equalsIgnoreCase(name))
                return kit;
            else if (hg.isNumeric(name) && Integer.parseInt(name) == kit.getId())
                return kit;
        return null;
    }

    public Kit getKitByPlayer(Player player) {
        for (Kit kit : kits)
            if (kit.getPlayers().contains(player))
                return kit;
        return null;
    }

    public ArrayList<Kit> getKits() {
        return kits;
    }

    public List<Kit> getPlayersKits(Player player) {
        return hisKits.get(player.getName());
    }

    public boolean ownsKit(Player player, Kit kit) {
        if (defaultKits.contains(kit))
            return true;
        if (player.hasPermission(kit.getPermission()))
            return true;
        if (player.hasPermission("hungergames.kit.*"))
            return true;
        return hisKits.containsKey(player.getName()) && hisKits.get(player.getName()).contains(kit);
    }

    public ItemStack[] parseItem(String string) {
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
                    System.out.print(String.format(cm.getUnrecognisedItemId(), args[0]));
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
                    if (argString.contains("Lore=")) {
                        String name = ChatColor.translateAlternateColorCodes('&', argString.substring(5)).replaceAll("_", " ");
                        ItemMeta meta = item.getItemMeta();
                        List<String> lore = meta.getLore();
                        if (lore == null)
                            lore = new ArrayList<String>();
                        for (String a : name.split("\\n"))
                            lore.add(a);
                        meta.setLore(lore);
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
                item = EnchantmentManager.updateEnchants(item);
                amount = amount - 64;
                items[i] = item;
            }
            return items;
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (ex instanceof ArrayIndexOutOfBoundsException)
                message = "java.lang.ArrayIndexOutOfBoundsException: " + message;
            System.out.print(String.format(cm.getErrorWhileParsingItemStack(), string, message));
            ex.printStackTrace();
        }
        return new ItemStack[] { null };
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

    public boolean removeKitFromPlayer(Player player, Kit kit) {
        if (!hisKits.containsKey(player.getName()))
            return false;
        return hisKits.get(player.getName()).remove(kit);
    }

    public boolean setKit(Player p, String name) {
        Kit kit = getKitByName(name);
        if (kit == null)
            return false;
        Kit kita = getKitByPlayer(p);
        if (kita != null)
            kita.removePlayer(p);
        kit.addPlayer(p);
        return true;
    }
}
