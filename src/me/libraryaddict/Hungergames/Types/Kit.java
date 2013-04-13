package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Kit extends Extender {

    private String kitName;
    private ItemStack[] armor;
    private ItemStack[] items;
    private List<String> players = new ArrayList<String>();
    private String permission;
    private String description = "No description was provided for this kit";
    private String[] abilities;
    private boolean isFree = false;
    private int price = -1;
    private int id;
    static private int cId = 0;

    public Kit(String name, ItemStack[] armour, ItemStack[] item, String perm, String desc, String[] abilitys) {
        id = cId;
        cId++;
        kitName = name;
        armor = armour;
        items = item;
        permission = perm;
        if (desc != null)
            description = desc;
        abilities = abilitys;
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

    public List<String> getPlayers() {
        return players;
    }

    public String getPermission() {
        return permission;
    }

    public void addPlayer(String player) {
        if (!players.contains(player))
            players.add(player);
    }

    public void removePlayer(String player) {
        players.remove(player);
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
        for (final String player : players) {
            Player p = Bukkit.getPlayerExact(player);
            if (p == null)
                continue;
            time += 0.1;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
                public void run() {
                    Player p = Bukkit.getPlayerExact(player);
                    if (p == null)
                        return;
                    giveKit(p);
                }
            }, Math.round(Math.floor(time)));
        }
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
            inv.addItem(item.clone());
        }

    }
}
