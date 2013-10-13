package me.libraryaddict.Hungergames.Listeners;

import java.util.Random;

import lombok.Data;
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
    private static LibsFeastManager feastManager;

    protected FeastConfig config = HungergamesApi.getConfigManager().getFeastConfig();
    protected Location feastLocation;
    protected GenerationManager gen;
    private boolean isEnabled;

    public LibsFeastManager() {
        Location spawn = HungergamesApi.getHungergames().world.getSpawnLocation();
        feastLocation = new Location(spawn.getWorld(), spawn.getX() + (new Random().nextInt(200) - 100), -1, spawn.getZ()
                + (new Random().nextInt(200) - 100));
    }

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
        for (int x = -height; x < height + 1; x++) {
            for (int z = -height; z < height + 1; z++) {
                int y = Math.abs(x);
                if (Math.abs(z) > y)
                    y = Math.abs(z);
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
                    block.setTypeIdAndData(Material.CHEST.getId(), (byte) 0, false);
                    gen.addToProcessedBlocks(block);
                    Chest chest = (Chest) block.getState();
                    cm.fillChest(chest.getInventory());
                    chest.update();
                } else
                    gen.setBlockFast(block, feastBlock.getTypeId(), feastBlock.getDurability());
            }
        }
    }

    public void generatePlatform(Location loc, int chestLayers, int platformSize) {
        ItemStack item = HungergamesApi.getConfigManager().getFeastConfig().getFeastGroundBlock();
        gen.generatePlatform(loc, chestLayers, platformSize, item.getType(), item.getDurability());
    }

    public Location getFeastLocation() {
        return feastLocation;
    }

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        int currentTime = HungergamesApi.getHungergames().currentTime;
        if (config.getFeastAdvertisements().containsKey(currentTime)) {
            Bukkit.broadcastMessage(String.format(config.getFeastAdvertisements().get(currentTime), feastLocation.getX(),
                    feastLocation.getY(), feastLocation.getZ(), HungergamesApi.getHungergames().returnTime(currentTime)));
        }
        if (config.getScoreboardStrings().containsKey(currentTime)) {
            ScoreboardManager.setDisplayName("Main", DisplaySlot.SIDEBAR, config.getScoreboardStrings().get(currentTime));
        }
        if (currentTime == config.getFeastPlatformGenerateTime()) {
            feastLocation.setY(feastLocation.getWorld().getHighestBlockYAt(feastLocation.getBlockX(), feastLocation.getBlockZ()));
            int feastHeight = gen.getSpawnHeight(feastLocation, config.getFeastSize());
            generatePlatform(feastLocation, config.getChestLayersHeight(), feastHeight);
            HungergamesApi.getInventoryManager().updateSpectatorHeads();
            Bukkit.getPluginManager().callEvent(new FeastAnnouncedEvent());
        }
        if (currentTime == config.getFeastGenerateTime()) {
            ScoreboardManager.hideScore("Main", DisplaySlot.SIDEBAR, config.getScoreboardFeastStartingIn());
            generateChests(feastLocation, config.getChestLayersHeight());
            World world = HungergamesApi.getHungergames().world;
            world.playSound(world.getSpawnLocation(), Sound.IRONGOLEM_DEATH, 1000, 0);
            Bukkit.getPluginManager().callEvent(new FeastSpawnedEvent());
        } else if (currentTime > config.getFeastPlatformGenerateTime() && currentTime < config.getFeastGenerateTime()) {
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, config.getScoreboardFeastStartingIn(),
                    config.getFeastGenerateTime() - currentTime);
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
