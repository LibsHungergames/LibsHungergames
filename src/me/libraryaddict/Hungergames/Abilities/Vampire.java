package me.libraryaddict.Hungergames.Abilities;

import me.libraryaddict.Hungergames.Events.PlayerKilledEvent;
import me.libraryaddict.Hungergames.Types.AbilityListener;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class Vampire extends AbilityListener {
    public int healsFromAnimals = 3;
    public int healsFromMonsters = 5;
    public int healsFromPlayers = 6;
    public int surplusHealthRequiredForPotion = 5;

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null && hasAbility(event.getEntity().getKiller())) {
            Player p = event.getEntity().getKiller();
            if (event.getEntity() instanceof Creature) {
                int hp = p.getHealth();
                hp += (event.getEntity() instanceof Animals ? healsFromAnimals : healsFromMonsters);
                if (hp > 20) {
                    hp = 20;
                }
                p.setHealth(hp);
            }
        }
    }

    @EventHandler
    public void onKilled(PlayerKilledEvent event) {
        if (event.getKillerPlayer() != null && hasAbility(event.getKillerPlayer().getPlayer())) {
            int hp = event.getKillerPlayer().getPlayer().getHealth();
            hp += healsFromPlayers;
            if (hp - 20 >= surplusHealthRequiredForPotion)
                event.getDrops().add(new ItemStack(Material.POTION, 1, (short) 16421));
            if (hp > 20) {
                hp = 20;
            }
            event.getKillerPlayer().getPlayer().setHealth(20);
        }
    }

}
