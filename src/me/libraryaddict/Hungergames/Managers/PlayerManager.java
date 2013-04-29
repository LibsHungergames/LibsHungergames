package me.libraryaddict.Hungergames.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.util.Vector;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.Damage;
import me.libraryaddict.Hungergames.Types.Enchants;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

public class PlayerManager {

    private ConcurrentLinkedQueue<Gamer> gamers = new ConcurrentLinkedQueue<Gamer>();
    public ConcurrentHashMap<String, Long> vips = new ConcurrentHashMap<String, Long>();
    public ConcurrentLinkedQueue<Gamer> loadGamer = new ConcurrentLinkedQueue<Gamer>();
    public HashMap<Gamer, Damage> lastDamager = new HashMap<Gamer, Damage>();
    Hungergames hg = HungergamesApi.getHungergames();
    KitManager kits = HungergamesApi.getKitManager();

    public synchronized Gamer getGamer(Entity entity) {
        for (Gamer g : gamers)
            if (g.getPlayer() == entity)
                return g;
        return null;
    }

    public synchronized Gamer getGamer(String name) {
        for (Gamer g : gamers)
            if (g.getName().equals(name))
                return g;
        return null;
    }

    public Gamer registerGamer(Player p) {
        Gamer gamer = new Gamer(p, (p.isOp() || vips.contains(p.getName())));
        gamers.add(gamer);
        gamer.clearInventory();
        return gamer;
    }

    public void unregisterGamer(Gamer gamer) {
        gamers.remove(gamer);
    }

    public Gamer unregisterGamer(Entity entity) {
        Iterator<Gamer> itel = gamers.iterator();
        while (itel.hasNext()) {
            Gamer g = itel.next();
            if (g.getPlayer() == entity) {
                itel.remove();
                return g;
            }
        }
        return null;
    }

    public List<Gamer> getGamers() {
        List<Gamer> game = new ArrayList<Gamer>();
        for (Gamer g : gamers)
            game.add(g);
        return game;
    }

    public int returnChance(int Chance) {
        return new Random().nextInt(Chance);
    }

