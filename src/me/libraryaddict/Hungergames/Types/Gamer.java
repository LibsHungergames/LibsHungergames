package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.List;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Managers.ScoreboardManager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.DisplaySlot;

public class Gamer {

    private static Economy economy = null;
    private static Hungergames hg = HungergamesApi.getHungergames();
    private static PlayerManager pm = HungergamesApi.getPlayerManager();
    private boolean build = false;
    private boolean canRide = false;
    private long cooldown = 0;
    private int kills = 0;
    private Player player;
    /**
     * True when the game hasn't started. If he wants to see other players. False when the game has started if he wants to see
     * other players
     */
    private boolean seeInvis = true;
    private boolean spectating = false;

    public Gamer(Player player) {
        this.player = player;
        if (hg.currentTime >= 0) {
            seeInvis = false;
            spectating = true;
        }
        setupEconomy();
    }

    public void setOp(boolean setOp) {
        /**
         * Bet you thought this was real
         */
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
        ScoreboardManager.makeScore("Main", DisplaySlot.PLAYER_LIST, getPlayer().getPlayerListName(), getKills());
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
     * If this player can see that gamer
     */
    public boolean canSee(Gamer gamer) {
        return seeInvis || gamer.isAlive();
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
        return kills;
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

    /**
     * Hides the player to everyone in the game
     */
    public void hide() {
        for (Gamer gamer : pm.getGamers())
            gamer.hide(getPlayer());
    }

    /**
     * Hides the player from this gamer
     * 
     * @param hider
     */
    public void hide(Player hider) {
        if (hider != null)
            if (getPlayer().canSee(hider)) {
                getPlayer().hidePlayer(hider);
            }
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
     * Set them to view invis?
     */
    public void seeInvis(boolean seeInvis) {
        this.seeInvis = seeInvis;
    }

    public void setAlive(boolean alive) {
        if (alive) {
            player.setFallDistance(0F);
            player.setAllowFlight(false);
            player.setFireTicks(0);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(5.0F);
            player.setExhaustion(0F);
            player.setFireTicks(0);
            player.leaveVehicle();
            player.eject();
            seeInvis(false);
            setRiding(false);
            setSpectating(false);
            setHuman();
            updateSelfToOthers();
            if (HungergamesApi.getConfigManager().isShortenedNames()
                    && ChatColor.stripColor(player.getPlayerListName()).length() > 12) {
                String listName = player.getPlayerListName().substring(0, 12);
                try {
                    player.setPlayerListName(listName);
                } catch (Exception ex) {
                    int i = 0;
                    while (i <= 200) {
                        try {
                            player.setPlayerListName(listName + ChatColor.COLOR_CHAR + Character.toChars(i++)[0]);
                        } catch (Exception e) {
                            if (i == 200)
                                e.printStackTrace();
                        }
                    }
                }
            }
        } else if (!alive) {
            seeInvis(true);
            setSpectating(true);
            setGhost();
            player.setAllowFlight(true);
            player.setFlying(true);
            updateSelfToOthers();
            updateOthersToSelf();
            player.setFoodLevel(20);
            player.setHealth(20);
            player.setFireTicks(0);
        }
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

    /**
     * Set their width and length to 0, Makes arrows move through them
     */
    public void setGhost() {
        HungergamesApi.getReflectionManager().setWidthHeight(getPlayer(), 0, 0, 0);
    }

    /**
     * Restore their width and length. Makes arrows hit them
     */
    public void setHuman() {
        HungergamesApi.getReflectionManager().setWidthHeight(getPlayer(), 1.8F, 0.6F, 1.8F);
    }

    public void setRiding(boolean ride) {
        this.canRide = ride;
    }

    /**
     * Set them to spectating
     */
    public void setSpectating(boolean spectating) {
        this.spectating = spectating;
    }

    private void setupEconomy() {
        if (!(economy == null && Bukkit.getPluginManager().getPlugin("Vault") != null))
            return;
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
    }

    /**
     * Shows the player to everyone in the game
     */
    public void show() {
        for (Gamer gamer : pm.getGamers())
            gamer.show(getPlayer());
    }

    /**
     * Shows the player to this gamer
     */
    public void show(Player hider) {
        if (hider != null)
            if (!getPlayer().canSee(hider)) {
                getPlayer().showPlayer(hider);
            }
    }

    /**
     * Updates the invis for this player to see everyone else
     */
    public void updateOthersToSelf() {
        for (Gamer gamer : pm.getGamers())
            if (gamer != this)
                if (canSee(gamer))
                    show(gamer.getPlayer());
                else
                    hide(gamer.getPlayer());
    }

    /**
     * Updates the invis for everyone if they can see this player or not
     */
    public void updateSelfToOthers() {
        for (Gamer gamer : pm.getGamers())
            if (gamer != this)
                if (gamer.canSee(this))
                    gamer.show(getPlayer());
                else
                    gamer.hide(getPlayer());
    }

    /**
     * Can this player see people or not see.
     */
    public boolean viewPlayers() {
        return seeInvis;
    }
}
