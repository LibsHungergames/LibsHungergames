package me.libraryaddict.Hungergames.Managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.ArrayList;

import java.util.List;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Manages the spectator status of a player
 * 
 *
 * @author Techcable
 */
public class SpectatorManager {
    private static GameMode SPECTATOR_MODE;

    static {
        try {
            SPECTATOR_MODE = GameMode.valueOf("SPECTATOR");
        } catch (IllegalArgumentException e) {
            SPECTATOR_MODE = null;
        }
    }

    public void activateSpectating(final Gamer toSpectate) {
        if (toSpectate.isSpectator()) return;
        if (SPECTATOR_MODE == null)
            throw new UnsupportedOperationException("Spectating isn't supported in this version of minecraft");
        if (HungergamesApi.getConfigManager().getMainConfig().isShortenedNames() && toSpectate.getPlayer().getPlayerListName().length() <= 14)
            toSpectate.getPlayer().setPlayerListName(ChatColor.GRAY + toSpectate.getPlayer().getPlayerListName());
        toSpectate.getPlayer().setGameMode(SPECTATOR_MODE);
        for (Gamer gamer : HungergamesApi.getPlayerManager().getGamers()) {
            if (gamer.isSpectator() && !HungergamesApi.getConfigManager().getMainConfig().isSpectatorsVisibleToEachOther()) {
                toSpectate.getPlayer().hidePlayer(gamer.getPlayer());
                gamer.getPlayer().hidePlayer(toSpectate.getPlayer());
            }
        }
        toSpectate.setSpectating(true);
        toSpectate.getPlayer().getInventory().remove(HungergamesApi.getInventoryManager().getKitSelector());
        toSpectate.getPlayer().getInventory().remove(HungergamesApi.getInventoryManager().getBuyKit());
        Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
            @Override
            public void run() {
                ItemStack compass = new ItemStack(Material.COMPASS);
                compass.addEnchantment(EnchantmentManager.UNDROPPABLE, 1);
                EnchantmentManager.updateEnchants(compass);
                if (!toSpectate.getPlayer().getInventory().contains(compass))
                    toSpectate.getPlayer().getInventory().addItem(compass);
            }
        });
    }

    public void deactiveSpectating(Gamer spectator) {
        if (!spectator.isSpectator()) return;
        if (SPECTATOR_MODE == null) {
            spectator.setSpectating(false);
            return;
        } //Never should be spectating without 1.8
        spectator.getInventory().clear();
        spectator.getPlayer().setGameMode(GameMode.SURVIVAL);
        for (Gamer gamer : getSpectators()) {
            if (!gamer.getPlayer().canSee(spectator.getPlayer())) gamer.getPlayer().showPlayer(gamer.getPlayer());
        }
        spectator.setSpectating(false);
    }

    public Iterable<Gamer> getSpectators() {
        return Iterables.filter(HungergamesApi.getPlayerManager().getGamers(), new Predicate<Gamer>() {
            @Override
            public boolean apply(Gamer input) {
                return input.isSpectator();
            }
        });
    }

    public static SpectatorManager getInstance() {
        return HungergamesApi.getSpectatorManager();
    }

    public boolean isPacketLibraryInstalled() {
        try {
            Class.forName("com.comphenix.protocol.ProtocolLibrary");
            return ProtocolLibrary.getProtocolManager() != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void updateCanSeeSpectators(Gamer toUpdate) {
        if (!isPacketLibraryInstalled())
            throw new UnsupportedOperationException("ProtocolLib must be installed to see invisible spectators");
        for (Gamer gamer : HungergamesApi.getPlayerManager().getGamers()) {
            if (gamer.isSpectator() && !HungergamesApi.getConfigManager().getMainConfig().isSpectatorsVisibleToEachOther()) {
                toUpdate.getPlayer().hidePlayer(gamer.getPlayer());
            }
        }
    }
}