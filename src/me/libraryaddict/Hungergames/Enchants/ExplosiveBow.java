package me.libraryaddict.Hungergames.Enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class ExplosiveBow extends Enchantment {

  public ExplosiveBow(int id) {
    super(id);
  }

  @Override
  public boolean canEnchantItem(ItemStack item) {
    return item.getType() == Material.BOW;
  }

  @Override
  public boolean conflictsWith(Enchantment other) {
    return false;
  }

  @Override
  public EnchantmentTarget getItemTarget() {
    return EnchantmentTarget.BOW;
  }

  @Override
  public int getMaxLevel() {
    return 10;
  }

  @Override
  public String getName() {
    return "Explosive";
  }

  @Override
  public int getStartLevel() {
    return 1;
  }
}
