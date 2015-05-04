package me.libraryaddict.Hungergames;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import me.libraryaddict.Hungergames.Configs.MainConfig;
import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Events.InvincibilityWearOffEvent;
import me.libraryaddict.Hungergames.Events.PlayerWinEvent;
import me.libraryaddict.Hungergames.Events.ServerShutdownEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Listeners.*;
import me.libraryaddict.Hungergames.Managers.*;
import me.libraryaddict.Hungergames.techcable.Title;
import me.libraryaddict.Hungergames.Types.CustomDeathCause;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Utilities.MapLoader;
import me.libraryaddict.death.DeathHandler;
import me.libraryaddict.scoreboard.ScoreboardManager;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;

import com.comphenix.protocol.PacketType;

public class Hungergames extends JavaPlugin {

    private static HashMap<Integer, String> stages = new HashMap<Integer, String>();

    public static void registerStage(int timeToActivate, String display) {
        stages.put(timeToActivate, display);
    }

    public int currentTime = -270;
    /**
     * doSeconds is false when the game has ended
     */
    public boolean doSeconds = true;
    public HashMap<Location, EntityType> entitysToSpawn = new HashMap<Location, EntityType>();
    private MainConfig mainConfig;
    private PlayerListener playerListener;
    private PlayerManager pm;
    private TranslationConfig translationsConfig;

    public World world;

