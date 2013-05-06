package me.libraryaddict.Hungergames.Abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Burrower extends AbilityListener {
    public int addX = 50;
    public int addZ = 50;
    private transient HashMap<Integer, List<Player>> expires = new HashMap<Integer, List<Player>>();
    public int giveBackItemDelay = 300;
    public int groundBlockData = 0;
    public int groundBlockId = 20;
    private transient Hungergames hg = HungergamesApi.getHungergames();
    public int itemData = 0;
    public int itemId = Material.SLIME_BALL.getId();
    private transient KitManager kits = HungergamesApi.getKitManager();
    public String messageNotHighEnough = ChatColor.RED + "You are too close to the void!";
    public int mustBeHigherThen = 10;
    public boolean randomCords = true;
    public int roofBlockData = 0;
    public int roofBlockId = 20;
    public int roomHeight = 2;
    public int roomWidth = 1;
    public boolean teleportHeightRelativeToCurrentPos = false;
    public int teleportToY = 10;
    public int wallsBlockData = 0;
    public int wallsBlockId = 20;

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        Player p = event.getKilled().getPlayer();
        if (hasAbility(p)) {
            Iterator<Integer> itel = expires.keySet().iterator();
            while (itel.hasNext()) {
                int no = itel.next();
                if (expires.get(no).remove(p))
                    if (expires.get(no).size() == 0)
                        itel.remove();
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction().name().contains("RIGHT") && hasAbility(event.getPlayer()) && item != null
                && item.getTypeId() == itemId && item.getDurability() == itemData) {
            Player p = event.getPlayer();
            if (p.getLocation().getY() > mustBeHigherThen) {
                int x = addX;
                int z = addZ;
                if (randomCords) {
                    x = new Random().nextInt(addX * 2) - addX;
                    z = new Random().nextInt(addZ * 2) - addZ;
                }
                item.setAmount(item.getAmount() - 1);
                if (item.getAmount() == 0)
                    p.setItemInHand(new ItemStack(0));
                Location loc = p.getLocation().clone().add(x, 0, z);
                if (teleportHeightRelativeToCurrentPos)
                    loc.setY(loc.getY() - teleportToY);
                else
                    loc.setY(teleportToY);
                loc = loc.add(0.5, 0, 0.5);
                for (int bX = -roomWidth; bX <= roomWidth; bX++) {
                    for (int bZ = -roomWidth; bZ <= roomWidth; bZ++) {
                        for (int bY = -1; bY <= roomHeight; bY++) {
                            Block b = loc.clone().add(bX, bY, bZ).getBlock();
                            if (bY == roomHeight) {
                                b.setTypeIdAndData(roofBlockId, (byte) roofBlockData, false);
                            } else if (bY == -1) {
                                b.setTypeIdAndData(groundBlockId, (byte) groundBlockData, false);
                            } else if (bX == -roomWidth || bZ == -roomWidth || bX == roomWidth || bZ == roomWidth) {
                                b.setTypeIdAndData(wallsBlockId, (byte) wallsBlockData, false);
                            } else
                                b.setType(Material.AIR);
                        }
                    }
                }
                p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 9);
                p.getWorld().playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 0);
                p.teleport(loc);
                p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 9);
                p.getWorld().playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 0);
                List<Player> players = new ArrayList<Player>();
                if (expires.containsKey(hg.currentTime))
                    players = expires.get(hg.currentTime);
                players.add(p);
                expires.put(hg.currentTime + giveBackItemDelay, players);
            } else
                p.sendMessage(messageNotHighEnough);
        }
    }

    @EventHandler
    public void onSecond(TimeSecondEvent event) {
        if (expires.containsKey(hg.currentTime)) {
            for (Player p : expires.remove(hg.currentTime))
                kits.addItem(p, new ItemStack(itemId, 1, (short) itemData));
        }
    }
}
