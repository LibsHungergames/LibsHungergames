package me.libraryaddict.Hungergames.Abilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import lombok.*;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class Zombifier extends AbilityListener implements Disableable {
    public Zombifier() throws Exception {
        if (Bukkit.getPluginManager().getPlugin("LibsDisguises") == null)
            throw new Exception(String.format(HungergamesApi.getConfigManager().getLoggerConfig().getDependencyNotFound(),
                    "Plugin LibsDisguises"));
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null)
            throw new Exception(String.format(HungergamesApi.getConfigManager().getLoggerConfig().getDependencyNotFound(),
                    "Plugin ProtocolLib"));
    }

    private static final MethodHandle OLD_SET_ITEM_STACK_METHOD;

    static {
        MethodHandle oldSetItemStackMethod = null;
        try {
            oldSetItemStackMethod = MethodHandles.publicLookup().findVirtual(FlagWatcher.class, "setItemStack", MethodType.methodType(void.class, int.class, ItemStack.class));
        } catch (NoSuchMethodException | IllegalAccessException ignored) {
            // We must be on a newer version :D
        }
        OLD_SET_ITEM_STACK_METHOD = oldSetItemStackMethod;
    }

    @EventHandler
    @SneakyThrows // MethodHandle throws a checked exception -_-
    public void onSneak(PlayerToggleSneakEvent event) {
        if (hasAbility(event.getPlayer())) {
            if (event.isSneaking()) {
                Disguise disguise = new MobDisguise(DisguiseType.ZOMBIE);
                DisguiseAPI.disguiseToAll(event.getPlayer(), disguise);
                FlagWatcher watcher = disguise.getWatcher();
                watcher.setSneaking(false);
                if (OLD_SET_ITEM_STACK_METHOD == null) {
                    // Yay, we're on a modern version!
                    watcher.setItemStack(EquipmentSlot.FEET, new ItemStack(Material.AIR));
                    watcher.setItemStack(EquipmentSlot.LEGS, new ItemStack(Material.AIR));
                    watcher.setItemStack(EquipmentSlot.CHEST, new ItemStack(Material.AIR));
                    watcher.setItemStack(EquipmentSlot.HEAD, new ItemStack(Material.AIR));
                } else {
                    // Old code for pre 1.9
                    for (int i = 0; i < 4; i++) {
                        OLD_SET_ITEM_STACK_METHOD.invokeExact(i, new ItemStack(Material.AIR));
                    }
                }
            } else {
                DisguiseAPI.undisguiseToAll(event.getPlayer());
            }
        }
    }
}
