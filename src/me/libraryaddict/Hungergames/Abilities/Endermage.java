package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Managers.EnchantmentManager;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class Endermage extends AbilityListener {
    public int invincibleTicks = 100;
    public boolean doInstantKO = true;
    public int instanceKOExpires = 5;
    public String endermagePortalName = ChatColor.WHITE + "Endermage Portal";
    public int endermagePortalId = Material.ENDER_PORTAL.getId();
    public int endermagePortalBlockId = Material.ENDER_PORTAL.getId();

    @EventHandler
    public void onPlace(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && item != null && item.getTypeId() == endermagePortalId
                && isSpecialItem(item, endermagePortalName)) {
            event.setCancelled(true);
            final Block b = event.getClickedBlock();
            if (b.getTypeId() == endermagePortalBlockId)
                return;
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() == 0)
                event.getPlayer().setItemInHand(new ItemStack(0));
            final Location portal = b.getLocation().clone().add(0.5, 0.5, 0.5);
            final Material material = b.getType();
            final byte dataValue = b.getData();
            portal.getBlock().setTypeId(endermagePortalBlockId);
            final Gamer mager = HungergamesApi.getPlayerManager().getGamer(event.getPlayer());
            for (int i = 0; i <= 5; i++) {
                final int no = i;
                Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                    public void run() {
                        for (Gamer gamer : HungergamesApi.getPlayerManager().getAliveGamers()) {
                            Player p = gamer.getPlayer();
                            if (p != mager.getPlayer() && isEnderable(portal, p.getLocation())) {
                                if (doInstantKO)
                                    p.setMetadata("InstantKill" + mager.getName(),
                                            new FixedMetadataValue(HungergamesApi.getHungergames(), System.currentTimeMillis()
                                                    + (instanceKOExpires * 1000)));
                                if (gamer.isAlive()) {
                                    if (p.getLocation().distance(portal) > 4) {
                                        p.playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 9);
                                        p.playEffect(portal, Effect.ENDER_SIGNAL, 9);
                                        p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1.2F);
                                        p.playSound(portal, Sound.ENDERMAN_TELEPORT, 1, 1.2F);
                                    }
                                    if (invincibleTicks > 0)
                                        p.setNoDamageTicks(invincibleTicks);
                                    p.teleport(portal);
                                }
                            }
                        }
                        if (no == 5) {
                            portal.getBlock().setTypeIdAndData(material.getId(), dataValue, true);
                            if (mager.isAlive()) {
                                ItemStack item = new ItemStack(endermagePortalId);
                                ItemMeta meta = item.getItemMeta();
                                meta.setDisplayName(endermagePortalName);
                                item.setItemMeta(meta);
                                item.addEnchantment(EnchantmentManager.UNLOOTABLE, 1);
                                EnchantmentManager.updateEnchants(item);
                                HungergamesApi.getKitManager().addItem(mager.getPlayer(), item);
                            }
                        }
                    }
                }, i * 20);
            }
        }
    }

    private boolean isEnderable(Location portal, Location player) {
        return Math.abs(portal.getX() - player.getX()) < 2 && Math.abs(portal.getZ() - player.getZ()) < 2
                && Math.abs(portal.getY() - player.getY()) > 4;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (doInstantKO)
            if (event.getEntity() instanceof Player
                    && event.getDamager().hasMetadata("InstantKill" + ((Player) event.getEntity()).getName())
                    && event.getDamager().getMetadata("InstantKill" + ((Player) event.getEntity()).getName()).get(0).asLong() < System
                            .currentTimeMillis())
                event.setDamage(9999);
    }

}
