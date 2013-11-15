package me.libraryaddict.Hungergames.Listeners;

import java.util.ArrayList;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import me.libraryaddict.Hungergames.Configs.FeastConfig;
import me.libraryaddict.Hungergames.Events.FeastAnnouncedEvent;
import me.libraryaddict.Hungergames.Events.FeastSpawnedEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Interfaces.ChestManager;
import me.libraryaddict.Hungergames.Managers.ConfigManager;
import me.libraryaddict.Hungergames.Managers.GenerationManager;
import me.libraryaddict.Hungergames.Managers.ScoreboardManager;
import me.libraryaddict.Hungergames.Types.CordPair;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class LibsFeastManager implements Listener {
    @Getter
    @Setter
    private static LibsFeastManager feastManager = new LibsFeastManager();
    protected FeastConfig config = HungergamesApi.getConfigManager().getFeastConfig();
    protected Location feastLocation;
    protected GenerationManager gen = HungergamesApi.getGenerationManager();
    private boolean isEnabled;

    /**
     * Generates the chests. Height is the amount of chests. There will be a enchanting table on top of this putting the total at
     * 4 blocks high.
     * 
     * @param loc
     * @param height
     *            of chests
     */
    public void generateChests(final Location loc, final int height) {
        ChestManager cm = HungergamesApi.getChestManager();
        ConfigManager config = HungergamesApi.getConfigManager();
        for (Player p : Bukkit.getOnlinePlayers()) {
            Location l = p.getLocation().clone();
            l.setY(loc.getY());
            if (l.distance(loc) < height && p.getLocation().getY() >= l.getY() && p.getLocation().getY() - l.getY() <= height) {
                l.setY(l.getY() + height + 1);
                p.teleport(l);
            }
        }
        ItemStack feastInside = config.getFeastConfig().getFeastInsides();
        ItemStack feastBlock = config.getFeastConfig().getFeastFeastBlock();
        ArrayList<CordPair> chunksToUnload = new ArrayList<CordPair>();
        for (int x = -height; x < height + 1; x++) {
            for (int z = -height; z < height + 1; z++) {
                // Get whichever number is higher
                int y = Math.abs(x);
                if (Math.abs(z) > y)
                    y = Math.abs(z);
                // Got the highest..
                // Now invert it and add on the height to get a pillar thats higher when closer to spanw
                y = -y + height;
                Block block = loc.clone().add(x, y, z).getBlock();
                Block b = block;
                // while repeated > 0
                for (int yLevel = y; yLevel > 0; yLevel--) {
                    b = b.getRelative(BlockFace.DOWN);
                    if (y - 1 >= yLevel)
                        gen.setBlockFast(b, feastInside.getTypeId(), feastInside.getDurability());
                    else
                        gen.setBlockFast(b, feastBlock.getTypeId(), feastBlock.getDurability());
                }
                if (x == 0 && z == 0) {
                    gen.setBlockFast(block, Material.ENCHANTMENT_TABLE.getId(), (byte) 0);
                    gen.setBlockFast(block.getRelative(BlockFace.DOWN), feastInside.getTypeId(),
                            (feastInside.getType() == Material.TNT ? 1 : feastInside.getDurability()));
                } else if (Math.abs(x + z) % 2 == 0) {
                    gen.addToProcessedBlocks(block);
                    boolean loadChunk = !b.getWorld().isChunkLoaded(b.getChunk().getX(), b.getChunk().getZ());
                    if (loadChunk) {
                        b.getWorld().loadChunk(b.getChunk().getX(), b.getChunk().getZ());
                        chunksToUnload.add(new CordPair(b.getChunk().getX(), b.getChunk().getZ()));
                    }
                    block.setType(Material.CHEST);
                    Chest chest = (Chest) block.getState();
                    cm.fillChest(chest.getInventory());
                    chest.update();
                } else
                    gen.setBlockFast(block, feastBlock.getTypeId(), feastBlock.getDurability());
            }
        }
        World world = Bukkit.getWorlds().get(0);
        for (CordPair pair : chunksToUnload) {
            if (world.isChunkLoaded(pair.getX(), pair.getZ()) && !world.isChunkInUse(pair.getX(), pair.getZ())) {
                world.unloadChunk(pair.getX(), pair.getZ());
            }
        }
    }

    public void generatePlatform(Location loc, int chestLayers, int platformSize) {
        ItemStack item = HungergamesApi.getConfigManager().getFeastConfig().getFeastGroundBlock();
        gen.generatePlatform(loc, chestLayers, platformSize, item.getType(), item.getDurability());
        if (config.isPillarsEnabled())
            gen.generatePillars(loc, platformSize, config.getPillarCorners().getTypeId(), config.getPillarCorners()
                    .getDurability(), config.getPillarInsides().getTypeId(), config.getPillarInsides().getDurability());
    }

    public Location getFeastLocation() {
        if (feastLocation == null) {
            Location spawn = HungergamesApi.getHungergames().world.getSpawnLocation();
            int dist = config.getFeastMaxDistanceFromSpawn();
            feastLocation = new Location(spawn.getWorld(), spawn.getX() + (new Random().nextInt(dist * 2) - dist), -1,
                    spawn.getZ() + (new Random().nextInt(dist * 2) - dist));
        }
        return feastLocation;
    }

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        int currentTime = HungergamesApi.getHungergames().currentTime;
        if (currentTime == config.getFeastPlatformGenerateTime()) {
            Location feastLoc = getFeastLocation();
            int feastHeight = gen.getSpawnHeight(getFeastLocation(), config.getFeastSize());
            feastLoc.setY(feastHeight);
            setFeastLocation(feastLoc);
            generatePlatform(getFeastLocation(), feastHeight, config.getFeastSize());
            HungergamesApi.getInventoryManager().updateSpectatorHeads();
            Bukkit.getPluginManager().callEvent(new FeastAnnouncedEvent());
        }
        if (currentTime == config.getFeastGenerateTime()) {
            ScoreboardManager.hideScore("Main", DisplaySlot.SIDEBAR, config.getScoreboardFeastStartingIn());
            generateChests(getFeastLocation(), config.getChestLayersHeight());
            World world = HungergamesApi.getHungergames().world;
            world.playSound(world.getSpawnLocation(), Sound.IRONGOLEM_DEATH, 1000, 0);
            Bukkit.getPluginManager().callEvent(new FeastSpawnedEvent());
        } else if (currentTime > config.getFeastPlatformGenerateTime() && currentTime < config.getFeastGenerateTime()) {
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, config.getScoreboardFeastStartingIn(),
                    config.getFeastGenerateTime() - currentTime);
        }
        if (config.getFeastAdvertisements().containsKey(currentTime)) {
            Bukkit.broadcastMessage(String.format(config.getFeastAdvertisements().get(currentTime), getFeastLocation().getX(),
                    getFeastLocation().getY(), getFeastLocation().getZ(),
                    HungergamesApi.getHungergames().returnTime(currentTime - config.getFeastGenerateTime())));
        }
    }

    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled) {
            isEnabled = enabled;
            if (enabled) {
                Bukkit.getPluginManager().registerEvents(this, HungergamesApi.getHungergames());
            } else {
                HandlerList.unregisterAll(this);
            }
        }
    }

    public void setFeastLocation(Location newFeastLocation) {
        feastLocation = newFeastLocation;
    }
}
