package me.libraryaddict.Hungergames.Listeners;

import java.util.Iterator;
import java.util.Random;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Events.PlayerTrackEvent;
import me.libraryaddict.Hungergames.Managers.ChatManager;
import me.libraryaddict.Hungergames.Managers.NameManager;
import me.libraryaddict.Hungergames.Managers.TranslationManager;
import me.libraryaddict.Hungergames.Managers.ConfigManager;
import me.libraryaddict.Hungergames.Managers.EnchantmentManager;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.KitSelectorManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Managers.ScoreboardManager;
import me.libraryaddict.Hungergames.Types.Damage;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.Kit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;

public class PlayerListener implements Listener {

    private final TranslationManager cm = HungergamesApi.getTranslationManager();
    private final ConfigManager config = HungergamesApi.getConfigManager();
    private final Hungergames hg = HungergamesApi.getHungergames();
    private final KitSelectorManager icon = HungergamesApi.getKitSelector();
    private final KitManager kits = HungergamesApi.getKitManager();
    private final PlayerManager pm = HungergamesApi.getPlayerManager();
    private final ChatManager chat = HungergamesApi.getChatManager();
    private final NameManager name = HungergamesApi.getNameManager();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {
        Gamer gamer = pm.getGamer(event.getPlayer());
        if (!gamer.canInteract())
            event.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Gamer gamer = pm.getGamer(event.getPlayer());
        if (!config.isSpectatorChatHidden() && !gamer.isAlive() && hg.doSeconds
                && !gamer.getPlayer().hasPermission("hungergames.spectatorchat")) {
            Iterator<Player> players = event.getRecipients().iterator();
            while (players.hasNext()) {
                Gamer g = pm.getGamer(players.next());
                if (!g.getPlayer().hasPermission("hungergames.spectatorchat") && g.isAlive())
                    players.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(BlockDamageEvent event) {
        if (!pm.getGamer(event.getPlayer()).canInteract())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player
                || (event.getEntity() instanceof Tameable && ((Tameable) event.getEntity()).isTamed())) {
            if ((event.getEntity() instanceof Player && (!hg.doSeconds || !pm.getGamer(event.getEntity()).isAlive()))
                    || hg.currentTime <= config.getInvincibilityTime())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && !pm.getGamer(event.getDamager()).canInteract()) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntity() instanceof Player) {
            Gamer damager = null;
            if (event.getDamager() instanceof Player)
                damager = pm.getGamer(event.getDamager());
            else if (event.getDamager() instanceof Projectile)
                damager = pm.getGamer(((Projectile) event.getDamager()).getShooter());
            else if (event.getDamager() instanceof Tameable) {
                if (((Tameable) event.getDamager()).getOwner() != null)
                    damager = pm.getGamer(((Tameable) event.getDamager()).getOwner().getName());
            }
            if (damager != null) {
                if (damager.canInteract())
                    pm.lastDamager.put(pm.getGamer(event.getEntity()), new Damage((System.currentTimeMillis() / 1000) + 60,
                            damager));
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        Player p = event.getEntity();
        EntityDamageEvent cause = event.getEntity().getLastDamageCause();
        String deathMessage = ChatColor.stripColor(event.getDeathMessage());
        if (cause instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) cause;
            if (entityEvent.getDamager() instanceof Player) {
                Player dmg = (Player) entityEvent.getDamager();
                deathMessage = cm.getKillMessages()[new Random().nextInt(cm.getKillMessages().length)];
                deathMessage = deathMessage.replace("%Killed%", p.getName()).replace("%Killer%", dmg.getName())
                        .replace("%Weapon%", name.getItemName(dmg.getItemInHand()));
            }
        } else if (cause.getCause() == DamageCause.FALL)
            deathMessage = String.format(cm.getKillMessageFellToDeath(), p.getName());
        Gamer gamer = pm.getGamer(p);
        pm.killPlayer(gamer, null, p.getLocation(), gamer.getInventory(), deathMessage);
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onEnter(EntityPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        if (!pm.getGamer(event.getPlayer()).isAlive())
            event.setAmount(0);
    }

    @EventHandler
    public void onHungry(FoodLevelChangeEvent event) {
        if (!pm.getGamer(event.getEntity()).isAlive())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Gamer gamer = pm.getGamer(p);
        if (!gamer.canInteract()) {
            event.setCancelled(true);
        }
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.COMPASS && event.getAction() != Action.PHYSICAL) {
            double distance = 10000;
            Player victim = null;
            for (Gamer game : HungergamesApi.getPlayerManager().getAliveGamers()) {
                double distOfPlayerToVictim = p.getLocation().distance(game.getPlayer().getLocation());
                if (distOfPlayerToVictim < distance && distOfPlayerToVictim > 15) {
                    distance = distOfPlayerToVictim;
                    victim = game.getPlayer();
                }
            }
            PlayerTrackEvent trackEvent = new PlayerTrackEvent(gamer, victim,
                    (victim == null ? cm.getMessagePlayerTrackNoVictim() : String.format(cm.getMessagePlayerTrack(),
                            victim.getName())));
            Bukkit.getPluginManager().callEvent(trackEvent);
            if (!trackEvent.isCancelled()) {
                p.sendMessage(trackEvent.getMessage());
                p.setCompassTarget(trackEvent.getLocation());
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (gamer.isAlive() && gamer.canRide())
                if (p.isInsideVehicle() == true)
                    p.leaveVehicle();
            if (item != null) {
                if (item.equals(icon.getKitSelector())) {
                    icon.openInventory(p);
                    event.setCancelled(true);
                }
                if (item.getType() == Material.MUSHROOM_SOUP && config.isMushroomStew()) {
                    if (p.getHealth() < 20 || p.getFoodLevel() < 19) {
                        event.setCancelled(true);
                        if (p.getHealth() < 20)
                            if (p.getHealth() + config.mushroomStewRestores() <= 20)
                                p.setHealth(p.getHealth() + config.mushroomStewRestores());
                            else
                                p.setHealth(20);
                        else if (p.getFoodLevel() < 20)
                            if (p.getFoodLevel() + config.mushroomStewRestores() <= 20)
                                p.setFoodLevel(p.getFoodLevel() + config.mushroomStewRestores());
                            else
                                p.setFoodLevel(20);
                        if (item.getAmount() > 1) {
                            item.setAmount(item.getAmount() - 1);
                            kits.addItem(p, new ItemStack(Material.BOWL));
                        } else
                            item = new ItemStack(Material.BOWL);
                        p.setItemInHand(item);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Gamer gamer = pm.getGamer(event.getPlayer());
        if (!gamer.canInteract()) {
            event.setCancelled(true);
        }
        if (!gamer.isAlive()) {
            if (event.getRightClicked() instanceof Player) {
                Player victim = (Player) event.getRightClicked();
                Player p = event.getPlayer();
                p.sendMessage(String.format(cm.getMessagePlayerHasHealthAndHunger(), victim.getName(), victim.getHealth(),
                        victim.getName(), victim.getFoodLevel()));
            }
            if (gamer.canRide()) {
                if (event.getPlayer().isInsideVehicle() == false && event.getRightClicked().getVehicle() != event.getPlayer())
                    event.getRightClicked().setPassenger(event.getPlayer());
                else
                    event.getPlayer().leaveVehicle();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!pm.getGamer(event.getWhoClicked()).canInteract()) {
            event.setCancelled(true);
        }
        if (event.getView().getTopInventory().getType() == InventoryType.ANVIL && event.getCurrentItem() != null
                && event.getCurrentItem().getType() != Material.AIR) {
            for (Enchantment enchant : event.getCurrentItem().getEnchantments().keySet())
                if (!EnchantmentManager.isNatural(enchant)) {
                    event.setCancelled(true);
                    ((Player) event.getWhoClicked()).sendMessage(cm.getMessagePlayerWarningForgeUnstableEnchants());
                }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (kits.getKitByPlayer(event.getPlayer()) == null)
            kits.setKit(event.getPlayer(), kits.defaultKitName);
        event.setJoinMessage(null);
        final Gamer gamer = pm.registerGamer(event.getPlayer());
        Player p = gamer.getPlayer();
        if (cm.getShouldIMessagePlayersWhosePlugin())
            p.sendMessage(String.format(cm.getMessagePlayerWhosePlugin(), hg.getDescription().getVersion()));
        p.setScoreboard(ScoreboardManager.getScoreboard("Main"));
        p.setAllowFlight(true);
        for (PotionEffect effect : p.getActivePotionEffects())
            p.removePotionEffect(effect.getType());
        if (hg.currentTime >= 0) {
            pm.setSpectator(gamer);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
                public void run() {
                    gamer.hide(gamer.getPlayer());
                }
            }, 0L);
        } else {
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, cm.getScoreboardPlayersLength(),
                    Bukkit.getOnlinePlayers().length);
            gamer.clearInventory();
            if (config.useKitSelector())
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
                    public void run() {
                        gamer.getPlayer().getInventory().addItem(icon.getKitSelector());
                    }
                }, 0L);
        }
        pm.sendToSpawn(gamer);
        gamer.updateOthersToSelf();
        gamer.updateSelfToOthers();
        pm.loadGamer.add(gamer);
        if (p.hasPermission("hungergames.update") && config.getLatestVersion() != null)
            p.sendMessage(String.format(cm.getMessagePlayerUpdateAvailable(), config.getLatestVersion(), hg.getDescription()
                    .getVersion()));
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() == Result.KICK_FULL)
            event.disallow(Result.KICK_FULL, cm.getKickGameFull());
        else if (hg.currentTime >= 0 && !config.isSpectatorsEnabled() && !event.getPlayer().hasPermission("hungergames.spectate"))
            event.disallow(Result.KICK_OTHER, cm.getKickSpectatorsDisabled());
    }

    @EventHandler
    public void onPickup(PlayerDropItemEvent event) {
        if (!pm.getGamer(event.getPlayer()).canInteract())
            event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (!pm.getGamer(event.getPlayer()).isAlive())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event) {
        if (!pm.getGamer(event.getPlayer()).canInteract()
                || event.getBlock().getLocation().getBlockY() > event.getBlock().getWorld().getMaxHeight())
            event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        chat.removeChatter(event.getPlayer().getName());
        event.setQuitMessage(null);
        Gamer gamer = pm.getGamer(event.getPlayer());
        Kit kit = kits.getKitByPlayer(event.getPlayer());
        if (kit != null)
            kit.removePlayer(event.getPlayer());
        if (gamer.isAlive() && hg.currentTime >= 0 && pm.getAliveGamers().size() > 1) {
            pm.killPlayer(gamer, null, gamer.getPlayer().getLocation(), gamer.getInventory(),
                    String.format(cm.getKillMessageLeavingGame(), gamer.getName()));
        }
        pm.unregisterGamer(gamer);
    }

    @EventHandler
    public void onVechileEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            if (!pm.getGamer(event.getEntered()).canInteract()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVechileEvent(VehicleDestroyEvent event) {
        if (event.getAttacker() instanceof Player) {
            if (!pm.getGamer(event.getAttacker()).canInteract()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVechileMove(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof Player)
            if (!pm.getGamer(event.getEntity()).canInteract())
                event.setCancelled(true);
    }
}
