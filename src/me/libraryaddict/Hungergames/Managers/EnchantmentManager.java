package me.libraryaddict.Hungergames.Managers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.libraryaddict.Hungergames.Enchants.Unlootable;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentManager {

    private static final int[] BVAL = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
    private static List<Integer> customEnchants = new ArrayList<Integer>();
    private static HashMap<Enchantment, String> enchantNames = new HashMap<Enchantment, String>();

    // Parallel arrays used in the conversion process.
    private static final String[] RCODE = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

    public static Enchantment UNLOOTABLE;

    static {
        ChatManager cm = HungergamesApi.getChatManager();
        enchantNames.put(Enchantment.DAMAGE_ALL, cm.getEnchantNameSharpness());
        enchantNames.put(Enchantment.ARROW_FIRE, cm.getEnchantNameArrowFire());
        enchantNames.put(Enchantment.ARROW_INFINITE, cm.getEnchantNameArrowInfinite());
        enchantNames.put(Enchantment.ARROW_DAMAGE, cm.getEnchantNameArrowDamage());
        enchantNames.put(Enchantment.ARROW_KNOCKBACK, cm.getEnchantNameArrowKnockback());
        enchantNames.put(Enchantment.DAMAGE_ARTHROPODS, cm.getEnchantNameDamageSpiders());
        enchantNames.put(Enchantment.DAMAGE_UNDEAD, cm.getEnchantNameDamageUndead());
        enchantNames.put(Enchantment.LOOT_BONUS_MOBS, cm.getEnchantNameLootMobs());
        enchantNames.put(Enchantment.LOOT_BONUS_BLOCKS, cm.getEnchantNameLootBlocks());
        enchantNames.put(Enchantment.WATER_WORKER, cm.getEnchantNameAquaAffinity());
        enchantNames.put(Enchantment.OXYGEN, cm.getEnchantNameRespiration());
        enchantNames.put(Enchantment.DIG_SPEED, cm.getEnchantNameDigSpeed());
        enchantNames.put(Enchantment.DURABILITY, cm.getEnchantNameDurability());
        enchantNames.put(Enchantment.PROTECTION_ENVIRONMENTAL, cm.getEnchantNameProtection());
        enchantNames.put(Enchantment.PROTECTION_FALL, cm.getEnchantNameProtectionFall());
        enchantNames.put(Enchantment.PROTECTION_EXPLOSIONS, cm.getEnchantNameProtectionBlast());
        enchantNames.put(Enchantment.PROTECTION_PROJECTILE, cm.getEnchantNameProtectionProjectiles());
        enchantNames.put(Enchantment.PROTECTION_FIRE, cm.getEnchantNameProtectionFire());
        enchantNames.put(Enchantment.SILK_TOUCH, cm.getEnchantNameSilkTouch());
        enchantNames.put(Enchantment.THORNS, cm.getEnchantNameSilkTouch());
        UNLOOTABLE = new Unlootable(getId());
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

    public static String getReadableName(Enchantment enchant) {
        if (enchantNames.containsKey(enchant))
            return enchantNames.get(enchant);
        return enchant.getName();
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
}
