package me.libraryaddict.Hungergames.Abilities;

import java.util.HashMap;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Milkman extends AbilityListener {
    private transient HashMap<ItemStack, Integer> cooldown = new HashMap<ItemStack, Integer>();
    private transient KitManager kits = HungergamesApi.getKitManager();
    public int maxUses = 5;
    public String milkbucketName = "Milkman's Bucket";
    public String milkBucketRanOut = ChatColor.BLUE + "Your milk bucket has run out of uses!";
    public String milkmanMessage = "I am the milkman, my milk is delicious";
    public String[] potionEffects = new String[] { "REGENERATION 900 0", "FIRE_RESISTANCE 900 0", "SPEED 900 0" };
    public String usedMilk = ChatColor.GREEN + "You have %s uses left!";

    private ItemStack clone(ItemStack item, Material newMaterial) {
        ItemStack newItem = new ItemStack(newMaterial.getId(), item.getAmount(), item.getDurability());
        newItem.setItemMeta(item.getItemMeta());
        return newItem;
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player p = event.getPlayer();
        if (hasAbility(p) && isSpecialItem(item, milkbucketName)) {
            p.chat(milkmanMessage);
            for (String string : potionEffects) {
                String[] effect = string.split(" ");
                PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(effect[0].toUpperCase()),
                        Integer.parseInt(effect[1]), Integer.parseInt(effect[2]));
                p.addPotionEffect(potionEffect, true);
            }
            if (!cooldown.containsKey(item))
                cooldown.put(item, 0);
            cooldown.put(item, cooldown.get(item) + 1);
            if (cooldown.get(item) == maxUses) {
                p.sendMessage(milkBucketRanOut);
                cooldown.remove(item);
                event.setCancelled(true);
                p.setItemInHand(new ItemStack(Material.BUCKET, item.getAmount(), item.getDurability()));
            } else {
                p.sendMessage(String.format(usedMilk, maxUses - cooldown.get(item)));
                event.setCancelled(true);
                p.setItemInHand(clone(item, Material.BUCKET));
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getItemInHand();
        if (isSpecialItem(item, milkbucketName) && item.getType() == Material.BUCKET) {
            if (event.getRightClicked() instanceof Cow) {
                event.setCancelled(true);
                ItemStack cloned = item.clone();
                item.setAmount(item.getAmount() - 1);
                if (item.getAmount() == 0)
                    event.getPlayer().setItemInHand(new ItemStack(0));
                kits.addItem(event.getPlayer(), clone(cloned, Material.MILK_BUCKET));
                event.getPlayer().updateInventory();
            }
        }
    }
}
