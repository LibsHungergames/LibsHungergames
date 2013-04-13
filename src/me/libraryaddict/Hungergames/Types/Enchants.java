package me.libraryaddict.Hungergames.Types;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.libraryaddict.Hungergames.Enchants.DoubleJump;
import me.libraryaddict.Hungergames.Enchants.ExplosiveBow;
import me.libraryaddict.Hungergames.Enchants.Unlootable;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Enchants {

    private static HashMap<Enchantment, String> enchantNames = new HashMap<Enchantment, String>();
    private static List<Integer> customEnchants = new ArrayList<Integer>();
    public static Enchantment EXPLOSIVE;
    public static Enchantment UNLOOTABLE;
    public static Enchantment DOUBLEJUMP;

    public Enchants() {
        enchantNames.put(Enchantment.DAMAGE_ALL, "Sharpness");
        enchantNames.put(Enchantment.ARROW_FIRE, "Flame");
        enchantNames.put(Enchantment.ARROW_INFINITE, "Infinite Arrows");
        enchantNames.put(Enchantment.ARROW_DAMAGE, "Power");
        enchantNames.put(Enchantment.ARROW_KNOCKBACK, "Punch");
        enchantNames.put(Enchantment.DAMAGE_ARTHROPODS, "Bane of Arthropods");
        enchantNames.put(Enchantment.DAMAGE_UNDEAD, "Smite");
        enchantNames.put(Enchantment.LOOT_BONUS_MOBS, "Looting");
        enchantNames.put(Enchantment.LOOT_BONUS_BLOCKS, "Fortune");
        enchantNames.put(Enchantment.WATER_WORKER, "Aqua Affinity");
        enchantNames.put(Enchantment.OXYGEN, "Respiration");
        enchantNames.put(Enchantment.DIG_SPEED, "Efficiency");
        enchantNames.put(Enchantment.DURABILITY, "Unbreaking");
        enchantNames.put(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection");
        enchantNames.put(Enchantment.PROTECTION_FALL, "Feather Falling");
        enchantNames.put(Enchantment.PROTECTION_EXPLOSIONS, "Blast Protection");
        enchantNames.put(Enchantment.PROTECTION_PROJECTILE, "Projectile Protection");
        enchantNames.put(Enchantment.PROTECTION_FIRE, "Fire Protection");
        enchantNames.put(Enchantment.SILK_TOUCH, "Silk Touch");
        enchantNames.put(Enchantment.THORNS, "Thorns");
        EXPLOSIVE = new ExplosiveBow(getId());
        UNLOOTABLE = new Unlootable(getId());
        DOUBLEJUMP = new DoubleJump(getId());
        try {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.setBoolean(Enchants.EXPLOSIVE, true);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (Enchantment.getById(EXPLOSIVE.getId()) == null) {
            Enchantment.registerEnchantment(EXPLOSIVE);
            customEnchants.add(EXPLOSIVE.getId());
            Enchantment.registerEnchantment(DOUBLEJUMP);
            customEnchants.add(DOUBLEJUMP.getId());
            Enchantment.registerEnchantment(UNLOOTABLE);
            customEnchants.add(UNLOOTABLE.getId());
        }
    }

    private static int getId() {
        for (int i = 1; i <= 1000; i++) {
            if (Enchantment.getById(i) == null && !customEnchants.contains(i)) {
                customEnchants.add(i);
                return i;
            }
        }
        return 0;
    }

    public static ItemStack updateEnchants(ItemStack item) {
        ArrayList<String> enchants = new ArrayList<String>();
        for (Enchantment ench : item.getEnchantments().keySet()) {
            if (!isNatural(ench)) {
                if (!ench.getName().contains("%no%"))
                    enchants.add(ChatColor.GRAY + ench.getName() + " " + toRoman(item.getEnchantments().get(ench)));
                else
                    enchants.add(ChatColor.GRAY + ench.getName().replace("%no%", "" + item.getEnchantments().get(ench)));
            }
        }
        ItemMeta meta = item.getItemMeta();
        meta.setLore(enchants);
        item.setItemMeta(meta);
        return item;
    }

    private static boolean isNatural(Enchantment ench) {
        if (customEnchants.contains(ench.getId()))
            return false;
        return true;
    }

    // Parallel arrays used in the conversion process.
    private static final String[] RCODE = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
    private static final int[] BVAL = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };

    // =========================================================== binaryToRoman
    private static String toRoman(int binary) {
        if (binary <= 0 || binary >= 4000) {
            throw new NumberFormatException("Value outside roman numeral range.");
        }
        String roman = "";
        for (int i = 0; i < RCODE.length; i++) {
            while (binary >= BVAL[i]) {
                binary -= BVAL[i];
                roman += RCODE[i];
            }
        }
        return roman;
    }

    public static String getReadableName(Enchantment enchant) {
        if (enchantNames.containsKey(enchant))
            return enchantNames.get(enchant);
        return enchant.getName();
    }
}
