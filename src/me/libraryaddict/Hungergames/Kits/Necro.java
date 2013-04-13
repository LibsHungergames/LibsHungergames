package me.libraryaddict.Hungergames.Kits;

import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import me.libraryaddict.Hungergames.Types.Extender;

public class Necro extends Extender implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if ((event.getEntity() instanceof Skeleton || event.getEntity() instanceof Zombie) && event.getDamager() instanceof Player
                && kits.hasAbility((Player) event.getDamager(), "Necro"))
            event.setDamage(999);
    }
}
