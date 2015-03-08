package me.libraryaddict.Hungergames.Abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Managers.EnchantmentManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Linkage extends AbilityListener implements Disableable {
    private class Teleport {
        private List<Block> blocks = new ArrayList<Block>();

        Player owner;

        Teleport(Player owner) {
            this.owner = owner;
        }

        public void addBlock(Block block) {
            blocks.add(block);
        }

        public Player getOwner() {
            return owner;
        }

        public int getSize() {
            return blocks.size();
        }

        public boolean hasBlock(Block block) {
            return blocks.contains(block);
        }

        public void removeBlock(Block block) {
            blocks.remove(block);
        }

        public void removeTeleports() {
            for (Block b : blocks)
                b.setType(Material.AIR);
            blocks.clear();
        }

        public void teleport(Player p, Block clicked) {
            p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 9);
            p.getWorld().playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.2F, 1);
            for (Block b : blocks)
                if (!b.equals(clicked))
                    p.teleport(b.getLocation().clone().add(0.5, 0.5, 0.5));
            p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 9);
            p.getWorld().playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.2F, 1);
        }
    }

    public String brokeTeleporter = ChatColor.GREEN + "You have broken %s's teleporter!";;
    public String hisTeleporterBroken = ChatColor.GREEN + "%s has broken your teleporter!";
    private PlayerManager pm = HungergamesApi.getPlayerManager();
    public String teleporterExploded = ChatColor.GREEN + "Your teleporter was exploded!";
    public String teleporterLinkEstablished = ChatColor.BLUE + "Teleportation link established";
    public String teleporterName = ChatColor.WHITE + "Teleporter";
    public String teleporterNoOtherSide = ChatColor.GREEN + "This teleporter has no other side!";
    public String teleporterPlaceAnother = ChatColor.BLUE + "Teleporter placed, place enough to establish a link";
    private HashMap<Player, Teleport> teleporters = new HashMap<Player, Teleport>();
    public String teleporterTooManyPlaced = ChatColor.BLUE + "Too many teleporters placed.";

    private void addItem(Player p) {
        ItemStack item = new ItemStack(Material.MOB_SPAWNER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(teleporterName);
        item.setItemMeta(meta);
        item.addEnchantment(EnchantmentManager.UNLOOTABLE, 1);
        EnchantmentManager.updateEnchants(item);
        p.getInventory().addItem(item);
    }

    private Teleport getTeleport(Block block) {
        for (Teleport port : teleporters.values())
            if (port.hasBlock(block))
                return port;
        return null;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDestroy(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        if (event.getBlock().getType() == Material.MOB_SPAWNER) {
            Teleport teleport = this.getTeleport(event.getBlock());
            if (teleport == null)
                return;
            event.setExpToDrop(0);
            Player p = teleport.getOwner();
            teleport.removeBlock(event.getBlock());
            if (event.getPlayer() != p) {
                p.sendMessage(String.format(hisTeleporterBroken, event.getPlayer().getName()));
                event.getPlayer().sendMessage(String.format(brokeTeleporter, p.getName()));
            }
            addItem(p);
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.isCancelled())
            return;
        Iterator<Block> itel = event.blockList().iterator();
        while (itel.hasNext()) {
            Block b = itel.next();
            Teleport teleport = getTeleport(b);
            if (teleport != null) {
                itel.remove();
                Player p = teleport.getOwner();
                p.sendMessage(teleporterExploded);
                addItem(p);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clicked = event.getClickedBlock();
            if (clicked.getType() == Material.MOB_SPAWNER) {
                Player p = event.getPlayer();
                if (pm.getGamer(p).isAlive()) {
                    Teleport teleport = getTeleport(clicked);
                    if (teleport != null) {
                        if (teleport.getSize() >= 2)
                            teleport.teleport(p, clicked);
                        else
                            p.sendMessage(teleporterNoOtherSide);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Block block = event.getBlock();
        if (pm.getGamer(p).isAlive() && hasAbility(p)) {
            if (block.getType() == Material.MOB_SPAWNER) {
                Teleport teleport = teleporters.get(p);
                if (teleport == null) {
                    teleport = new Teleport(p);
                    teleporters.put(p, teleport);
                }
                if (teleport.getSize() >= 2) {
                    p.sendMessage(teleporterTooManyPlaced);
                    event.setCancelled(true);
                    return;
                }
                CreatureSpawner creature = (CreatureSpawner) block.getState();
                creature.setDelay(Integer.MAX_VALUE);
                teleport.addBlock(block);
                if (teleport.getSize() >= 2) {
                    p.sendMessage(teleporterLinkEstablished);
                } else
                    p.sendMessage(teleporterPlaceAnother);
            }
        }
    }

    @EventHandler
    public void onPlayerKilled(PlayerKilledEvent event) {
        if (teleporters.containsKey(event.getKilled().getPlayer())) {
            Teleport tele = teleporters.remove(event.getKilled().getPlayer());
            tele.removeTeleports();
        }
    }

}
