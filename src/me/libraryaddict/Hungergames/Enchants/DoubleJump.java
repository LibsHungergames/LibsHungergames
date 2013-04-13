package me.libraryaddict.Hungergames.Enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class DoubleJump extends Enchantment {

  public DoubleJump(int id) {
    super(id);
  }

  @Override
  public boolean canEnchantItem(ItemStack item) {
    return item.getType().name().contains("BOOT");
  }

  @Override
  public boolean conflictsWith(Enchantment other) {
    return false;
  }

  @Override
  public EnchantmentTarget getItemTarget() {
    return EnchantmentTarget.ARMOR_FEET;
  }

  @Override
  public int getMaxLevel() {
    return 10;
  }

  @Override
  public String getName() {
    return "Double Jump";
  }

  @Override
  public int getStartLevel() {
    return 1;
  }
}
