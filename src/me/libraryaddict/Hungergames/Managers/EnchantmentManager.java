package me.libraryaddict.Hungergames.Managers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.libraryaddict.Hungergames.Enchants.*;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentManager {

    private static final int[] BVAL = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
    private static List<Integer> customEnchants = new ArrayList<Integer>();

    // Parallel arrays used in the conversion process.
    private static final String[] RCODE = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

    public static Enchantment UNDROPPABLE;
    public static Enchantment UNLOOTABLE;

    static {
        UNLOOTABLE = new Unlootable(getId());
        UNDROPPABLE = new Undroppable(getId());
        try {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.setBoolean(EnchantmentManager.UNLOOTABLE, true);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (Enchantment.getById(UNLOOTABLE.getId()) == null) {
            Enchantment.registerEnchantment(UNLOOTABLE);
            customEnchants.add(UNLOOTABLE.getId());
            Enchantment.registerEnchantment(UNDROPPABLE);
            customEnchants.add(UNDROPPABLE.getId());
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

    public static boolean isNatural(Enchantment ench) {
        if (customEnchants.contains(ench.getId()))
            return false;
        return true;
    }

    // =========================================================== binaryToRoman
    private static String toRoman(int binary) {
        if (binary <= 0) {
            return "";
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

    public static ItemStack updateEnchants(ItemStack item) {
        ArrayList<String> enchants = new ArrayList<String>();
        NameManager nm = HungergamesApi.getNameManager();
        for (Enchantment ench : item.getEnchantments().keySet()) {
            if (!isNatural(ench)) {
                String enchantName = nm.getEnchantName(ench);
                enchants.add(ChatColor.GRAY + enchantName + " " + toRoman(item.getEnchantments().get(ench)));
            }
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.hasLore())
            for (String lore : meta.getLore())
                enchants.add(lore);
        meta.setLore(enchants);
        item.setItemMeta(meta);
        return item;
    }
}
