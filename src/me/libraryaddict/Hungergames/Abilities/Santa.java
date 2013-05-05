package me.libraryaddict.Hungergames.Abilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Kit;

public class Santa extends AbilityListener {
    private transient int timeSinceLastPresent = 0;
    private Hungergames hg = HungergamesApi.getHungergames();
    private KitManager kits = HungergamesApi.getKitManager();
    public String[] dontGivePresentFor = new String[] { "Santa", "Barbarian", "Crafter", "Doctor", "Endermage", "Flash", "Monk",
            "Pickpocket", "Pyro", "Reaper", "Seeker" };
    public double chanceOutOfAHundredForNaughtyKitToBeViable = 70;
    public String[] naughtyList = new String[] { "Urgal", "Chemist", "Digger", "Grandpa", "Jumper" };
    public int presentEndingDataValue = 15;
    public int presentID = Material.WOOL.getId();
    public String[] presentLore = new String[] {
            ChatColor.DARK_PURPLE + "I wish you " + ChatColor.GREEN + "joy " + ChatColor.WHITE + "all though your holidays.",
            ChatColor.DARK_PURPLE + "I wish you " + ChatColor.GREEN + "good luck " + ChatColor.WHITE + "that "
                    + ChatColor.UNDERLINE + "forever " + ChatColor.RESET + ChatColor.WHITE + "stays.",
            ChatColor.DARK_PURPLE + "I wish you " + ChatColor.GREEN + "the love of family and friends.",
            ChatColor.DARK_PURPLE + "I wish you " + ChatColor.GREEN + "happy days the never ends",
            ChatColor.GOLD + "Merry holidays and a happy new years!", ChatColor.GOLD + "" + ChatColor.BOLD + "- You smell" };
    public String presentName = ChatColor.WHITE + "" + ChatColor.ITALIC + "Present for kit - %s";
    public int presentStartingDataValue = 0;
    public boolean preventPlacing = true;
    public boolean specialPresent = true;
    public int timeBetweenPresents = 60;
    public boolean displayMessageOnOpen = true;
    public String messageOnPresentOpen = ChatColor.DARK_RED + "M" + ChatColor.WHITE + "e" + ChatColor.DARK_RED + "r"
            + ChatColor.WHITE + "r" + ChatColor.DARK_RED + "y" + ChatColor.WHITE + " C" + ChatColor.DARK_RED + "h"
            + ChatColor.WHITE + "r" + ChatColor.DARK_RED + "i" + ChatColor.WHITE + "s" + ChatColor.DARK_RED + "t"
            + ChatColor.WHITE + "m" + ChatColor.DARK_RED + "a" + ChatColor.WHITE + "s" + ChatColor.DARK_RED + "!";

    private Kit findViableKit() {
        ArrayList<Kit> randomKits = new ArrayList<Kit>();
        for (Kit kit : kits.getKits()) {
            if (hasChance(kit)) {
                randomKits.add(kit);
            }
        }
        if (randomKits.size() == 0)
            return null;
        Collections.shuffle(randomKits, new Random());
        return randomKits.get(0);
    }

    public boolean hasChance(Kit kit) {
        for (String kitName : dontGivePresentFor) {
            if (kit.getName().equalsIgnoreCase(kitName))
                return false;
        }
        for (String kitName : naughtyList) {
            if (kit.getName().equalsIgnoreCase(kitName)
                    && (new Random().nextInt(10000) < chanceOutOfAHundredForNaughtyKitToBeViable * 100))
                return false;
        }
        if (kit.getItems().length == 0)
            return false;
        return true;
    }

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (preventPlacing && item != null && item.getTypeId() == presentID && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()) {
            for (Kit kit : kits.getKits())
                if (String.format(presentName, kit.getName()).equals(item.getItemMeta().getDisplayName())) {
                    event.setCancelled(true);
                    event.getPlayer().updateInventory();
                    break;
                }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = p.getItemInHand();
        Kit kit = kits.getKitByPlayer(p);
        if (kit != null && item != null && event.getAction().name().contains("RIGHT")
                && isSpecialItem(item, String.format(presentName, kit.getName()))) {
            event.setCancelled(true);
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() == 0)
                p.setItemInHand(new ItemStack(0));
            if (displayMessageOnOpen)
                p.sendMessage(messageOnPresentOpen);
            for (ItemStack i : kit.getItems()) {
                kits.addItem(p, kit.prepareToGive(i));
            }
            p.updateInventory();
        }
    }

    @EventHandler
    public void timeSecondEvent(TimeSecondEvent event) {
        if (hg.currentTime >= 0)
            timeSinceLastPresent++;
        if (timeSinceLastPresent == timeBetweenPresents) {
            timeSinceLastPresent = 0;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (hasAbility(p)) {
                    Kit kit = findViableKit();
                    if (kit != null) {
                        ItemStack item = new ItemStack(
                                this.presentID,
                                1,
                                (short) (new Random().nextInt((presentEndingDataValue - presentStartingDataValue) + 1) + presentStartingDataValue));
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(String.format(presentName, kit.getName()));
                        if (presentLore.length > 0 && (presentLore.length > 1 || presentLore[0].length() > 0))
                            meta.setLore(Arrays.asList(presentLore));
                        item.setItemMeta(meta);
                        kits.addItem(p, item);
                    }
                }
            }
        }
    }
}
