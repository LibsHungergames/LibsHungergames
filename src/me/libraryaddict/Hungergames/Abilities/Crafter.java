package me.libraryaddict.Hungergames.Abilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.FakeFurnace;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Crafter extends AbilityListener {

    private transient Map<ItemStack, FakeFurnace> furnaces = new HashMap<ItemStack, FakeFurnace>();
    public String craftingStarItemName = ChatColor.WHITE + "Crafting Star";
    public String furnacePowderItemName = ChatColor.WHITE + "Furnace Powder";
    public int furnacePowderItemId = Material.BLAZE_POWDER.getId();
    public int craftingStarItemId = Material.NETHER_STAR.getId();

    public Crafter() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HungergamesApi.getHungergames(), new Runnable() {
            public void run() {
                for (FakeFurnace furnace : furnaces.values())
                    furnace.tick();
            }
        }, 1, 1);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            Player p = event.getPlayer();
            if (isSpecialItem(item, craftingStarItemName) && furnacePowderItemId == item.getTypeId()) {
                p.openWorkbench(null, true);
            } else if (isSpecialItem(item, furnacePowderItemName) && furnacePowderItemId == item.getTypeId()) {
                if (!furnaces.containsKey(item)) {
                    furnaces.put(item, new FakeFurnace());
                }
                ((CraftPlayer) p).getHandle().openFurnace(furnaces.get(item));
            }
        }
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        Iterator<ItemStack> itel = event.getDrops().iterator();
        while (itel.hasNext()) {
            ItemStack item = itel.next();
            if (item != null && furnaces.containsKey(item)) {
                FakeFurnace furnace = furnaces.remove(item);
                if (furnace != null) {
                    for (net.minecraft.server.v1_5_R3.ItemStack i : furnace.getContents())
                        event.getDrops().add(CraftItemStack.asBukkitCopy(i));
                }
            }
        }
    }

}
