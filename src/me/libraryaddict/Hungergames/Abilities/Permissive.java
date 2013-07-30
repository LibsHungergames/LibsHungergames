package me.libraryaddict.Hungergames.Abilities;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.permissions.PermissionAttachment;

import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Interfaces.Disableable;
import me.libraryaddict.Hungergames.Types.AbilityListener;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Kit;

public class Permissive extends AbilityListener implements Disableable {
    private HashMap<Player, PermissionAttachment> attachments = new HashMap<Player, PermissionAttachment>();
    public String[] kitPermissions = new String[] { "KitName Permission Permission Permission", "NoCheatBypass nocheat.bypass" };

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        for (Player player : getMyPlayers()) {
            Kit hisKit = HungergamesApi.getKitManager().getKitByPlayer(player);
            PermissionAttachment attachment = null;
            for (String string : kitPermissions) {
                String[] strings = string.split(" ");
                if (strings[0].equalsIgnoreCase(hisKit.getName())) {
                    if (attachment == null)
                        attachment = player.addAttachment(HungergamesApi.getHungergames());
                    for (int i = 1; i < strings.length; i++)
                        attachment.setPermission(strings[i], true);
                }
            }
            if (attachment != null)
                attachments.put(player, attachment);
        }
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        if (hasAbility(event.getKilled().getPlayer())) {
            event.getKilled().getPlayer().removeAttachment(attachments.remove(event.getKilled().getPlayer()));
        }
    }
}