    public void checkWinner() {
        if (doSeconds) {
            List<Gamer> aliveGamers = pm.getAliveGamers();
            if (aliveGamers.size() == 1) {
                doSeconds = false;
                final Gamer winner = aliveGamers.get(0);
                if (winner.getStats() != null) {
                    winner.getStats().addWin();
                }
                Bukkit.getPluginManager().callEvent(new PlayerWinEvent(winner));
                int reward = getPrize(1) * winner.getPayMultiplier();
                if (reward > 0)
                    winner.addBalance(reward);
                winner.getPlayer().setAllowFlight(true);
                Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                    public void run() {
                        String msg = String.format(translationsConfig.getBroadcastWinnerWon(), winner.getName());
                        if (PacketType.Play.Server.TITLE.isSupported()) {
                            Title title = new Title(msg, "Good Game");
                            title.sendTo(Bukkit.getOnlinePlayers());
                        } else {
                            Bukkit.broadcastMessage(msg);
                        }
                    }
                }, 0, mainConfig.getWonBroadcastsDelay() * 20);
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    public void run() {
                        String kick = String.format(translationsConfig.getKickMessageWon(), winner.getName());
                        shutdown(kick);
                    }
                }, mainConfig.getGameShutdownDelay() * 20);
            } else if (aliveGamers.size() == 0) {
                doSeconds = false;
                shutdown(translationsConfig.getKickNobodyWonMessage());
            }
        }
    }

    private void doBorder(Gamer gamer) {
        Player p = gamer.getPlayer();
        Location pLoc = p.getLocation().clone();
        Location sLoc = world.getSpawnLocation().clone();
        double border = mainConfig.getBorderSize();
        if (mainConfig.isRoundedBorder()) {
            sLoc.setY(pLoc.getY());
            double fromSpawn = pLoc.distance(sLoc);
            if (fromSpawn >= border - 20) {
                // Warn
                p.sendMessage(translationsConfig.getMessagePlayerApproachingBorder());
                if (fromSpawn >= border) {
                    // Punish
                    if (gamer.isAlive()) {
                        // Damage and potentially kill.
                        double dmg = HungergamesApi.getConfigManager().getMainConfig().getDamageBorderDeals();
                        if (p.getHealth() - dmg > 0) {
                            p.damage(0);
                            p.setHealth(p.getHealth() - dmg);
                        } else {
                            pm.killPlayer(gamer, null, pLoc, gamer.getInventory(), CustomDeathCause.BORDER);
                        }
                    } else if (border > 10) {
                        // Hmm. Preferably I tp them back inside.
                        // May as well just tp to spawn. No harm done.
                        pm.sendToSpawn(gamer);
                    }
                }
            }
        } else {
            boolean message = false;
            Location tpTo = pLoc.clone();
            int xDist = pLoc.getBlockX() - sLoc.getBlockX();
            if (Math.abs(xDist) > border - 20) {
                message = true;
                if (Math.abs(xDist) > border) {
                    if (xDist > 0) {
                        tpTo.setX(border - 2 + sLoc.getBlockX());
                    } else {
                        tpTo.setX(border + 2 + sLoc.getBlockX());
                    }
                }
            }
            int zDist = pLoc.getBlockZ() - sLoc.getBlockZ();
            if (Math.abs(zDist) > border - 20) {
                message = true;
                if (Math.abs(zDist) > border) {
                    if (zDist > 0) {
                        tpTo.setZ(border - 2 + sLoc.getBlockZ());
                    } else {
                        tpTo.setZ(border + 2 + sLoc.getBlockZ());
                    }
                }
            }
            if (message)
                p.sendMessage(translationsConfig.getMessagePlayerApproachingBorder());
            if (tpTo.getBlockX() != pLoc.getBlockX() || tpTo.getBlockZ() != pLoc.getBlockZ()) {
                if (gamer.isAlive()) {
                    // Damage and potentially kill.
                    double dmg = HungergamesApi.getConfigManager().getMainConfig().getDamageBorderDeals();
                    if (p.getHealth() - dmg > 0) {
                        p.damage(0);
                        p.setHealth(p.getHealth() - dmg);
                    } else {
                        pm.killPlayer(gamer, null, pLoc, gamer.getInventory(), CustomDeathCause.BORDER);
                    }
                } else if (border > 10) {
                    gamer.getPlayer().teleport(tpTo);
                }
            }
        }
    }

    private void doStage() {
        if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()
                && stages.containsKey(HungergamesApi.getHungergames().currentTime)) {
            ScoreboardManager.setDisplayName(DisplaySlot.SIDEBAR, stages.get(HungergamesApi.getHungergames().currentTime));
        }
    }

    public int getPrize(int pos) {
        if (HungergamesApi.getConfigManager().getWinnersConfig().getPrizesForPlacing().containsKey(pos))
            return HungergamesApi.getConfigManager().getWinnersConfig().getPrizesForPlacing().get(pos);
        return 0;
    }

    public boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.kickPlayer(translationsConfig.getKickGameShutdownUnexpected());
            PlayerQuitEvent event = new PlayerQuitEvent(p, "He came, he saw, he conquered");
            playerListener.onQuit(event);
        }
        int slept = (HungergamesApi.getPlayerManager().saveGamer.size() * 2) + 5;
        while (HungergamesApi.getConfigManager().getMySqlConfig().isStatsEnabled()
                && !HungergamesApi.getPlayerManager().saveGamer.isEmpty() && slept-- >= 0) {
            try {
                System.out.print(String.format(HungergamesApi.getConfigManager().getLoggerConfig().getWaitingForStatsToSave(),
                        HungergamesApi.getPlayerManager().saveGamer.size(), (slept + 1)));
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        HungergamesApi.getMySqlManager().getPlayerJoinThread().mySqlDisconnect();
        HungergamesApi.getMySqlManager().getPlayerJoinThread().stop();
        HungergamesApi.getMySqlManager().getPlayerQuitThread().mySqlDisconnect();
        HungergamesApi.getMySqlManager().getPlayerQuitThread().stop();
    }

    @Override
    public void onEnable() {
        HungergamesApi.init(this);
        ConfigManager config = HungergamesApi.getConfigManager();
        config.loadConfigs();
        HungergamesApi.getChestManager().setRandomItems(config.getFeastConfig().getRandomItems());
        translationsConfig = config.getTranslationsConfig();
        mainConfig = config.getMainConfig();
        pm = HungergamesApi.getPlayerManager();
        HungergamesApi.getMySqlManager();
        MapLoader.loadMap();
        if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()) {
            ScoreboardManager.setDisplayName(DisplaySlot.SIDEBAR, translationsConfig.getScoreboardStagePreGame());
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                world = Bukkit.getWorlds().get(0);
                world.setAutoSave(false);
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setTime(6000);
                if (mainConfig.isForcedCords())
                    world.setSpawnLocation(mainConfig.getForceSpawnX(),
                            world.getHighestBlockYAt(mainConfig.getForceSpawnX(), mainConfig.getForceSpawnZ()),
                            mainConfig.getForceSpawnZ());
                Location spawn = world.getSpawnLocation();
                for (int x = -5; x <= 5; x++)
                    for (int z = -5; z <= 5; z++)
                        spawn.clone().add(x * 16, 0, z * 16).getChunk().load();
                File mapConfig = new File(getDataFolder() + "/map.yml");
                YamlConfiguration mapConfiguration = YamlConfiguration.loadConfiguration(mapConfig);
                HungergamesApi.getGenerationManager().generateChunks();
                if (mapConfiguration.getBoolean("GenerateSpawnPlatform")) {
                    ItemStack spawnGround = mapConfiguration.getItemStack("SpawnPlatformBlock");
                    GenerationManager gen = HungergamesApi.getGenerationManager();
                    int platformHeight = gen.getSpawnHeight(world.getSpawnLocation(),
                            mapConfiguration.getInt("SpawnPlatformSize"));
                    gen.generatePlatform(world.getSpawnLocation(), platformHeight, mapConfiguration.getInt("SpawnPlatformSize"),
                            100, spawnGround.getTypeId(), spawnGround.getDurability());
                    world.getSpawnLocation().setY(platformHeight + 2);
                }
                world.setDifficulty(Difficulty.HARD);
                if (world.hasStorm())
                    world.setStorm(false);
                world.setWeatherDuration(999999999);
            }
        });
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            private long time = 0;

            public void run() {
                if (System.currentTimeMillis() >= time && doSeconds) {
                    time = System.currentTimeMillis() + 1000;
                    onSecond();
                    Bukkit.getPluginManager().callEvent(new TimeSecondEvent());
                }
            }
        }, 2L, 1L);
        HungergamesApi.getCommandManager();
        playerListener = new PlayerListener();
        Bukkit.getPluginManager().registerEvents(playerListener, this);
        Bukkit.getPluginManager().registerEvents(new GeneralListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        HungergamesApi.getAbilityManager();
        HungergamesApi.getInventoryManager().updateSpectatorHeads();
        if (Bukkit.getPluginManager().getPermission("ThisIsUsedForMessaging") == null) {
            Permission perm = new Permission("ThisIsUsedForMessaging", PermissionDefault.TRUE);
            perm.setDescription("Used for messages in LibsHungergames");
            Bukkit.getPluginManager().addPermission(perm);
        }
        DeathHandler.initialize(this);
    }

    private void onSecond() {
        currentTime++;
        if (currentTime < 0) {
            world.setTime(0);
            if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()) {
                ScoreboardManager.makeScore(DisplaySlot.SIDEBAR, translationsConfig.getScoreBoardGameStartingIn(), -currentTime);
            }
            if (mainConfig.isTeleportToSpawnLocationPregame() && -currentTime == mainConfig.getSecondsToTeleportPlayerToSpawn()) {
                for (Gamer gamer : HungergamesApi.getPlayerManager().getGamers()) {
                    HungergamesApi.getPlayerManager().sendToSpawn(gamer);
                    if (mainConfig.isPreventMovingFromSpawnUsingPotions()) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200), true);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200), true);
                        }
                    }
                }
            }
            if (mainConfig.isGameStarting(currentTime))
                Bukkit.broadcastMessage(String.format(translationsConfig.getBroadcastGameStartingIn(), returnTime(currentTime)));
        } else if (currentTime == 0) {
            if (pm.getGamers().size() < mainConfig.getMinPlayersForGameStart()) {
                currentTime = -90;
                if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()) {
                    ScoreboardManager.makeScore(DisplaySlot.SIDEBAR, translationsConfig.getScoreBoardGameStartingIn(),
                            -currentTime);
                }
                Bukkit.broadcastMessage(translationsConfig.getBroadcastNotEnoughPlayers());
            } else {
                startGame();
                return;
            }
        } else if (mainConfig.getAmountBorderClosesInPerSecond() > 0 && currentTime > mainConfig.getBorderStartsClosingIn()) {
            double borderSize = mainConfig.getBorderSize() - mainConfig.getAmountBorderClosesInPerSecond();
            if (borderSize < 0)
                borderSize = 0;
            mainConfig.setBorderSize(borderSize);
            if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()) {
                ScoreboardManager.makeScore(DisplaySlot.SIDEBAR, translationsConfig.getScoreboardBorderSize(),
                        (int) mainConfig.getBorderSize());
            }
        }
        for (Gamer gamer : pm.getGamers()) {
            this.doBorder(gamer);
        }
        if (mainConfig.getTimeForInvincibility() > 0 && currentTime <= mainConfig.getTimeForInvincibility() && currentTime >= 0) {
            if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()) {
                ScoreboardManager.makeScore(DisplaySlot.SIDEBAR, translationsConfig.getScoreboardInvincibleRemaining(),
                        mainConfig.getTimeForInvincibility() - currentTime);
            }
            if (currentTime == mainConfig.getTimeForInvincibility()) {
                Bukkit.broadcastMessage(translationsConfig.getBroadcastInvincibilityWornOff());
                if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()) {
                    ScoreboardManager.hideScore(DisplaySlot.SIDEBAR, translationsConfig.getScoreboardInvincibleRemaining());
                }
                Bukkit.getPluginManager().callEvent(new InvincibilityWearOffEvent());
            } else if (mainConfig.advertiseInvincibility(currentTime)) {
                Bukkit.broadcastMessage(String.format(translationsConfig.getBroadcastInvincibiltyWearsOffIn(),
                        returnTime(mainConfig.getTimeForInvincibility() - currentTime)));
            }
        }
        doStage();
    }

    public String returnTime(Integer i) {
        i = Math.abs(i);
        int remainder = i % 3600, minutes = remainder / 60, seconds = remainder % 60;
        if (seconds == 0 && minutes == 0)
            return translationsConfig.getTimeFormatNoTime();
        if (minutes == 0) {
            if (seconds == 1)
                return String.format(translationsConfig.getTimeFormatSecond(), seconds);
            return String.format(translationsConfig.getTimeFormatSeconds(), seconds);
        }
        if (seconds == 0) {
            if (minutes == 1)
                return String.format(translationsConfig.getTimeFormatMinute(), minutes);
            return String.format(translationsConfig.getTimeFormatMinutes(), minutes);
        }
        if (seconds == 1) {
            if (minutes == 1)
                return String.format(translationsConfig.getTimeFormatSecondAndMinute(), minutes, seconds);
            return String.format(translationsConfig.getTimeFormatSecondAndMinutes(), minutes, seconds);
        }
        if (minutes == 1) {
            return String.format(translationsConfig.getTimeFormatSecondsAndMinute(), minutes, seconds);
        }
        return String.format(translationsConfig.getTimeFormatSecondsAndMinutes(), minutes, seconds);
    }

    public void shutdown(final String messageToKickWith) {
        System.out.print(HungergamesApi.getConfigManager().getLoggerConfig().getShuttingDown());
        ServerShutdownEvent event = new ServerShutdownEvent();
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            for (String command : mainConfig.getCommandsToRunBeforeShutdown())
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.kickPlayer(messageToKickWith);
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), mainConfig.getCommandToStopTheServerWith());
                }
            }, 60);
        } else
            System.out.print(HungergamesApi.getConfigManager().getLoggerConfig().getShutdownCancelled());
    }

    public void startGame() {
        currentTime = 0;
        if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()) {
            ScoreboardManager.hideScore(DisplaySlot.SIDEBAR, translationsConfig.getScoreBoardGameStartingIn());
            ScoreboardManager.makeScore(DisplaySlot.PLAYER_LIST, "libraryaddict", 0);
        }
        if (mainConfig.getTimeForInvincibility() > 0) {
            if (HungergamesApi.getConfigManager().getMainConfig().isScoreboardEnabled()) {
                ScoreboardManager.makeScore(DisplaySlot.SIDEBAR, translationsConfig.getScoreboardInvincibleRemaining(),
                        mainConfig.getTimeForInvincibility());
            }
        } else {
            Bukkit.getPluginManager().callEvent(new InvincibilityWearOffEvent());
        }
        Bukkit.broadcastMessage(translationsConfig.getBroadcastGameStartedMessage());
        if (mainConfig.getTimeForInvincibility() > 0)
            Bukkit.broadcastMessage(String.format(translationsConfig.getBroadcastInvincibiltyWearsOffIn(),
                    returnTime(mainConfig.getTimeForInvincibility() - currentTime)));
        for (Gamer gamer : pm.getGamers()) {
            if (mainConfig.isKitSelectorEnabled()) {
                gamer.getPlayer().getInventory().remove(HungergamesApi.getInventoryManager().getKitSelector());
            }
            if (HungergamesApi.getConfigManager().getMySqlConfig().isBuyKitMenuEnabled()) {
                gamer.getPlayer().getInventory().remove(HungergamesApi.getInventoryManager().getBuyKit());
            }
            SpectatorManager.getInstance().deactiveSpectating(gamer);
            gamer.getPlayer().setFlying(false);
            gamer.getPlayer().setAllowFlight(false);
            if (mainConfig.isTeleportToSpawnLocationPregame() && mainConfig.isPreventMovingFromSpawnUsingPotions()) {
                gamer.getPlayer().removePotionEffect(PotionEffectType.SLOW);
                gamer.getPlayer().removePotionEffect(PotionEffectType.JUMP);
            }
            pm.sendToSpawn(gamer);
        }
        world.setGameRuleValue("doDaylightCycle", "true");
        world.setTime(mainConfig.getTimeOfDay());
        world.playSound(world.getSpawnLocation(), Sound.AMBIENCE_THUNDER, 1, 0.8F);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                for (Gamer gamer : pm.getAliveGamers())
                    gamer.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
                for (me.libraryaddict.Hungergames.Types.Kit kit : HungergamesApi.getKitManager().getKits())
                    kit.giveKit();
                HungergamesApi.getAbilityManager().registerAbilityListeners();
                Bukkit.getPluginManager().callEvent(new GameStartEvent());
                for (Location l : entitysToSpawn.keySet())
                    l.getWorld().spawnEntity(l, entitysToSpawn.get(l));
                entitysToSpawn.clear();
            }
        });
        checkWinner();
        HungergamesApi.getInventoryManager().updateSpectatorHeads();
        doStage();
    }

}
