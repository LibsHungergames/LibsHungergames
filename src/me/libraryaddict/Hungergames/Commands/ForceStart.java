package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Hungergames;

import me.libraryaddict.Hungergames.Configs.MainConfig;
import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ForceStart implements CommandExecutor {
    public String[] aliases = new String[] { "fstart" };
    private TranslationConfig cm = HungergamesApi.getConfigManager().getTranslationsConfig();
    public String description = "Change the time until the game starts";
    private Hungergames hg = HungergamesApi.getHungergames();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender.hasPermission("hungergames.forcestart")) {
            if (hg.currentTime >= 0) {
                sender.sendMessage(cm.getCommandForceStartAlreadyStarted());
                return true;
            }
            if (args.length > 0) {
                if (hg.isNumeric(args[0]) && Integer.parseInt(args[0]) > 0) {
                    int newTime = -Math.abs(Integer.parseInt(args[0]));
                    MainConfig config = HungergamesApi.getConfigManager().getMainConfig();
                    if (config.isTeleportToSpawnLocationPregame() && newTime >= -config.getSecondsToTeleportPlayerToSpawn()
                            && -hg.currentTime < config.getSecondsToTeleportPlayerToSpawn()) {
                        for (Gamer gamer : HungergamesApi.getPlayerManager().getGamers()) {
                            HungergamesApi.getPlayerManager().sendToSpawn(gamer);
                            if (config.isPreventMovingFromSpawnUsingPotions()) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200), true);
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200), true);
                                }
                            }
                        }
                    }
                    hg.currentTime = newTime;
                    Bukkit.broadcastMessage(String.format(cm.getCommandForceStartChangedCountdownTime(), sender.getName(),
                            hg.returnTime(hg.currentTime)));
                } else {
                    sender.sendMessage(String.format(cm.getCommandForceStartNotANumber(), args[0]));
                }
            } else {
                hg.startGame();
            }
        } else
            sender.sendMessage(cm.getCommandForceStartNoPermission());
        return true;
    }
}
