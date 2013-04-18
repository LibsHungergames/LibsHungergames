package me.libraryaddict.Hungergames.Kits;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.Enchants;
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
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class Endermage implements Listener {

    private PlayerManager pm = HungergamesApi.getPlayerManager();
    private KitManager kits = HungergamesApi.getKitManager();
    private Hungergames hg = HungergamesApi.getHungergames();

    @EventHandler
    public void onPlace(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && item != null && item.getType() == Material.ENDER_PORTAL
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Endermage Portal")) {
            event.setCancelled(true);
            final Block b = event.getClickedBlock();
            if (b.getType() == Material.ENDER_PORTAL)
                return;
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() == 0)
                event.getPlayer().setItemInHand(new ItemStack(0));
            final Location portal = b.getLocation().clone().add(0.5, 0.5, 0.5);
            final Material material = b.getType();
            final byte dataValue = b.getData();
            portal.getBlock().setType(Material.ENDER_PORTAL);
            final Gamer mager = pm.getGamer(event.getPlayer());
            for (int i = 0; i <= 5; i++) {
                final int no = i;
                Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
                    public void run() {
                        for (Gamer gamer : pm.getAliveGamers()) {
                            Player p = gamer.getPlayer();
                            if (p != mager.getPlayer() && isEnderable(portal, p.getLocation())) {
                                p.setMetadata("InstantKill" + mager.getName(),
                                        new FixedMetadataValue(hg, System.currentTimeMillis() + 5000));
                                if (gamer.isAlive()) {
                                    if (p.getLocation().distance(portal) > 4) {
                                        p.playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 9);
                                        p.playEffect(portal, Effect.ENDER_SIGNAL, 9);
                                        p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1.2F);
                                        p.playSound(portal, Sound.ENDERMAN_TELEPORT, 1, 1.2F);
                                    }
                                    p.teleport(portal);
                                }
                            }
                        }
                        if (no == 5) {
                            portal.getBlock().setTypeIdAndData(material.getId(), dataValue, true);
                            if (mager.isAlive()) {
                                ItemStack item = new ItemStack(Material.ENDER_PORTAL);
                                ItemMeta meta = item.getItemMeta();
                                meta.setDisplayName(ChatColor.WHITE + "Endermage Portal");
                                item.setItemMeta(meta);
                                item.addEnchantment(Enchants.UNLOOTABLE, 1);
                                Enchants.updateEnchants(item);
                                kits.addItem(mager.getPlayer(), item);
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
        if (event.getEntity() instanceof Player
                && event.getDamager().hasMetadata("InstantKill" + ((Player) event.getEntity()).getName())
                && event.getDamager().getMetadata("InstantKill" + ((Player) event.getEntity()).getName()).get(0).asLong() < System
                        .currentTimeMillis())
            event.setDamage(9999);
    }

}
