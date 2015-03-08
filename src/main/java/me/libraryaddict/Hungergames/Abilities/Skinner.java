package me.libraryaddict.Hungergames.Abilities;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;

public class Skinner extends AbilityListener implements Disableable {
    public int chanceInOneOfSkinning = 3;
    private boolean disable = true;
    public boolean doMessageForSkinned = true;
    public String skinName = "Default";
    public String skinnedMessage = ChatColor.RED + "%s has just skinned you! You are now known as %s!";
    public boolean skinUsersOfKit = true;

    public Skinner() throws Exception {
        if (Bukkit.getPluginManager().getPlugin("LibsDisguises") == null)
            throw new Exception(String.format(HungergamesApi.getConfigManager().getLoggerConfig().getDependencyNotFound(),
                    "Plugin LibsDisguises"));
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null)
            throw new Exception(String.format(HungergamesApi.getConfigManager().getLoggerConfig().getDependencyNotFound(),
                    "Plugin ProtocolLib"));
    }

    private boolean isSkinned(Entity entity) {
        Disguise disguise = DisguiseAPI.getDisguise(entity);
        if (disguise != null) {
            if (disguise.isPlayerDisguise() && ((PlayerDisguise) disguise).getName().equals(skinName)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!event.isCancelled() && event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if (hasAbility((Player) event.getDamager())
                    && (this.chanceInOneOfSkinning <= 0 || new Random().nextInt(this.chanceInOneOfSkinning) == 0)
                    && !isSkinned(event.getEntity())) {
                this.disable = false;
                DisguiseAPI.disguiseToAll(event.getEntity(), new PlayerDisguise(this.skinName));
                if (this.doMessageForSkinned) {
                    ((Player) event.getEntity()).sendMessage(String.format(this.skinnedMessage,
                            ((Player) event.getDamager()).getName(), skinName));
                }
            }
        }
    }

    @EventHandler
    public void onDisguise(DisguiseEvent event) {
        if (isSkinned(event.getEntity()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        if (skinUsersOfKit)
            for (Player p : getMyPlayers()) {
                DisguiseAPI.disguiseToAll(p, new PlayerDisguise(this.skinName));
            }
    }

    @EventHandler
    public void onUnDisguise(UndisguiseEvent event) {
        Gamer gamer = HungergamesApi.getPlayerManager().getGamer(event.getEntity());
        if (gamer != null && gamer.isAlive()) {
            if (isSkinned(event.getEntity()))
                event.setCancelled(true);
        }
    }

    @Override
    public void unregisterPlayer(Player player) {
        myPlayers.remove(player.getName());
        if (disable && HungergamesApi.getHungergames().currentTime >= 0 && this instanceof Disableable && myPlayers.size() == 0) {
            HandlerList.unregisterAll(this);
        }
    }
}
