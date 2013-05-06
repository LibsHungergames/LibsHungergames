package me.libraryaddict.Hungergames.Abilities;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Gambler extends AbilityListener {
    public int chanceHeal = 100;
    public int chanceHunger = 100;
    public String foodName = "&2Full hunger";
    public String healthName = "&2Full health";
    private transient KitManager kits = HungergamesApi.getKitManager();
    public String[] potionEffects = new String[] { "1000 &cInstant_Death HARM 0 1000", "100 &cSlowness_Effect SLOW 1200 1",
            "100 &2Speed_Effect SPEED 1200 1", "100 &cPoison_Effect POISON 200 0", "100 &cHunger_Effect HUNGER 1200 1",
            "100 &2Strength_Boost INCREASE_DAMAGE 1200 1", "100 &cWeakness_Effect WEAKNESS 1200 1",
            "100 &2Regeneration_Effect REGENERATION 1200 0" };
    public boolean potionEffectsDurationsStack = true;
    public String[] randomItems = new String[] { "500 &2Diamond_Helmet DIAMOND_HELMET 0 1",
            "500 &2Diamond_Chestplate DIAMOND_CHESTPLATE 0 1", "500 &2Diamond_Leggings DIAMOND_LEGGINGS 0 1",
            "500 &2Diamond_Boots DIAMOND_BOOTS" };
    public String whatYouWon = ChatColor.BLUE + "You won: " + ChatColor.AQUA + "%s";

    private boolean hasChance(String string) {
        String[] split = string.split(" ");
        return (split.length == 5 && new Random().nextInt(Integer.parseInt(split[0])) == 0);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && hasAbility(event.getPlayer())
                && event.getClickedBlock().getType() == Material.STONE_BUTTON && event.getClickedBlock().getData() <= (byte) 4
                && event.getClickedBlock().getData() > (byte) 0) {
            Player p = event.getPlayer();
            while (true) {
                int chance = new Random().nextInt(randomItems.length + potionEffects.length + 2);
                if (chance < randomItems.length) {
                    if (hasChance(randomItems[chance])) {
                        String[] string = randomItems[chance].split(" ");
                        for (ItemStack item : kits.parseItem(randomItems[chance].substring(string[0].length()
                                + string[1].length() + 2)))
                            kits.addItem(p, item);
                        p.updateInventory();
                        p.sendMessage(String.format(whatYouWon,
                                ChatColor.translateAlternateColorCodes('&', string[1].replace("_", " "))));
                        break;
                    }
                } else if (chance - randomItems.length < potionEffects.length) {
                    int effect = chance - randomItems.length;
                    if (hasChance(potionEffects[effect])) {
                        String[] string = potionEffects[effect].split(" ");
                        PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(string[2].toUpperCase()),
                                Integer.parseInt(string[3]), Integer.parseInt(string[4]));
                        if (potionEffectsDurationsStack)
                            for (PotionEffect e : p.getActivePotionEffects()) {
                                if (e.getType() == potionEffect.getType()) {
                                    potionEffect = new PotionEffect(e.getType(), e.getDuration() + potionEffect.getDuration(),
                                            potionEffect.getAmplifier());
                                }
                            }
                        p.addPotionEffect(potionEffect, true);
                        p.sendMessage(String.format(whatYouWon,
                                ChatColor.translateAlternateColorCodes('&', string[1].replace("_", " "))));
                        break;
                    }
                } else {
                    if (new Random().nextBoolean()) {
                        if (new Random().nextInt(chanceHeal) == 0) {
                            p.setHealth(20);
                            p.sendMessage(String.format(whatYouWon, healthName));
                            break;
                        }
                    } else {
                        if (new Random().nextInt(chanceHunger) == 0) {
                            p.setFoodLevel(20);
                            p.setSaturation(5.0F);
                            p.setExhaustion(0F);
                            p.sendMessage(String.format(whatYouWon, foodName));
                            break;
                        }
                    }
                }
            }
        }
    }
}