    public void sendToSpawn(final Player p) {
        p.setFlying(false);
        Location spawn = hg.world.getSpawnLocation().clone();
        int chances = 0;
        if (p.isInsideVehicle())
            p.leaveVehicle();
        p.eject();
        while (true) {
            Location hisSpawn = new Location(spawn.getWorld(), spawn.getX() + (returnChance(10 * 2) - 10), spawn.getY()
                    + new Random().nextInt(10), spawn.getZ() + (returnChance(10 * 2) - 10));
            chances = chances + 1;
            while (hisSpawn.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                if (hisSpawn.getY() > 0)
                    hisSpawn.add(0, -1, 0);
                else
                    continue;
            }
            if (hisSpawn.getBlock().getType() == Material.AIR
                    && hisSpawn.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
                spawn = hisSpawn.clone();
                break;
            }
            if (chances == 300) {
                hisSpawn.setY(p.getWorld().getHighestBlockYAt(hisSpawn));
                spawn = hisSpawn.clone();
                break;
            }
        }
        final Location destination = spawn.add(0.5, 0, 0.5).clone();
        p.teleport(destination);
        Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
            public void run() {
                p.teleport(destination);
            }
        });
    }

    public void killPlayer(Gamer gamer, Entity killer, Location dropLoc, List<ItemStack> drops, String deathMsg) {
        if (!hg.doSeconds || hg.currentTime < 0)
            return;
        Damage dmg = lastDamager.get(gamer);
        Gamer backup = null;
        if (dmg != null)
            if (dmg.getTime() >= System.currentTimeMillis() / 1000)
                backup = dmg.getDamager();
        PlayerKilledEvent event = new PlayerKilledEvent(gamer, killer, backup, deathMsg, dropLoc, drops);
        Bukkit.getPluginManager().callEvent(event);
        manageDeath(event);
    }

    public void manageDeath(PlayerKilledEvent event) {
        Gamer killed = event.getKilled();
        final Player p = killed.getPlayer();
        p.setHealth(20);
        if (event.isCancelled())
            return;
        for (HumanEntity human : p.getInventory().getViewers())
            human.closeInventory();
        if (p.isInsideVehicle())
            p.leaveVehicle();
        p.eject();
        Iterator<Gamer> itel = lastDamager.keySet().iterator();
        while (itel.hasNext()) {
            Gamer g = itel.next();
            if (lastDamager.get(g).getDamager() == killed)
                itel.remove();
        }
        if (event.getDeathMessage().equalsIgnoreCase(ChatColor.stripColor(event.getDeathMessage())))
            event.setDeathMessage(ChatColor.DARK_RED + event.getDeathMessage());
        p.setLevel(0);
        p.setExp(0F);
        event.setDeathMessage(event.getDeathMessage().replace(
                p.getName(),
                ChatColor.RED + p.getName() + ChatColor.DARK_RED + "(" + ChatColor.RED
                        + (kits.getKitByPlayer(p.getName()) == null ? "None" : kits.getKitByPlayer(p.getName()).getName())
                        + ChatColor.DARK_RED + ")"));
        if (event.getKillerPlayer() != null) {
            event.getKillerPlayer().addKill();
            event.setDeathMessage(event.getDeathMessage().replace(
                    event.getKillerPlayer().getName(),
                    ChatColor.RED
                            + event.getKillerPlayer().getName()
                            + ChatColor.DARK_RED
                            + "("
                            + ChatColor.RED
                            + (kits.getKitByPlayer(event.getKillerPlayer().getName()) == null ? "None" : kits.getKitByPlayer(
                                    event.getKillerPlayer().getName()).getName()) + ChatColor.DARK_RED + ")"));
        }
        int reward = hg.getPrize(getAliveGamers().size());
        if (reward > 0)
            killed.addBalance(reward);
        hg.cannon();
        killed.clearInventory();
        World world = p.getWorld();
        for (ItemStack item : event.getDrops()) {
            if (item == null || item.getType() == Material.AIR || item.containsEnchantment(Enchants.UNLOOTABLE))
                continue;
            else if (item.hasItemMeta())
                world.dropItemNaturally(event.getDropsLocation(), item.clone()).getItemStack().setItemMeta(item.getItemMeta());
            else
                world.dropItemNaturally(event.getDropsLocation(), item);
        }
        if (event.getDeathMessage() != null)
            Bukkit.broadcastMessage(event.getDeathMessage());
        setSpectator(killed);
        ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, ChatColor.GREEN + "Players: ", getAliveGamers().size());
        hg.checkWinner();
        p.setVelocity(new Vector());
        for (PotionEffect effect : p.getActivePotionEffects())
            p.removePotionEffect(effect.getType());
        p.teleport(p.getWorld().getHighestBlockAt(p.getLocation()).getLocation().clone().add(0, 10, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 9), true);
        p.sendBlockChange(p.getLocation(), Material.PORTAL.getId(), (byte) 0);
        p.sendBlockChange(p.getLocation(), Material.AIR.getId(), (byte) 0);
        for (Entity entity : p.getWorld().getEntities()) {
            if (entity instanceof Tameable && ((Tameable) entity).isTamed()
                    && ((Tameable) entity).getOwner().getName().equals(p.getName())) {
                if (entity instanceof Wolf)
                    ((Wolf) entity).setSitting(true);
                else if (entity instanceof Ocelot)
                    ((Ocelot) entity).setSitting(true);
                else
                    entity.remove();
            }
            if (entity instanceof Creature && ((Creature) entity).getTarget() == p)
                ((Creature) entity).setTarget(null);
        }
        if (!HungergamesApi.getConfigManager().isSpectatorsEnabled() && !p.hasPermission("hungergames.spectate"))
            p.kickPlayer(event.getDeathMessage());
        HungergamesApi.getAbilityManager().unregisterPlayer(p);
    }

    public void setSpectator(Gamer gamer) {
        gamer.setGhost();
        gamer.hide();
        gamer.setSpectating(true);
        final Player p = gamer.getPlayer();
        p.setAllowFlight(true);
        p.setFlying(true);
        p.setFoodLevel(20);
        p.setHealth(20);
        p.setFireTicks(0);
        gamer.updateSelfToOthers();
        Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
            public void run() {
                p.getInventory().addItem(new ItemStack(Material.COMPASS));
            }
        });
        if (p.getDisplayName().equals(p.getName()))
            p.setDisplayName(ChatColor.DARK_GRAY + p.getName() + ChatColor.RESET);
    }

    public List<Gamer> getAliveGamers() {
        List<Gamer> aliveGamers = new ArrayList<Gamer>();
        for (Gamer gamer : gamers)
            if (gamer.isAlive())
                aliveGamers.add(gamer);
        return aliveGamers;
    }

}
