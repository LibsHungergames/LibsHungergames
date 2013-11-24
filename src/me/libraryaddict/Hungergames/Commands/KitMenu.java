package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Types.HungergamesApi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitMenu implements CommandExecutor {
    public String description = "A command to toggle if the player should be able to build when he normally shouldn't";

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (HungergamesApi.getHungergames().currentTime < 0) {
            HungergamesApi.getInventoryManager().openKitInventory((Player) sender);
        } else {
            sender.sendMessage(HungergamesApi.getConfigManager().getTranslationsConfig().getCommandKitMenuGameInProgress());
        }
        return true;
    }
}
