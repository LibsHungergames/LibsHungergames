package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.List;

import me.libraryaddict.Hungergames.Hungergames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit {

    private String kitName;
    private ItemStack icon;
    private ItemStack[] armor;
    private ItemStack[] items;
    private List<Player> players = new ArrayList<Player>();
    private String permission;
    private String description = HungergamesApi.getChatManager().getKitDescriptionDefault();
    private String[] abilities;
    private boolean isFree = false;
    private int price = -1;
    private int id;
    private static int identifier = 0;
    private static int cId = 0;

    public Kit(String name, ItemStack icon, ItemStack[] armour, ItemStack[] item, String desc, String[] abilitys) {
        id = cId;
        this.icon = icon;
        cId++;
        kitName = name;
        armor = armour;
        items = item;
        permission = "hungergames.kit." + name.replaceAll(" ", "_").toLowerCase();
        if (desc != null)
            description = desc;
        abilities = abilitys;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int newId) {
        id = newId;
    }

    public String getDescription() {
        return description;
    }

    public int getPlayerSize() {
        return players.size();
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int p) {
        price = p;
    }

    public boolean hasAbility(String string) {
        for (String ability : abilities)
            if (string.equalsIgnoreCase(ability))
                return true;
        return false;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public boolean isFree() {
        return isFree;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getPermission() {
        return permission;
    }

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            for (String abilityName : abilities) {
                HungergamesApi.getAbilityManager().registerPlayerAbility(player, abilityName);
            }
            players.add(player);
        }
    }

    public void removePlayer(Player p) {
        players.remove(p);
        for (String abilityName : abilities) {
            HungergamesApi.getAbilityManager().unregisterPlayerAbility(p, abilityName);
        }

    }

    public String getName() {
        return kitName;
    }

    public String getSafeName() {
        return ChatColor.stripColor(kitName);
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public void giveKit() {
        double time = 0;
        Hungergames hg = HungergamesApi.getHungergames();
        for (final Player p : players) {
            time += 0.1;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
                public void run() {
                    giveKit(p);
                }
            }, Math.round(Math.floor(time)));
        }
    }

    public ItemStack prepareToGive(ItemStack item) {
        if (item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("UniqueIdentifier")) {
            ItemStack itemStack = item.clone();
            ItemMeta meta = itemStack.getItemMeta();
            String string = meta.getDisplayName().replace("UniqueIdentifier", "");
            for (char c : ("" + identifier++).toCharArray())
                string = string + ChatColor.COLOR_CHAR + c;
            meta.setDisplayName(string);
            itemStack.setItemMeta(meta);
            return itemStack;
        } else
            return item.clone();
    }

    public void giveKit(Player p) {
        PlayerInventory inv = p.getInventory();
        ItemStack[] arm = inv.getArmorContents();
        for (int n = 0; n < 4; n++) {
            if (armor[n] == null || armor[n].getType() == Material.AIR)
                continue;
            if (arm[n] == null || arm[n].getType() == Material.AIR)
                arm[n] = armor[n].clone();
        }
        inv.setArmorContents(arm);
        for (ItemStack item : items) {
            inv.addItem(prepareToGive(item));
        }
    }
}
