package me.libraryaddict.Hungergames.Managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Interfaces.ChestManager;
import me.libraryaddict.Hungergames.Interfaces.FeastManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.RandomItem;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class LibsFeastManager implements FeastManager {
    private class BlockInfo {
        public BlockInfo(int id, byte data) {
            this.id = id;
            this.data = data;
        }

        byte data;
        int id;
    }

    // This manages the chests, The buildings
    private List<BlockFace> faces = new ArrayList<BlockFace>();
    private List<BlockFace> jungleFaces = new ArrayList<BlockFace>();
    private LinkedList<Block> processedBlocks = new LinkedList<Block>();
    private BukkitRunnable runnable;
    private BukkitRunnable chestGenerator;
    private HashMap<Block, BlockInfo> queued = new HashMap<Block, BlockInfo>();

    public LibsFeastManager() {
        faces.add(BlockFace.UP);
        faces.add(BlockFace.DOWN);
        faces.add(BlockFace.SOUTH);
        faces.add(BlockFace.NORTH);
        faces.add(BlockFace.WEST);
        faces.add(BlockFace.EAST);
        faces.add(BlockFace.SELF);
        jungleFaces.add(BlockFace.UP);
        jungleFaces.add(BlockFace.SELF);
        jungleFaces.add(BlockFace.DOWN);
        Hungergames hg = HungergamesApi.getHungergames();
        File file = new File(hg.getDataFolder().toString() + "/feast.yml");
        try {
            if (!file.exists())
                file.createNewFile();
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            ChestManager chest = HungergamesApi.getChestManager();
            if (!config.contains("FeastLoot")) {
                ArrayList<String> strings = new ArrayList<String>();
                for (RandomItem item : chest.getRandomItems())
                    strings.add(item.toString());
                config.set("How To Use",
                        "Chance in hundred, MinAmount, MaxAmount, ID or Material, Data Value, Addictional data like in kits items such as enchants");
                config.set("FeastLoot", strings);
                config.save(file);
            }
            chest.clearRandomItems();
            for (String string : config.getStringList("FeastLoot")) {
                chest.addRandomItem(new RandomItem(string));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        chestGenerator = new BukkitRunnable() {
            public void run() {
                ChestManager cm = HungergamesApi.getChestManager();
                ConfigManager config = HungergamesApi.getConfigManager();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Location l = p.getLocation().clone();
                    l.setY(loc.getY());
                    if (l.distance(loc) < height && p.getLocation().getY() >= l.getY()
                            && p.getLocation().getY() - l.getY() <= height) {
                        l.setY(l.getY() + height + 1);
                        p.teleport(l);
                    }
                }
                ItemStack feast = config.getFeast();
                ItemStack feastInsides = config.getFeastInsides();
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
                                setBlockFast(b, feastInsides.getTypeId(), feastInsides.getDurability());
                            else
                                setBlockFast(b, feast.getTypeId(), feast.getDurability());
                        }
                        if (x == 0 && z == 0) {
                            setBlockFast(block, Material.ENCHANTMENT_TABLE.getId(), (byte) 0);
                            setBlockFast(block.getRelative(BlockFace.DOWN), feastInsides.getType().getId(),
                                    (config.isFeastTntIgnite() && feastInsides.getType() == Material.TNT ? (byte) 1
                                            : feastInsides.getDurability()));
                        } else if (Math.abs(x + z) % 2 == 0) {
                            block.setTypeIdAndData(Material.CHEST.getId(), (byte) 0, false);
                            processedBlocks.add(block);
                            Chest chest = (Chest) block.getState();
                            cm.fillChest(chest.getInventory());
                            chest.update();
                        } else
                            setBlockFast(block, feast.getTypeId(), feast.getDurability());
                    }
                }
            }
        };
        if (runnable == null) {
            chestGenerator.runTask(HungergamesApi.getHungergames());
            chestGenerator = null;
        }
    }

    public void generatePillars(Location loc, int radius) {
        // Generates pillars

        ConfigManager config = HungergamesApi.getConfigManager();
        int[] cords = new int[] { (int) (-radius / 2.5), (int) (radius / 2.5) };
        int pillarRadius = Math.round(radius / 8);
        ItemStack pillarCorner = config.getPillarCorner();
        ItemStack pillarInsides = config.getPillarInsides();
        if (config.generatePillars())
            for (int px = 0; px <= 1; px++)
                for (int pz = 0; pz <= 1; pz++)
                    for (int x = -pillarRadius; x <= pillarRadius; x++) {
                        for (int z = -pillarRadius; z <= pillarRadius; z++) {
                            Block b = loc.getWorld().getBlockAt(x + loc.getBlockX() + cords[px], loc.getBlockY() - 2,
                                    z + loc.getBlockZ() + cords[pz]);
                            while (!isSolid(b)) {
                                if (Math.abs(x) == pillarRadius && Math.abs(z) == pillarRadius)
                                    setBlockFast(b, pillarCorner.getTypeId(), pillarCorner.getDurability());
                                else
                                    setBlockFast(b, pillarInsides.getTypeId(), pillarInsides.getDurability());
                                b = b.getRelative(BlockFace.DOWN);
                            }
                        }
                    }
        // naturalizeSpawn(loc, radius);
    }

    /**
     * Generates a feast
     * 
     * @param loc
     * @param lowestLevel
     * @param radius
     */
    public void generatePlatform(Location loc, int lowestLevel, int radius) {
        ConfigManager config = HungergamesApi.getConfigManager();
        ItemStack feastGround = config.getFeastGround();
        int yHeight = radius / 2;
        if (yHeight < 4)
            yHeight = 4;
        generatePlatform(loc, lowestLevel, radius, yHeight, feastGround.getTypeId(), feastGround.getDurability());
        generatePillars(loc, radius);
    }

    /**
     * Generates a feast
     * 
     * @param loc
     * @param lowestLevel
     * @param radius
     */
    public void generatePlatform(Location loc, int lowestLevel, int radius, int yHeight, int platformGround,
            short platformDurability) {
        loc.setY(lowestLevel + 1);
        double radiusSquared = radius * radius;
        // Sets to air and generates to stand on
        for (int radiusX = -radius; radiusX <= radius; radiusX++) {
            for (int radiusZ = -radius; radiusZ <= radius; radiusZ++) {
                if ((radiusX * radiusX) + (radiusZ * radiusZ) <= radiusSquared) {
                    for (int y = loc.getBlockY() - 1; y < loc.getBlockY() + yHeight; y++) {
                        if (y > loc.getWorld().getMaxHeight())
                            break;
                        Block b = loc.getWorld().getBlockAt(radiusX + loc.getBlockX(), y, radiusZ + loc.getBlockZ());
                        removeLeaves(b);
                        if (y >= loc.getBlockY()) {// If its less then 0
                            setBlockFast(b, 0, (byte) 0);
                        } else {
                            // Generates to stand on
                            setBlockFast(b, platformGround, platformDurability);
                        }
                    }
                }
            }
        }
    }

    public int getHeight(ArrayList<Integer> heights, int radius) {
        List<List<Integer>> commons = new ArrayList<List<Integer>>();
        for (int i = 0; i < heights.size(); i++) {
            List<Integer> numbers = new ArrayList<Integer>();
            numbers.add(heights.get(i));
            for (int a = i; a < heights.size(); a++) {
                if (heights.get(i) - heights.get(a) >= -radius)
                    numbers.add(heights.get(a));
                else
                    break;
            }
            commons.add(numbers);
        }
        int highest = 0;
        List<Integer> found = new ArrayList<Integer>();
        for (List l : commons) {
            if (l.size() > highest) {
                highest = l.size();
                found = l;
            }
        }
        if (found.size() == 0)
            return -1;
        return found.get((int) Math.round(found.size() / 3));
    }

    private Block getHighest(Block b) {
        while (b.getY() > 1 && !isSolid(b))
            b = b.getRelative(BlockFace.DOWN);
        return b;
    }

    /**
     * Gets the best Y level to spawn the feast at This also modifies the Location fed to it for use by feast generation
     * 
     * @param loc
     * @param radius
     * @return Best Y Level
     */
    public int getSpawnHeight(Location loc, int radius) {
        ArrayList<Integer> heightLevels = new ArrayList<Integer>();
        for (double degree = 0; degree <= 360; degree += 1) {
            double angle = degree * Math.PI / 180;
            int x = (int) (loc.getX() + .5 + radius * Math.cos(angle));
            int z = (int) (loc.getZ() + radius * Math.sin(angle));
            Block b = getHighest(loc.getWorld().getHighestBlockAt(x, z));
            if (isBlockValid(b))
                heightLevels.add(b.getY());
            /*
             * // Do it again but at 2/3 the radius angle = degree * Math.PI /
             * 180; x = (int) (loc.getX() + .5 + ((radius / 3) * 2) *
             * Math.cos(angle)); z = (int) (loc.getZ() + ((radius / 3) * 2) *
             * Math.sin(angle)); b =
             * getHighest(loc.getWorld().getHighestBlockAt(x, z)); if
             * (!b.getChunk().isLoaded()) b.getChunk().load(true); if
             * (isBlockValid(b)) heightLevels.add(b.getY());
             */
        }
        Block b = getHighest(loc.getBlock());
        if (isBlockValid(b))
            heightLevels.add(b.getY());
        Collections.sort(heightLevels);
        int y = getHeight(heightLevels, 5);
        if (y == -1)
            y = b.getY();
        loc = new Location(loc.getWorld(), loc.getBlockX(), y + 1, loc.getBlockZ());
        return y;
    }

    private boolean isBlockValid(Block b) {
        if (b.isLiquid() || b.getRelative(BlockFace.UP).isLiquid())
            return false;
        return true;
    }

    private boolean isSolid(Block b) {
        return (!(b.getType() == Material.AIR || b.isLiquid() || b.getType() == Material.VINE || b.getType() == Material.LOG
                || b.getType() == Material.LEAVES || b.getType() == Material.SNOW || b.getType() == Material.LONG_GRASS
                || b.getType() == Material.WOOD || b.getType() == Material.COBBLESTONE || b.getType().name().contains("FLOWER") || b
                .getType().name().contains("MUSHROOM")));
    }

    /**
     * This generates the grass and flowers. Pretty!
     */
    public void naturalizeSpawn(Location loc, int radius) {
        double radiusSquared = radius * radius;
        for (double x = -radius; x <= radius; x++) {
            for (double z = -radius; z <= radius; z++) {
                if ((x * x) + (z * z) <= radiusSquared) {
                    Block b = loc.getWorld().getBlockAt((int) x + loc.getBlockX(), loc.getBlockY(), (int) z + loc.getBlockZ());
                    if (b.getType() == Material.AIR)
                        setNature(b);
                }
            }
        }
    }

    private void removeLeaves(Block b) {
        for (BlockFace face : ((b.getBiome() == Biome.JUNGLE || b.getBiome() == Biome.JUNGLE_HILLS) ? jungleFaces : faces)) {
            Block newB = b.getRelative(face);
            if ((newB.getType() == Material.LEAVES || newB.getType() == Material.LOG || newB.getType() == Material.VINE)) {
                if (!queued.containsKey(newB) && !processedBlocks.contains(newB)) {
                    setBlockFast(newB, 0, (byte) 0);
                    if (newB.getRelative(BlockFace.DOWN).getType() == Material.DIRT) {
                        newB = newB.getRelative(BlockFace.DOWN);
                        if (!queued.containsKey(newB) && !processedBlocks.contains(newB)) {
                            setBlockFast(newB, Material.GRASS.getId(), (byte) 0);
                        }
                    }
                }
            } else if (newB.getType() == Material.SNOW && face == BlockFace.UP) {
                if (!queued.containsKey(newB) && !processedBlocks.contains(newB)) {
                    setBlockFast(newB, 0, (byte) 0);
                }
            }
        }
    }

    public void setBlockFast(Block b, int typeId, short s) {
        try {
            if (b.getTypeId() != typeId || b.getData() != s) {
                queued.put(b, new BlockInfo(typeId, (byte) s));
                if (runnable == null) {
                    runnable = new BukkitRunnable() {
                        public void run() {
                            if (queued.size() == 0) {
                                runnable = null;
                                processedBlocks.clear();
                                if (chestGenerator != null) {
                                    chestGenerator.runTask(HungergamesApi.getHungergames());
                                    chestGenerator = null;
                                }
                                cancel();
                            }
                            int i = 0;
                            HashMap<Block, BlockInfo> toDo = new HashMap<Block, BlockInfo>();
                            for (Block b : queued.keySet()) {
                                if (i++ >= 200)
                                    break;
                                toDo.put(b, queued.get(b));
                                b = b.getRelative(BlockFace.UP);
                                while (b != null
                                        && queued.containsKey(b)
                                        && (b.isLiquid() || b.getType() == Material.SAND || b.getType() == Material.ANVIL || b
                                                .getType() == Material.GRAVEL)) {
                                    toDo.put(b, queued.get(b));
                                    b = b.getRelative(BlockFace.UP);
                                }
                            }
                            for (Block b : toDo.keySet()) {
                                if (!processedBlocks.contains(b))
                                    processedBlocks.add(b);
                                queued.remove(b);
                                b.setTypeIdAndData(toDo.get(b).id, toDo.get(b).data, true);
                            }
                        }
                    };
                    runnable.runTaskTimer(HungergamesApi.getHungergames(), 2, 1);
                }
                // return ((CraftChunk) b.getChunk()).getHandle().a(b.getX() & 15,
                // b.getY(), b.getZ() & 15, typeId, data);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setNature(Block b) {
        int num = new Random().nextInt(((b.getBiome() == Biome.ICE_MOUNTAINS || b.getBiome() == Biome.ICE_PLAINS) ? 20 : 7));
        if (num > 10) {
            setBlockFast(b, Material.SNOW.getId(), (byte) 0);
        } else if (num == 1) {
            setBlockFast(b, Material.LONG_GRASS.getId(), (byte) 1);
        }
    }
}
