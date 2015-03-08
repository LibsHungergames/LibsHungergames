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
	if (SPECTATOR_MODE == null) throw new UnsupportedOperationException("Spectating isn't supported in this version of minecraft");
	if (HungergamesApi.getConfigManager().getMainConfig().isShortenedNames() && toSpectate.getPlayer().getPlayerListName().length() <= 14) toSpectate.getPlayer().setPlayerListName(ChatColor.GRAY + toSpectate.getPlayer().getPlayerListName());
        toSpectate.getPlayer().setGameMode(SPECTATOR_MODE);
        for (Gamer gamer : HungergamesApi.getPlayerManager().getGamers()) {
            if (gamer.isSpectator() && !HungergamesApi.getConfigManager().getMainConfig().isSpectatorsVisibleToEachOther()) {
                if (!toSpectate.isSeeInvisibleSpectators()) toSpectate.getPlayer().hidePlayer(gamer.getPlayer());
                if (!gamer.isSeeInvisibleSpectators()) gamer.getPlayer().hidePlayer(toSpectate.getPlayer());
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
        if (!isPacketLibraryInstalled()) throw new UnsupportedOperationException("ProtocolLib must be installed to see invisible spectators");
        getSpectatorPacketAdapter().sendFakeGamemode(toUpdate, getSpectators());
        if (toUpdate.isSeeInvisibleSpectators()) {
            for (Gamer gamer : HungergamesApi.getPlayerManager().getGamers()) {
                if (!toUpdate.getPlayer().canSee(gamer.getPlayer())) toUpdate.getPlayer().showPlayer(gamer.getPlayer());
            }
        } else {
            for (Gamer gamer : HungergamesApi.getPlayerManager().getGamers()) {
                if (gamer.isSpectator() && !HungergamesApi.getConfigManager().getMainConfig().isSpectatorsVisibleToEachOther()) {
                    toUpdate.getPlayer().hidePlayer(gamer.getPlayer());
                }
            }
        }
    }
    
    private SpectatorPacketAdapter spectatorPacketAdapter;
    public SpectatorPacketAdapter getSpectatorPacketAdapter() {
        if (spectatorPacketAdapter == null) {
            if (!isPacketLibraryInstalled()) throw new UnsupportedOperationException("ProtocolLib must be installed to see invisible spectators");
            this.spectatorPacketAdapter = new SpectatorPacketAdapter();
            ProtocolLibrary.getProtocolManager().addPacketListener(spectatorPacketAdapter);
        }
        return this.spectatorPacketAdapter;
    }
    
    public class SpectatorPacketAdapter extends PacketAdapter {
        public SpectatorPacketAdapter() {
           super(HungergamesApi.getHungergames(), ListenerPriority.HIGH, PacketType.Play.Server.PLAYER_INFO, PacketType.Play.Server.GAME_STATE_CHANGE);
        }

        @SuppressWarnings("deprecation")
        public void sendFakeGamemode(Gamer receiver, Iterable<Gamer> players) {
            PacketContainer container = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
            container.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE);
            List<PlayerInfoData> playerInfoDataList = new ArrayList<PlayerInfoData>();
            for (Gamer gamer : players) {
                Player player = gamer.getPlayer();
                PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(player), -1, EnumWrappers.NativeGameMode.fromBukkit(getFakeGamemode(gamer, receiver)), null);
                playerInfoDataList.add(playerInfoData);
            }
            container.getPlayerInfoDataLists().write(0, playerInfoDataList);
        }
        
        @Override
        public void onPacketReceiving(PacketEvent event) {
            if (event.getPacket().getType() == PacketType.Play.Server.PLAYER_INFO) {
                if (event.getPacket().getPlayerInfoAction().read(0) != EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE) return;
                List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(0);
                List<PlayerInfoData> newPlayerInfoDataList = new ArrayList<PlayerInfoData>();
                for (PlayerInfoData playerInfoData : playerInfoDataList) {
                    Player entityPlayer = Bukkit.getPlayer(playerInfoData.getProfile().getUUID());
                    if (entityPlayer == null) continue;
                    Gamer player = HungergamesApi.getPlayerManager().getGamer(entityPlayer);
                    if (player == null) continue;
                    Gamer receiver = HungergamesApi.getPlayerManager().getGamer(event.getPlayer());
                    if (receiver == null) continue;
                    EnumWrappers.NativeGameMode fakeGamemode = EnumWrappers.NativeGameMode.fromBukkit(getFakeGamemode(player, receiver));
                    PlayerInfoData newPlayerInfoData = new PlayerInfoData(playerInfoData.getProfile(), playerInfoData.getPing(), fakeGamemode, playerInfoData.getDisplayName());
                    newPlayerInfoDataList.add(newPlayerInfoData);
                }
                event.getPacket().getPlayerInfoDataLists().write(0, newPlayerInfoDataList);
            } else if (event.getPacketType() == PacketType.Play.Server.GAME_STATE_CHANGE) {
                if (event.getPacket().getIntegers().read(0) != 3) return;
                Gamer gamer = HungergamesApi.getPlayerManager().getGamer(event.getPlayer());
                event.getPacket().getFloat().write(0, (float)getFakeGamemode(gamer, gamer).getValue());
            }
        }
    }
    
    public GameMode getFakeGamemode(Gamer player, Gamer receiver) {
        if (player.getPlayer().getGameMode().equals(SPECTATOR_MODE) && receiver.isSeeInvisibleSpectators()) {
            return GameMode.CREATIVE;
        }
        return player.getPlayer().getGameMode();
    }
    
}