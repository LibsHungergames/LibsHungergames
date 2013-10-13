package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Managers.KitManager;

import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Respawn implements CommandExecutor {
    private TranslationConfig cm = HungergamesApi.getConfigManager().getTranslationsConfig();
    public String description = "Respawn a player after they have died";
    private PlayerManager pm = HungergamesApi.getPlayerManager();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender.hasPermission("hungergames.respawn")) {
            Gamer gamer = pm.getGamer(sender.getName());
            if (args.length > 0) {
                Player player = sender.getServer().getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(cm.getCommandRespawnPlayerDoesntExist());
                    return true;
                }
                gamer = pm.getGamer(player);
            }
            if (HungergamesApi.getHungergames().currentTime >= 0 && HungergamesApi.getHungergames().doSeconds) {
                if (!gamer.isAlive()) {
                    gamer.setAlive(true);
                    KitManager kits = HungergamesApi.getKitManager();
                    Player p = gamer.getPlayer();
                    p.setNoDamageTicks(200);
                    p.getInventory().addItem(new ItemStack(Material.COMPASS));
                    kits.setKit(p, kits.getKitByPlayer(p).getName());
                    kits.getKitByPlayer(p).giveKit(p);
                    p.sendMessage(cm.getCommandRespawnYouHaveBeenRespawned());
                    if (p != sender)
                        sender.sendMessage(String.format(cm.getCommandRespawnRespawnedPlayer(), gamer.getName()));
                } else
                    sender.sendMessage(cm.getCommandRespawnPlayerIsAlive());
            } else
                sender.sendMessage(cm.getCommandRespawnGameHasntStarted());
        } else
            sender.sendMessage(cm.getCommandRespawnNoPermission());
        return true;
    }
}
