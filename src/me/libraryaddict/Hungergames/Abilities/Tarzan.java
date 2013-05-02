package me.libraryaddict.Hungergames.Abilities;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import me.libraryaddict.Hungergames.Types.AbilityListener;

public class Tarzan extends AbilityListener {

    private HashMap<BlockFace, Byte> faces = new HashMap<BlockFace, Byte>();
    private ArrayList<Integer> ignoreBlockTypes = new ArrayList<Integer>();
    public int scanUpRadius = 5;
    public int scanDownRadius = 5;
    public int scanSidewaysRadius = 5;

    public Tarzan() {
        faces.put(BlockFace.SOUTH, (byte) 1);
        faces.put(BlockFace.WEST, (byte) 2);
        faces.put(BlockFace.NORTH, (byte) 4);
        faces.put(BlockFace.EAST, (byte) 8);
        ignoreBlockTypes.add(0);
        for (int b = 8; b < 12; b++)
            ignoreBlockTypes.add(b);
        ignoreBlockTypes.add(Material.SNOW.getId());
        ignoreBlockTypes.add(Material.LONG_GRASS.getId());
        ignoreBlockTypes.add(Material.RED_MUSHROOM.getId());
        ignoreBlockTypes.add(Material.RED_ROSE.getId());
        ignoreBlockTypes.add(Material.YELLOW_FLOWER.getId());
        ignoreBlockTypes.add(Material.BROWN_MUSHROOM.getId());
        ignoreBlockTypes.add(Material.SIGN_POST.getId());
        ignoreBlockTypes.add(Material.WALL_SIGN.getId());
        ignoreBlockTypes.add(Material.FIRE.getId());
        ignoreBlockTypes.add(Material.TORCH.getId());
        ignoreBlockTypes.add(Material.REDSTONE_WIRE.getId());
        ignoreBlockTypes.add(Material.REDSTONE_TORCH_OFF.getId());
        ignoreBlockTypes.add(Material.REDSTONE_TORCH_ON.getId());
        ignoreBlockTypes.add(Material.VINE.getId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (hasAbility(event.getPlayer()) && event.getPlayer().getItemInHand() != null
                && event.getPlayer().getItemInHand().getType() == Material.VINE) {
            Location loc = event.getPlayer().getLocation();
            for (int x = -scanSidewaysRadius; x < scanSidewaysRadius; x++) {
                for (int z = -scanSidewaysRadius; z < scanSidewaysRadius; z++) {
                    for (int y = -scanDownRadius; y < scanUpRadius; y++) {
                        if (loc.getY() + y <= 0)
                            continue;
                        Block b = loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                        if (b.getType() == Material.AIR) {
                            byte data = 0;
                            if (b.getRelative(BlockFace.UP).getType() == Material.VINE)
                                data = b.getRelative(BlockFace.UP).getData();
                            else
                                for (BlockFace face : faces.keySet()) {
                                    Block block = b.getRelative(face);
                                    if (!ignoreBlockTypes.contains(block.getTypeId()))
                                        data += faces.get(face);
                                }
                            if (data != (byte) 0)
                                b.setTypeIdAndData(Material.VINE.getId(), data, false);
                        }
                    }
                }
            }
        }
    }
}
