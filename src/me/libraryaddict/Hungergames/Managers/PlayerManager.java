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
import me.libraryaddict.Hungergames.Configs.MainConfig;
import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.Damage;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

public class PlayerManager {

    public static int returnChance(int start, int end) {
        return start + (int) (Math.random() * ((end - start) + 1));
    }

    private TranslationConfig cm = HungergamesApi.getConfigManager().getTranslationsConfig();
    private ConcurrentLinkedQueue<Gamer> gamers = new ConcurrentLinkedQueue<Gamer>();
    private Hungergames hg = HungergamesApi.getHungergames();
    private KitManager kits = HungergamesApi.getKitManager();
    public HashMap<Gamer, Damage> lastDamager = new HashMap<Gamer, Damage>();
    public ConcurrentLinkedQueue<Gamer> loadGamer = new ConcurrentLinkedQueue<Gamer>();
    private ArrayList<Integer> nonSolid = new ArrayList<Integer>();
    private Iterator<Location> spawnItel;
    private HashMap<Location, Integer[]> spawns = new HashMap<Location, Integer[]>();

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

    public void addSpawnPoint(Location loc, int radius, int height) {
        spawns.put(loc.add(0.000001, 0, 0.000001), new Integer[] { radius, height });
    }

    private String formatDeathMessage(String deathMessage, Player p) {
        String playerKit = cm.getKillMessageNoKit();
        if (kits.getKitByPlayer(p) != null)
            playerKit = kits.getKitByPlayer(p).getName();
        String killMessage = cm.getKillMessageFormatPlayerKit();
        if (killMessage.contains("%Player%") || killMessage.contains("%Kit%")) {
            playerKit = killMessage.replace("%Player%", p.getName()).replace("%Kit%", playerKit);
        } else {
            playerKit = String.format(killMessage, p.getName(), playerKit);
        }
        return deathMessage.replace(p.getName(), playerKit);
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

    public Gamer getKiller(Gamer victim) {
        Damage dmg = lastDamager.get(victim);
        Gamer backup = null;
        if (dmg != null)
            if (dmg.getTime() >= System.currentTimeMillis())
                backup = dmg.getDamager();
        return backup;
    }

    public void killPlayer(Gamer gamer, Entity killer, Location dropLoc, List<ItemStack> drops, String deathMsg) {
        if (!hg.doSeconds || hg.currentTime < 0)
            return;
        PlayerKilledEvent event = new PlayerKilledEvent(gamer, killer, getKiller(gamer), deathMsg, dropLoc, drops);
        Bukkit.getPluginManager().callEvent(event);
        manageDeath(event);
    }

    public void manageDeath(PlayerKilledEvent event) {
        Gamer killed = event.getKilled();
        final Player p = killed.getPlayer();
        p.setHealth(p.getMaxHealth());
        if (event.isCancelled())
            return;
        for (HumanEntity human : p.getInventory().getViewers())
            human.closeInventory();
        p.leaveVehicle();
        p.eject();
        p.setLevel(0);
        p.setExp(0F);
        if (event.getDeathMessage().equals(ChatColor.stripColor(event.getDeathMessage())))
            event.setDeathMessage(ChatColor.DARK_RED + event.getDeathMessage());
        event.setDeathMessage(this.formatDeathMessage(
                event.getDeathMessage().replace("%Remaining%", "" + (getAliveGamers().size() - 1)), p));
        if (event.getKillerPlayer() != null) {
            event.getKillerPlayer().addKill();
            event.setDeathMessage(this.formatDeathMessage(event.getDeathMessage(), event.getKillerPlayer().getPlayer()));
        }
        Bukkit.broadcastMessage(event.getDeathMessage());
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
        if (HungergamesApi.getConfigManager().getMainConfig().isKickOnDeath() && !p.hasPermission("hungergames.spectate"))
            p.kickPlayer(String.format(cm.getKickDeathMessage(), event.getDeathMessage()));
        HungergamesApi.getAbilityManager().unregisterPlayer(p);
        HungergamesApi.getInventoryManager().updateSpectatorHeads();
    }

    public Gamer registerGamer(Player p) {
        Gamer gamer = new Gamer(p);
        gamers.add(gamer);
        return gamer;
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

    public void sendToSpawn(Gamer gamer) {
        final Player p = gamer.getPlayer();
        Location originalSpawn = p.getWorld().getSpawnLocation();
        MainConfig main = HungergamesApi.getConfigManager().getMainConfig();
        int spawnRadius = main.getSpawnRadius();
        int spawnHeight = main.getSpawnHeight();
        if (spawns.size() > 0) {
            if (spawnItel == null || !spawnItel.hasNext())
                spawnItel = spawns.keySet().iterator();
            originalSpawn = spawnItel.next();
            spawnRadius = spawns.get(originalSpawn)[0];
            spawnHeight = spawns.get(originalSpawn)[1];
        }
        Location spawn = originalSpawn.clone();
        int chances = 0;
        if (p.isInsideVehicle())
            p.leaveVehicle();
        p.eject();
        boolean foundSpawn = true;
        if (Math.abs(spawnHeight) > 0 || Math.abs(spawnRadius) > 0) {
            foundSpawn = false;
            while (chances < main.getTimesToCheckForValidSpawnPerPlayer()) {
                chances++;
                Location newLoc = new Location(p.getWorld(), spawn.getX() + returnChance(-spawnRadius, spawnRadius), spawn.getY()
                        + new Random().nextInt(spawnHeight), spawn.getZ() + returnChance(-spawnRadius, spawnRadius));
                if (nonSolid.contains(newLoc.getBlock().getTypeId())
                        && nonSolid.contains(newLoc.getBlock().getRelative(BlockFace.UP).getTypeId())) {
                    while (newLoc.getBlockY() >= 1
                            && nonSolid.contains(newLoc.getBlock().getRelative(BlockFace.DOWN).getTypeId())) {
                        newLoc = newLoc.add(0, -1, 0);
                    }
                    if (newLoc.getBlockY() <= 1)
                        continue;
                    spawn = newLoc;
                    foundSpawn = true;
                    break;
                }
            }
        }
        if (!foundSpawn && spawn.getX() == originalSpawn.getX() && spawn.getY() == originalSpawn.getY()
                && spawn.getZ() == originalSpawn.getZ()) {
            spawn = new Location(p.getWorld(), spawn.getX() + returnChance(-spawnRadius, spawnRadius), 0, spawn.getZ()
                    + returnChance(-spawnRadius, spawnRadius));
            spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
            if (gamer.isAlive() && spawn.getY() <= 1) {
                spawn.getBlock().setType(Material.GLASS);
                spawn.setY(spawn.getY() + 1);
            }
        }
        final Location destination = spawn;
        if (spawn.getX() % 1 == 0 && spawn.getY() % 1 == 0 && spawn.getZ() % 1 == 0)
            spawn.add(0.5, 0.1, 0.5);
        destination.setPitch(originalSpawn.getPitch());
        destination.setYaw(originalSpawn.getYaw());
        destination.setWorld(p.getWorld());
        p.teleport(destination);
        Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
            public void run() {
                p.teleport(destination);
            }
        });
    }

    public void setSpectator(final Gamer gamer) {
        gamer.setAlive(false);
        gamer.getPlayer().getInventory().remove(HungergamesApi.getInventoryManager().getKitSelector());
        Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
            public void run() {
                ItemStack compass = new ItemStack(Material.COMPASS);
                compass.addEnchantment(EnchantmentManager.UNDROPPABLE, 1);
                EnchantmentManager.updateEnchants(compass);
                if (!gamer.getPlayer().getInventory().contains(compass))
                    gamer.getPlayer().getInventory().addItem(compass);
            }
        });
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
