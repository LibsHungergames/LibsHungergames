package me.libraryaddict.Hungergames.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

public class PlayerManager {

    private ConcurrentLinkedQueue<Gamer> gamers = new ConcurrentLinkedQueue<Gamer>();
    public ConcurrentLinkedQueue<Gamer> loadGamer = new ConcurrentLinkedQueue<Gamer>();
    public HashMap<Gamer, Damage> lastDamager = new HashMap<Gamer, Damage>();
    Hungergames hg = HungergamesApi.getHungergames();
    ChatManager cm = HungergamesApi.getChatManager();
    KitManager kits = HungergamesApi.getKitManager();
    private ArrayList<Integer> nonSolid = new ArrayList<Integer>();

    public PlayerManager() {
        nonSolid.add(0);
        for (int b = 8; b < 12; b++)
            nonSolid.add(b);
        nonSolid.add(Material.SNOW.getId());
        nonSolid.add(Material.LONG_GRASS.getId());
        nonSolid.add(Material.RED_MUSHROOM.getId());
        nonSolid.add(Material.RED_ROSE.getId());
        nonSolid.add(Material.YELLOW_FLOWER.getId());
        nonSolid.add(Material.BROWN_MUSHROOM.getId());
        nonSolid.add(Material.SIGN_POST.getId());
        nonSolid.add(Material.WALL_SIGN.getId());
        nonSolid.add(Material.FIRE.getId());
        nonSolid.add(Material.TORCH.getId());
        nonSolid.add(Material.REDSTONE_WIRE.getId());
        nonSolid.add(Material.REDSTONE_TORCH_OFF.getId());
        nonSolid.add(Material.REDSTONE_TORCH_ON.getId());
        nonSolid.add(Material.VINE.getId());
    }

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
        Gamer gamer = new Gamer(p);
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

    public static int returnChance(int start, int end) {
        return start + (int) (Math.random() * ((end - start) + 1));
    }

    public void sendToSpawn(Gamer gamer) {
        final Player p = gamer.getPlayer();
        Location spawn = p.getWorld().getSpawnLocation().clone();
        int chances = 0;
        if (p.isInsideVehicle())
            p.leaveVehicle();
        p.eject();
        int spawnRadius = 8;
        int spawnHeight = 5;
        while (chances < 100) {
            chances++;
            Location newLoc = new Location(p.getWorld(), spawn.getX() + returnChance(-spawnRadius, spawnRadius), spawn.getY()
                    + new Random().nextInt(spawnHeight), spawn.getZ() + returnChance(-spawnRadius, spawnRadius));
            if (nonSolid.contains(newLoc.getBlock().getTypeId())
                    && nonSolid.contains(newLoc.getBlock().getRelative(BlockFace.UP).getTypeId())) {
                while (newLoc.getBlockY() >= 1 && nonSolid.contains(newLoc.getBlock().getRelative(BlockFace.DOWN).getTypeId())) {
                    newLoc = newLoc.add(0, -1, 0);
                }
                if (newLoc.getBlockY() <= 1)
                    continue;
                spawn = newLoc;
                break;
            }
        }
        if (spawn.equals(p.getWorld().getSpawnLocation())) {
            spawn = new Location(p.getWorld(), spawn.getX() + returnChance(-spawnRadius, spawnRadius), 0, spawn.getZ()
                    + returnChance(-spawnRadius, spawnRadius));
            spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
            if (gamer.isAlive() && spawn.getY() <= 1) {
                spawn.getBlock().setType(Material.GLASS);
                spawn.setY(spawn.getY() + 1);
            }
        }
        final Location destination = spawn.add(0.5, 0.1, 0.5);
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

    private String formatDeathMessage(String deathMessage, Player p) {
        String kitName = cm.getKillMessageNoKit();
        if (kits.getKitByPlayer(p) != null)
            kitName = kits.getKitByPlayer(p).getName();
        return deathMessage.replaceAll(p.getName(), String.format(cm.getKillMessageFormatPlayerKit(), p.getName(), kitName));
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
        event.setDeathMessage(this.formatDeathMessage(event.getDeathMessage(), p));
        if (event.getKillerPlayer() != null) {
            event.getKillerPlayer().addKill();
            event.setDeathMessage(this.formatDeathMessage(event.getDeathMessage(), event.getKillerPlayer().getPlayer()));
        }
        int reward = hg.getPrize(getAliveGamers().size());
        if (reward > 0)
            killed.addBalance(reward);
        hg.cannon();
        killed.clearInventory();
        World world = p.getWorld();
        for (ItemStack item : event.getDrops()) {
            if (item == null || item.getType() == Material.AIR || item.containsEnchantment(EnchantmentManager.UNLOOTABLE))
                continue;
            else if (item.hasItemMeta())
                world.dropItemNaturally(event.getDropsLocation(), item.clone()).getItemStack().setItemMeta(item.getItemMeta());
            else
                world.dropItemNaturally(event.getDropsLocation(), item);
        }
        if (event.getDeathMessage() != null)
            Bukkit.broadcastMessage(event.getDeathMessage());
        setSpectator(killed);
        ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, cm.getScoreboardPlayersLength(), getAliveGamers().size());
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
