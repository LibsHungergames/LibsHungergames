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

    public static int returnChance(int start, int end) {
        return start + (int) (Math.random() * ((end - start) + 1));
    }

    TranslationManager cm = HungergamesApi.getTranslationManager();
    private ConcurrentLinkedQueue<Gamer> gamers = new ConcurrentLinkedQueue<Gamer>();
    Hungergames hg = HungergamesApi.getHungergames();
    KitManager kits = HungergamesApi.getKitManager();
    public HashMap<Gamer, Damage> lastDamager = new HashMap<Gamer, Damage>();
    public ConcurrentLinkedQueue<Gamer> loadGamer = new ConcurrentLinkedQueue<Gamer>();

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

    private String formatDeathMessage(String deathMessage, Player p) {
        String kitName = cm.getKillMessageNoKit();
        if (kits.getKitByPlayer(p) != null)
            kitName = kits.getKitByPlayer(p).getName();
        return deathMessage.replaceAll(p.getName(), String.format(cm.getKillMessageFormatPlayerKit(), p.getName(), kitName));
    }

    public List<Gamer> getAliveGamers() {
        List<Gamer> aliveGamers = new ArrayList<Gamer>();
        for (Gamer gamer : gamers)
            if (gamer.isAlive())
                aliveGamers.add(gamer);
        return aliveGamers;
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

    public List<Gamer> getGamers() {
        List<Gamer> game = new ArrayList<Gamer>();
        for (Gamer g : gamers)
            game.add(g);
        return game;
    }

    public void killPlayer(Gamer gamer, Entity killer, Location dropLoc, List<ItemStack> drops, String deathMsg) {
        if (!hg.doSeconds || hg.currentTime < 0)
            return;
        PlayerKilledEvent event = new PlayerKilledEvent(gamer, killer, getKiller(gamer), deathMsg, dropLoc, drops);
        Bukkit.getPluginManager().callEvent(event);
        manageDeath(event);
    }

    public Gamer getKiller(Gamer victim) {
        Damage dmg = lastDamager.get(victim);
        Gamer backup = null;
        if (dmg != null)
            if (dmg.getTime() >= System.currentTimeMillis())
                backup = dmg.getDamager();
        return backup;
    }

    public void removeKilled(Gamer gamer) {
        lastDamager.remove(gamer);
        Iterator<Gamer> itel = lastDamager.keySet().iterator();
        while (itel.hasNext()) {
            Gamer g = itel.next();
            if (lastDamager.get(g).getDamager() == gamer)
                itel.remove();
        }
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
        if (HungergamesApi.getConfigManager().isKickOnDeath() && !p.hasPermission("hungergames.spectate"))
            p.kickPlayer(String.format(HungergamesApi.getTranslationManager().getKickDeathMessage(), event.getDeathMessage()));
        HungergamesApi.getAbilityManager().unregisterPlayer(p);
        HungergamesApi.getInventoryManager().updateSpectatorHeads();
    }

    public Gamer registerGamer(Player p) {
        Gamer gamer = new Gamer(p);
        gamers.add(gamer);
        gamer.clearInventory();
        return gamer;
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

    public void unregisterGamer(Gamer gamer) {
        gamers.remove(gamer);
    }

}
