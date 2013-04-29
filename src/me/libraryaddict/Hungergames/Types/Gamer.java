package me.libraryaddict.Hungergames.Types;

import java.util.ArrayList;
import java.util.List;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Managers.ScoreboardManager;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.Packet201PlayerInfo;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.DisplaySlot;

public class Gamer {

    private boolean spectating = false;
    private boolean build = false;
    private Player player;
    /**
     * True when the game hasn't started. If he wants to see other players.
     * 
     * False when the game has started if he wants to see other players
     */
    private boolean seeInvis = true;
    private boolean canRide = false;
    private boolean vip = false;
    private long cooldown = 0;
    private static Economy economy = null;
    private static PlayerManager pm = HungergamesApi.getPlayerManager();
    private static Hungergames hg = HungergamesApi.getHungergames();
    private int kills = 0;

    public Gamer(Player player, boolean isVip) {
        vip = isVip;
        this.player = player;
        if (hg.currentTime >= 0) {
            seeInvis = false;
            spectating = true;
        }
        setupEconomy();
    }

    public boolean isVip() {
        return vip;
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

    public long getBalance() {
        if (economy == null)
            return 0;
        return (long) economy.getBalance(getName());
    }

    public void addBalance(long newBalance) {
        if (economy != null) {
            if (newBalance > 0)
                economy.depositPlayer(getName(), newBalance);
            else
                economy.withdrawPlayer(getName(), newBalance);
        }
    }

    /**
     * @return Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return Player name
     */
    public String getName() {
        return player.getName();
    }

    /**
     * Is this player op
     */
    public boolean isOp() {
        return getPlayer().isOp();
    }

    /**
     * Set their width and length to 0, Makes arrows move through them
     */
    public void setGhost() {
        EntityPlayer p = ((CraftPlayer) getPlayer()).getHandle();
        p.width = 0;
        p.length = 0;
    }

    /**
     * Restore their width and length. Makes arrows hit them
     */
    public void setHuman() {
        EntityPlayer p = ((CraftPlayer) getPlayer()).getHandle();
        p.width = 0.6F;
        p.length = 1.8F;
    }

    /**
     * Hides the player from this gamer
     * 
     * @param hider
     */
    public void hide(Player hider) {
        Packet201PlayerInfo packet = new Packet201PlayerInfo(hider.getPlayerListName(), false, 9999);
        if (hider != null)
            if (getPlayer().canSee(hider)) {
                getPlayer().hidePlayer(hider);
                ((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(packet);
            }
    }

    /**
     * Shows the player to this gamer
     */
    public void show(Player hider) {
        Packet201PlayerInfo packet = new Packet201PlayerInfo(hider.getPlayerListName(), true, 0);
        if (hider != null)
            if (!getPlayer().canSee(hider)) {
                getPlayer().showPlayer(hider);
                ((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(packet);
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
     * Hides the player to everyone in the game
     */
    public void hide() {
        for (Gamer gamer : pm.getGamers())
            gamer.hide(getPlayer());
    }

    /**
     * Can this player interact with the world regardless of being a spectator
     */
    public boolean canBuild() {
        return build;
    }

    /**
     * Is this player spectating
     */
    public boolean isSpectator() {
        return spectating;
    }

    /**
     * Can this player interact with the world
     */
    public boolean canInteract() {
        if (build || (hg.currentTime >= 0 && !spectating))
            return true;
        return false;
    }

    /**
     * Can this player see people or not see.
     */
    public boolean viewPlayers() {
        return seeInvis;
    }

    /**
     * Set them to view invis?
     */
    public void seeInvis(boolean seeInvis) {
        this.seeInvis = seeInvis;
    }

    /**
     * Set them to spectating
     */
    public void setSpectating(boolean spectating) {
        this.spectating = spectating;
    }

    /**
     * Set if he can build regardless of spectating
     */
    public void setBuild(boolean buildMode) {
        this.build = buildMode;
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

    public void setRiding(boolean ride) {
        this.canRide = ride;
    }

    public boolean canRide() {
        return canRide;
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
     * If this player can see that gamer
     */
    public boolean canSee(Gamer gamer) {
        return seeInvis || gamer.isAlive();
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

    public boolean isAlive() {
        return !isSpectator() && hg.currentTime >= 0;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        kills++;
        ScoreboardManager.makeScore("Main", DisplaySlot.PLAYER_LIST, getName(), getKills());
    }

    public long getChunkCooldown() {
        return this.cooldown;
    }

    public void setChunkCooldown(long newCool) {
        this.cooldown = newCool;
    }
}
