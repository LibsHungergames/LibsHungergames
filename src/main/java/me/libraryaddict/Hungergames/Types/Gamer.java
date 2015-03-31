package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.List;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.scoreboard.ScoreboardManager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;

import lombok.*;

public class Gamer {

    private static Economy economy = null;
    private static Hungergames hg = HungergamesApi.getHungergames();
    private static PlayerManager pm = HungergamesApi.getPlayerManager();
    static {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            System.out.print(HungergamesApi.getConfigManager().getLoggerConfig().getFailedToFindVault());
        } else {
            RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
                    .getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
                System.out.print(HungergamesApi.getConfigManager().getLoggerConfig().getRegisteredVault());
            } else {
                System.out.print(HungergamesApi.getConfigManager().getLoggerConfig().getFailedToRegisterVault());
            }
        }
    }
    private boolean build = false;
    private boolean canRide = false;
    private long cooldown = 0;
    private int kills = 0;
    private Player player;
    @Setter
    private boolean spectating = false;
    private Stats stats;

    public Gamer(Player player) {
        this.player = player;
        if (hg.currentTime >= 0) {
            spectating = true;
        }
    }

    public void addBalance(long newBalance) {
        if (economy != null) {
            if (newBalance > 0)
                economy.depositPlayer(getName(), newBalance);
            else
                economy.withdrawPlayer(getName(), -newBalance);
        }
    }

    public void addKill() {
        kills++;
        if (getStats() != null) {
            getStats().addKill();
        }
        if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()) {
            ScoreboardManager.makeScore(DisplaySlot.PLAYER_LIST, getPlayer().getPlayerListName(), getKills());
        }
    }

    /**
     * Can this player interact with the world regardless of being a spectator
     */
    public boolean canBuild() {
        return build;
    }

    /**
     * Can this player interact with the world
     */
    public boolean canInteract() {
        if (build || (hg.currentTime >= 0 && !spectating))
            return true;
        return false;
    }

    public boolean canRide() {
        return canRide;
    }

    /**
     * Clears his inventory and returns it
     * 
     * @return
     */
    public void clearInventory() {
        getPlayer().getInventory().setArmorContents(new ItemStack[4]);
        getPlayer().getInventory().clear();
        getPlayer().setItemOnCursor(new ItemStack(0));
    }

    public long getBalance() {
        if (economy == null)
            return 0;
        return (long) economy.getBalance(getName());
    }

    public long getChunkCooldown() {
        return this.cooldown;
    }

    public List<ItemStack> getInventory() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        for (ItemStack item : getPlayer().getInventory().getContents())
            if (item != null && item.getType() != Material.AIR)
                items.add(item.clone());
        for (ItemStack item : getPlayer().getInventory().getArmorContents())
            if (item != null && item.getType() != Material.AIR)
                items.add(item.clone());
        if (getPlayer().getItemOnCursor() != null && getPlayer().getItemOnCursor().getType() != Material.AIR)
            items.add(getPlayer().getItemOnCursor().clone());
        return items;
    }

    public int getKills() {
        return getStats() == null ? kills : getStats().getKillsCurrent();
    }

    /**
     * @return Player name
     */
    public String getName() {
        return player.getName();
    }

    /**
     * @return Player
     */
    public Player getPlayer() {
        return player;
    }

    public Stats getStats() {
        return stats;
    }

    public boolean isAlive() {
        return !isSpectator() && hg.currentTime >= 0;
    }

    /**
     * Is this player op
     */
    public boolean isOp() {
        return getPlayer().isOp();
    }

    /**
     * Is this player spectating
     */
    public boolean isSpectator() {
        return spectating;
    }

    /**
     * Set if he can build regardless of spectating
     */
    public void setBuild(boolean buildMode) {
        this.build = buildMode;
    }

    public void setChunkCooldown(long newCool) {
        this.cooldown = newCool;
    }

    public void setRiding(boolean ride) {
        this.canRide = ride;
    }

    public void setStats(Stats stats2) {
        this.stats = stats2;
    }
}
